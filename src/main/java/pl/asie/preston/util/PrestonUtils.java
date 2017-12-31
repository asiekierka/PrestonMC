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

package pl.asie.preston.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemHandlerHelper;

public final class PrestonUtils {
	private PrestonUtils() {

	}

	public static float clamp(float v, float a, float b) {
		if (v < a) return a;
		else if (v > b) return b;
		else return v;
	}

	public static NBTTagCompound getTagCompound(ItemStack stack, boolean create) {
		if (create && !stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		return stack.getTagCompound();
	}

	public static boolean canMerge(ItemStack source, ItemStack target) {
		return ItemHandlerHelper.canItemStacksStack(source, target);
	}

	public static IBlockState getBlockState(ItemStack stack) {
		if (stack.getItem() instanceof ItemBlock) {
			int m = stack.getMetadata();
			if (m >= 0 && m < 16) {
				try {
					return ((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
				} catch (Exception e) {
					return ((ItemBlock) stack.getItem()).getBlock().getDefaultState();
				}
			} else {
				return ((ItemBlock) stack.getItem()).getBlock().getDefaultState();
			}
		} else {
			Block block = Block.getBlockFromItem(stack.getItem());
			return block.getDefaultState();
		}
	}
}
