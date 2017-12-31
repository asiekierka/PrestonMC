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

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pl.asie.preston.container.ItemCompressedBlock;
import pl.asie.preston.recipe.RecipeCompress;
import pl.asie.preston.recipe.RecipeDecompress;

import java.rmi.registry.Registry;

@Mod(modid = PrestonMod.MODID, version = PrestonMod.VERSION)
public class PrestonMod {
    public static final String MODID = "preston";
    public static final String VERSION = "@VERSION@";
    public static int MAX_COMPRESSION_LEVELS;
    private static boolean ENABLE_COMPRESSION_BY_RECIPE, ENABLE_DECOMPRESSION_BY_RECIPE;

    @SidedProxy(clientSide = "pl.asie.preston.ProxyClient", serverSide = "pl.asie.preston.ProxyCommon", modId = MODID)
    public static ProxyCommon proxy;
	public static ItemCompressedBlock itemCompressedBlock;

	private Configuration config;

    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(MODID) {
        @Override
        public ItemStack getTabIconItem() {
            return ItemCompressedBlock.shiftLevel(new ItemStack(Blocks.COBBLESTONE), 1);
        }
    };

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());

        MAX_COMPRESSION_LEVELS = config.getInt("maxCompressionLevels", "balance", 16, 1, 1000, "The maximum amount of compression levels for each block.");
        ENABLE_COMPRESSION_BY_RECIPE = config.getBoolean("enableCompressionByRecipe", "balance", true, "Whether block compression by recipe should be enabled.");
        ENABLE_DECOMPRESSION_BY_RECIPE = config.getBoolean("enableDecompressionByRecipe", "balance", true, "Whether block decompression by recipe should be enabled.");

        if (config.hasChanged()) {
            config.save();
        }

        MinecraftForge.EVENT_BUS.register(this);
        proxy.preInit();
    }

    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> event) {
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
}
