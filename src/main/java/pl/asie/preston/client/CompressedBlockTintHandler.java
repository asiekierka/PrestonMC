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
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import pl.asie.preston.container.ItemCompressedBlock;
import pl.asie.preston.container.TileCompressedBlock;

import javax.annotation.Nullable;

public class CompressedBlockTintHandler implements IBlockColor, IItemColor {
	@Override
	public int colorMultiplier(ItemStack stack, int tintIndex) {
		ItemStack contained = ItemCompressedBlock.getContained(stack);
		return Minecraft.getMinecraft().getItemColors().colorMultiplier(contained, tintIndex);
	}

	@Override
	public int colorMultiplier(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int tintIndex) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileCompressedBlock) {
			return Minecraft.getMinecraft().getItemColors().colorMultiplier(((TileCompressedBlock) tile).getContainedStack(), tintIndex);
		} else {
			return -1;
		}
	}
}
