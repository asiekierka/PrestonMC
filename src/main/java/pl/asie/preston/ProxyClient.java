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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pl.asie.preston.client.CompressedBlockBakedModel;
import pl.asie.preston.client.ResourceGenerator;
import pl.asie.preston.container.ItemCompressedBlock;

public class ProxyClient extends ProxyCommon {
	private static final ModelResourceLocation CB_MRL = new ModelResourceLocation("preston:compressed_block", "normal");

	public void preInit() {
		super.preInit();
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new ResourceGenerator());
	}

	@SubscribeEvent
	public void onRegisterModels(ModelRegistryEvent event) {
		ModelLoader.setCustomMeshDefinition(PrestonMod.itemCompressedBlock, stack -> CB_MRL);
		ModelLoader.registerItemVariants(PrestonMod.itemCompressedBlock, CB_MRL);
	}

	@SubscribeEvent
	public void onBakeModels(ModelBakeEvent event) {
		event.getModelRegistry().putObject(CB_MRL, new CompressedBlockBakedModel());
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		boolean hasAny = false;

		if (player != null) {
			for (int j = 0; j < 9; j++) {
				ItemStack source = player.inventory.getStackInSlot(j);
				if (!source.isEmpty() && ItemCompressedBlock.canCompress(source)) {
					hasAny = true;
					break;
				}
			}
		}

		if (!hasAny) {
			super.getSubItems(tab, items);
		} else {
			for (int i = 1; i <= PrestonMod.MAX_COMPRESSION_LEVELS; i++) {
				for (int j = 0; j < 9; j++) {
					ItemStack source = player.inventory.getStackInSlot(j);
					if (!source.isEmpty() && ItemCompressedBlock.canCompress(source)) {
						items.add(ItemCompressedBlock.setLevel(source, i));
					}
				}
			}
		}
	}
}
