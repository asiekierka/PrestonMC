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

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.Constants;
import pl.asie.preston.util.TileBase;

public class TileCompressedBlock extends TileBase {
	protected ItemStack containedStack = ItemStack.EMPTY;
	protected int level;
	private FakeBlockAccess access;

	public ItemStack getDroppedBlock() {
		return ItemCompressedBlock.setLevel(containedStack, level);
	}

	public void initFromStack(ItemStack stack) {
		containedStack = ItemCompressedBlock.getContained(stack);
		level = ItemCompressedBlock.getLevel(stack);
		access = null;

		IBlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 3);
	}

	public IBlockAccess getBlockAccess() {
		if (access == null) {
			access = new FakeBlockAccess(getWorld(), getPos(), containedStack);
		}
		return access;
	}

	public ItemStack getContainedStack() {
		return containedStack;
	}

	public int getLevel() {
		return level;
	}

	@Override
	public void readNBTData(NBTTagCompound compound, boolean isClient) {
		if (compound.hasKey("stack", Constants.NBT.TAG_COMPOUND)) {
			containedStack = new ItemStack(compound.getCompoundTag("stack"));
		} else {
			containedStack = ItemStack.EMPTY;
		}

		level = compound.getInteger("level");
		access = null;

		if (isClient) {
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	@Override
	public NBTTagCompound writeNBTData(NBTTagCompound compound, boolean isClient) {
		compound.setTag("stack", containedStack.serializeNBT());
		compound.setInteger("level", level);
		return compound;
	}
}
