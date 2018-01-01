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

package pl.asie.preston.container;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import pl.asie.preston.PrestonMod;
import pl.asie.preston.util.PrestonUtils;

import javax.annotation.Nullable;

public class ItemCompressedBlock extends ItemBlock {

	public ItemCompressedBlock(Block b) {
		super(b);
		setCreativeTab(PrestonMod.CREATIVE_TAB);
	}

	public static boolean canCompress(ItemStack stack) {
		if (stack.getItem() instanceof ItemCompressedBlock) {
			return getLevel(stack) < PrestonMod.MAX_COMPRESSION_LEVELS;
		} else {
			if (!PrestonMod.whitelistedItems.isEmpty()) {
				if (!PrestonMod.whitelistedItems.contains(stack.getItem())) {
					return false;
				}
			}

			if (PrestonMod.blacklistedItems.contains(stack.getItem())) {
				return false;
			}

			for (ItemStack other : PrestonMod.blacklistedItemStacks) {
				if (PrestonUtils.canMerge(stack, other)) {
					return false;
				}
			}

			IBlockState state = PrestonUtils.getBlockState(stack);
			if (state != null) {
				if (state.getMaterial() == Material.AIR) {
					return false;
				}

				if (state.isFullCube() || state.isFullBlock()) {
					return true;
				}

				try {
					if (state.getBoundingBox(new FakeBlockAccess(NullBlockAccess.INSTANCE, FakeBlockAccess.ACCESS_POS, state), FakeBlockAccess.ACCESS_POS).equals(Block.FULL_BLOCK_AABB)) {
						return true;
					}
				} catch (Exception e) {

				}

				return false;
			}
		}

		return false;
	}

	public static boolean canDecompress(ItemStack stack) {
		return stack.getItem() instanceof ItemCompressedBlock;
	}

	public static ItemStack getContained(ItemStack stack) {
		if (stack.getItem() instanceof ItemCompressedBlock) {
			return new ItemStack(PrestonUtils.getTagCompound(stack, true).getCompoundTag("stack"));
		} else {
			return ItemStack.EMPTY;
		}
	}

	public static int getLevel(ItemStack stack) {
		if (stack.getItem() instanceof ItemCompressedBlock) {
			return PrestonUtils.getTagCompound(stack, true).getInteger("level");
		} else {
			return 0;
		}
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			PrestonMod.proxy.getSubItems(tab, items);
		}
	}

	@Nullable
	public static ItemStack shiftLevel(ItemStack source, int amount) {
		return setLevel(source, getLevel(source) + amount);
	}

	@Nullable
	public static ItemStack setLevel(ItemStack source, int targetLevel) {
		int sourceLevel = getLevel(source);
		if (targetLevel < 0 || targetLevel > PrestonMod.MAX_COMPRESSION_LEVELS) {
			return null;
		} else if (targetLevel == sourceLevel) {
			return source.copy();
		} else if (targetLevel == 0) {
			return getContained(source);
		} else if (sourceLevel == 0) {
			ItemStack target = new ItemStack(PrestonMod.itemCompressedBlock, 1, 0);
			NBTTagCompound compound = PrestonUtils.getTagCompound(target, true);
			ItemStack sourceS = source;
			if (sourceS.getCount() > 1) {
				sourceS = sourceS.copy();
				sourceS.setCount(1);
			}

			compound.setTag("stack", sourceS.serializeNBT());
			compound.setInteger("level", targetLevel);
			return target;
		} else {
			ItemStack target = source.copy();
			target.setCount(1);
			NBTTagCompound compound = PrestonUtils.getTagCompound(target, true);
			compound.setInteger("level", targetLevel);
			return target;
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String level = "preston.tuple." + getLevel(stack);
		String name = getContained(stack).getDisplayName();
		return I18n.translateToLocalFormatted("item.preston.compressed_block.name", I18n.translateToLocal(level), name);
	}
}