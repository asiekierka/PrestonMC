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

package pl.asie.preston.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.model.TRSRTransformation;
import org.lwjgl.opengl.GL11;
import pl.asie.preston.container.ItemCompressedBlock;
import pl.asie.preston.machine.BlockCompressor;
import pl.asie.preston.machine.TileCompressor;

public class TileRendererCompressor extends TileEntitySpecialRenderer<TileCompressor> {
	public static IModel pistonHead;

	@Override
	public void render(TileCompressor tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (tile == null) {
			return;
		}

		IBlockState state = getWorld().getBlockState(tile.getPos());
		if (!(state.getBlock() instanceof BlockCompressor)) {
			return;
		}

		ItemStack stackProcessed = tile.getStackHandler().getStackInSlot(0);

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);

		GlStateManager.translate(0.5f, 0.5f, 0.5f);
		GlStateManager.rotate(180.0f - state.getValue(BlockCompressor.FACING).getHorizontalAngle(), 0, 1, 0);
		GlStateManager.translate(-0.5f, -0.5f, -0.5f);

		GlStateManager.enableAlpha();
		GlStateManager.enableLighting();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		if (pistonHead != null) {
			IBakedModel bakedModel = pistonHead.bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());

			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0.25f * ((255 - tile.armProgressClient) / 255.0f), 0);

			Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(
					bakedModel, 1.0f, 1.0f, 1.0f, 1.0f
			);

			GlStateManager.popMatrix();
		}

		if (!stackProcessed.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.rotate(90, 1, 0, 0);
			GlStateManager.translate(0.5, 0.5, -(7.25f/16f));
			if (tile.armProgressClient >= 128 && (stackProcessed.getItem() instanceof ItemBlock || stackProcessed.getItem() instanceof ItemCompressedBlock)) {
				GlStateManager.translate(0, 0, 0.25f * ((tile.armProgressClient - 128) / 255.0f));
			}
			GlStateManager.scale(0.5f, 0.5f, 0.5f);
			Minecraft.getMinecraft().getRenderItem().renderItem(stackProcessed, ItemCameraTransforms.TransformType.FIXED);
			GlStateManager.popMatrix();
		}

		GlStateManager.popMatrix();

		GlStateManager.disableBlend();
	}
}
