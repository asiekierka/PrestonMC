/*
 * Copyright (c) 2015, 2016, 2017 Adrian Siekierka
 *
 * This file is part of Preston.
 *
 * Preston is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Preston is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Preston.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.asie.preston;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.asie.preston.api.ICompressorRecipe;
import pl.asie.preston.container.ItemCompressedBlock;
import pl.asie.preston.machine.BlockCompressor;
import pl.asie.preston.machine.CompressorRecipeCompress;
import pl.asie.preston.machine.ContainerCompressor;
import pl.asie.preston.machine.TileCompressor;
import pl.asie.preston.network.PacketEmitParticlesOfHappiness;
import pl.asie.preston.network.PacketRegistry;
import pl.asie.preston.network.PacketSyncBatteryValue;
import pl.asie.preston.network.PacketSyncHeadProgress;
import pl.asie.preston.recipe.RecipeCompress;
import pl.asie.preston.recipe.RecipeDecompress;

import java.rmi.registry.Registry;
import java.util.*;

@Mod(modid = PrestonMod.MODID, version = PrestonMod.VERSION)
public class PrestonMod {
    public static final String MODID = "preston";
    public static final String VERSION = "@VERSION@";
    public static int MAX_COMPRESSION_LEVELS;

    public static List<ICompressorRecipe> recipes = new ArrayList<>();
    public static List<ItemStack> blacklistedItemStacks = new ArrayList<>();
    public static Set<Item> whitelistedItems = Collections.newSetFromMap(new IdentityHashMap<Item, Boolean>());
    public static Set<Item> blacklistedItems = Collections.newSetFromMap(new IdentityHashMap<Item, Boolean>());

    public static Logger logger;

    public static String ENERGY_UNIT_NAME;
    public static int ENERGY_MULTIPLIER;
    private static boolean ENABLE_COMPRESSION_BY_RECIPE, ENABLE_DECOMPRESSION_BY_RECIPE, ENABLE_COMPRESSOR, ENABLE_COMPRESSION_BY_COMPRESSOR, BLACKLIST_AS_WHITELIST;

    @SidedProxy(clientSide = "pl.asie.preston.ProxyClient", serverSide = "pl.asie.preston.ProxyCommon", modId = MODID)
    public static ProxyCommon proxy;
    public static PacketRegistry packet;

    public static BlockCompressor blockCompressor;
    public static Item itemBlockCompressor;
	public static ItemCompressedBlock itemCompressedBlock;

	@Mod.Instance(MODID)
	public static PrestonMod instance;

	private Configuration config;

    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(MODID) {
        @Override
        public ItemStack getTabIconItem() {
            return ItemCompressedBlock.shiftLevel(new ItemStack(Blocks.COBBLESTONE), 1);
        }
    };

    @EventHandler
    public void onIMC(FMLInterModComms.IMCEvent event) {
        for (FMLInterModComms.IMCMessage message : event.getMessages()) {
            if ("blacklist".equals(message.key)) {
                if (message.isItemStackMessage()) {
                    ItemStack stack = message.getItemStackValue();
                    if (!stack.isEmpty()) {
                        if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                            blacklistedItems.add(stack.getItem());
                        } else {
                            blacklistedItemStacks.add(stack);
                        }
                    }
                } else if (message.isResourceLocationMessage()) {
                    blacklistedItems.add(Item.getByNameOrId(message.getResourceLocationValue().toString()));
                }
            } else if ("registerCompressorRecipe".equals(message.key)) {
                if (message.isStringMessage()) {
                    try {
                        Object o = Class.forName(message.getStringValue()).newInstance();
                        recipes.add((ICompressorRecipe) o);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        logger = LogManager.getLogger();

        ENERGY_UNIT_NAME = config.getString("energyUnitName", "general", "RF", "The name the mod uses to refer to energy.");
        MAX_COMPRESSION_LEVELS = config.getInt("maxCompressionLevels", "balance", 16, 1, 1000, "The maximum amount of compression levels for each block.");
        ENABLE_COMPRESSOR = config.getBoolean("enableCompressor", "features", true, "Enable or disable the Compressor machine.");
        ENABLE_COMPRESSION_BY_COMPRESSOR = config.getBoolean("enableCompressionByCompressor", "balance", true, "Whether block compression by the Compressor machine should be enabled.");
        ENABLE_COMPRESSION_BY_RECIPE = config.getBoolean("enableCompressionByRecipe", "balance", false, "Whether block compression by recipe should be enabled.");
        ENABLE_DECOMPRESSION_BY_RECIPE = config.getBoolean("enableDecompressionByRecipe", "balance", true, "Whether block decompression by recipe should be enabled.");
        ENERGY_MULTIPLIER = config.getInt("compressorBlockCompressionEnergyMultiplier", "balance", 100, 20, Integer.MAX_VALUE, "The energy multiplier for block compression in the compressor.");

        if (config.hasChanged()) {
            config.save();
        }

        if (ENABLE_COMPRESSION_BY_COMPRESSOR) {
            recipes.add(new CompressorRecipeCompress());
        }

        MinecraftForge.EVENT_BUS.register(this);
        proxy.preInit();

        NetworkRegistry.INSTANCE.registerGuiHandler(this, GuiHandlerPreston.INSTANCE);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        packet = new PacketRegistry(MODID);
        packet.registerPacket(0x01, PacketSyncBatteryValue.class);
        packet.registerPacket(0x02, PacketSyncHeadProgress.class);
        packet.registerPacket(0x03, PacketEmitParticlesOfHappiness.class);

        if (ENABLE_COMPRESSOR) {
            GameRegistry.registerTileEntity(TileCompressor.class, "preston:compressor");
        }

        String[] blacklistedItemsList = config.getStringList("itemBlacklist", "balance", new String[0], "Names of items to be blacklisted. IMC can also be used.");
        for (String s : blacklistedItemsList) {
            blacklistedItems.add(Item.getByNameOrId(s));
        }

        String[] whitelistedItemsList = config.getStringList("itemWhitelist", "balance", new String[0], "Names of items to be whitelisted. THE WHITELIST IS ONLY ACTIVE IF AT LEAST ONE ITEM IS ON THIS LIST.");
        for (String s : whitelistedItemsList) {
            whitelistedItems.add(Item.getByNameOrId(s));
        }

        if (config.hasChanged()) {
            config.save();
        }

        proxy.init();
    }

    @SubscribeEvent
    public void onRegisterBlocks(RegistryEvent.Register<Block> event) {
        if (ENABLE_COMPRESSOR) {
            event.getRegistry().register(blockCompressor = (BlockCompressor) new BlockCompressor().setRegistryName("preston:compressor"));
        }
    }

    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> event) {
        if (ENABLE_COMPRESSOR) {
            event.getRegistry().register(itemBlockCompressor = new ItemBlock(blockCompressor).setRegistryName("preston:compressor"));
        }

        event.getRegistry().register(itemCompressedBlock = (ItemCompressedBlock) new ItemCompressedBlock().setRegistryName("preston:compressed_block"));
    }

    @SubscribeEvent
    public void onRegisterRecipes(RegistryEvent.Register<IRecipe> event) {
        if (ENABLE_COMPRESSION_BY_RECIPE) {
            RecipeCompress recipeCompress = new RecipeCompress();
            recipeCompress.setRegistryName("preston:compress_block");
            event.getRegistry().register(recipeCompress);
        }

        if (ENABLE_DECOMPRESSION_BY_RECIPE) {
            RecipeDecompress recipeDecompress = new RecipeDecompress();
            recipeDecompress.setRegistryName("preston:decompress_block");
            event.getRegistry().register(recipeDecompress);
        }
    }

    @SubscribeEvent
    public void synchronizeBatteries(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.END) {
            Container c = event.player.openContainer;
            if (c instanceof ContainerCompressor) {
                long v = event.player.getEntityWorld().getTotalWorldTime() - ((ContainerCompressor) c).worldTimeStart;
                if ((v & 1) == 0) {
                    packet.sendTo(new PacketSyncBatteryValue(((ContainerCompressor) c).owner), event.player);
                }
            }
        }
    }

	public static boolean isFeatureEnabled(String ftr) {
        if ("compressor".equals(ftr)) {
            return ENABLE_COMPRESSOR;
        } else {
            return false;
        }
	}
}
