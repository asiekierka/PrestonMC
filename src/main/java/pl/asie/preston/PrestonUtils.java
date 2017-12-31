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
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

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
		return equals(source, target, false, true, true);
	}

	public static boolean equals(ItemStack source, ItemStack target, boolean matchStackSize, boolean matchDamage, boolean matchNBT) {
		return equals(source, target, matchStackSize, matchDamage, matchNBT, matchNBT);
	}

	public static boolean equals(ItemStack source, ItemStack target, boolean matchStackSize, boolean matchDamage, boolean matchNBT, boolean matchCaps) {
		if (source == target) {
			return true;
		} else if (source.isEmpty()) {
			return target.isEmpty();
		} else {
			if (source.getItem() != target.getItem()) {
				return false;
			}

			if (matchStackSize && source.getCount() != target.getCount()) {
				return false;
			}

			if (matchDamage && source.getItemDamage() != target.getItemDamage()) {
				return false;
			}

			if (matchNBT) {
				if (source.hasTagCompound() != target.hasTagCompound()) {
					return false;
				} else if (source.hasTagCompound() && !source.getTagCompound().equals(target.getTagCompound())) {
					return false;
				}
			}

			if (matchCaps) {
				if (!source.areCapsCompatible(target)) {
					return false;
				}
			}

			return true;
		}
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
