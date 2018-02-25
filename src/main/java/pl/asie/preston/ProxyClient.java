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

import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.ItemHandlerHelper;
import pl.asie.preston.client.CompressedBlockBakedModel;
import pl.asie.preston.client.CompressedBlockTintHandler;
import pl.asie.preston.client.ResourceGenerator;
import pl.asie.preston.client.TileRendererCompressor;
import pl.asie.preston.container.ItemCompressedBlock;
import pl.asie.preston.machine.CompressorRecipeCompress;
import pl.asie.preston.machine.ContainerCompressor;
import pl.asie.preston.machine.GuiCompressor;
import pl.asie.preston.machine.TileCompressor;

import java.util.ArrayList;
import java.util.List;

public class ProxyClient extends ProxyCommon {
	private static final ModelResourceLocation CB_MRL = new ModelResourceLocation("preston:compressed_block", "normal");

	@Override
	public void preInit() {
		super.preInit();
		GuiHandlerPreston.INSTANCE.register(GuiHandlerPreston.COMPRESSOR, Side.CLIENT, (a) -> new GuiCompressor((ContainerCompressor) a.getContainer()));
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new ResourceGenerator());
	}

	@Override
	public void init() {
		super.init();
		ClientRegistry.bindTileEntitySpecialRenderer(TileCompressor.class, new TileRendererCompressor());
	}

	@Override
	public Object startProgressBar(String name, int steps) {
		PrestonMod.logger.info(name);
		return ProgressManager.push(name, steps);
	}

	@Override
	public void stepProgressBar(Object o, String value) {
		((ProgressManager.ProgressBar) o).step(value);
	}

	@Override
	public void stopProgressBar(Object o) {
		ProgressManager.pop((ProgressManager.ProgressBar) o);
	}

	public static IModel getModel(ResourceLocation location) {
		try {
			return ModelLoaderRegistry.getModel(location);
		} catch (Exception e) {
			PrestonMod.logger.error("Model " + location.toString() + " is missing! THIS WILL CAUSE A CRASH!");
			e.printStackTrace();
			return null;
		}
	}

	public static IModel getModelWithTextures(ResourceLocation location, TextureMap map) {
		IModel model = getModel(location);
		if (model != null) {
			for (ResourceLocation tlocation : model.getTextures()) {
				map.registerSprite(tlocation);
			}
		}
		return model;
	}

	@Override
	public EntityPlayer getPlayer(INetHandler handler) {
		return (handler instanceof INetHandlerPlayClient || handler instanceof INetHandlerLoginClient)
				? Minecraft.getMinecraft().player : super.getPlayer(handler);
	}

	@Override
	public boolean isCallingFromMinecraftThread() {
		return Minecraft.getMinecraft().isCallingFromMinecraftThread();
	}

	@Override
	public ListenableFuture<Object> addScheduledTask(Runnable runnable) {
		return Minecraft.getMinecraft().addScheduledTask(runnable);
	}

	@Override
	public World getLocalWorld(INetHandler handler, int dim) {
		if (handler instanceof INetHandlerPlayClient || handler instanceof INetHandlerLoginClient) {
			World w = getPlayer(handler).world;
			if (w.provider.getDimension() == dim) {
				return w;
			} else {
				return null;
			}
		} else {
			return super.getLocalWorld(handler, dim);
		}
	}

	@SubscribeEvent
	public void onTextureStitchPre(TextureStitchEvent.Pre event) {
		if (PrestonMod.blockCompressor != null) {
			CompressorRecipeCompress.clearCache();
			TileRendererCompressor.pistonHead = getModelWithTextures(new ResourceLocation("preston", "block/compressor_piston_head"), event.getMap());
		}
	}

	@SubscribeEvent
	public void onRegisterModels(ModelRegistryEvent event) {
		if (PrestonMod.itemBlockCompressor != null) {
			ModelLoader.setCustomModelResourceLocation(PrestonMod.itemBlockCompressor, 0, new ModelResourceLocation("preston:compressor", "inventory"));
		}
		ModelLoader.setCustomMeshDefinition(PrestonMod.itemCompressedBlock, stack -> CB_MRL);
		ModelLoader.registerItemVariants(PrestonMod.itemCompressedBlock, CB_MRL);
	}

	@SubscribeEvent
	public void onBakeModels(ModelBakeEvent event) {
		event.getModelRegistry().putObject(CB_MRL, new CompressedBlockBakedModel());
	}

	@SubscribeEvent
	public void onColorHandlerBlockRegiser(ColorHandlerEvent.Block event) {
		event.getBlockColors().registerBlockColorHandler(new CompressedBlockTintHandler(), PrestonMod.blockCompressedBlock);
	}

	@SubscribeEvent
	public void onColorHandlerItemRegiser(ColorHandlerEvent.Item event) {
		event.getItemColors().registerItemColorHandler(new CompressedBlockTintHandler(), PrestonMod.itemCompressedBlock);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		List<ItemStack> stackList = new ArrayList<>();

		if (player != null) {
			for (int j = 0; j < 9; j++) {
				ItemStack source = player.inventory.getStackInSlot(j);
				if (!source.isEmpty() && ItemCompressedBlock.canCompress(source)) {
					ItemStack sourceChk = ItemCompressedBlock.setLevel(source, 0);
					boolean shouldAdd = true;
					for (int i = 0; i < stackList.size(); i++) {
						if (ItemHandlerHelper.canItemStacksStack(stackList.get(i), sourceChk)) {
							shouldAdd = false;
							break;
						}
					}
					if (shouldAdd) {
						stackList.add(sourceChk);
					}
				}
			}
		}

		if (stackList.isEmpty()) {
			super.getSubItems(tab, items);
		} else {
			for (int i = 1; i <= PrestonMod.MAX_COMPRESSION_LEVELS; i++) {
				for (ItemStack source : stackList) {
					if (!source.isEmpty() && ItemCompressedBlock.canCompress(source)) {
						items.add(ItemCompressedBlock.setLevel(source, i));
					}
				}
			}
		}
	}
}
