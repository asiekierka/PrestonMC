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

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class ItemHandlerProxy implements IItemHandler {
	private final IItemHandler base;
	private final int start, length;
	private final boolean allowInsertion, allowExtraction;

	public ItemHandlerProxy(IItemHandler base, int start, int length, boolean allowInsertion, boolean allowExtraction) {
		this.base = base;
		this.start = start;
		this.length = length;
		this.allowInsertion = allowInsertion;
		this.allowExtraction = allowExtraction;
	}

	@Override
	public int getSlots() {
		return length;
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot) {
		if (slot >= start && slot < start+length) {
			return base.getStackInSlot(slot + start);
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (slot >= 0 && slot < length && allowInsertion) {
			return base.insertItem(slot + start, stack, simulate);
		} else {
			return stack;
		}
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (slot >= 0 && slot < length && allowExtraction) {
			return base.extractItem(slot + start, amount, simulate);
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public int getSlotLimit(int slot) {
		if (slot >= start && slot < start+length) {
			return base.getSlotLimit(slot + start);
		} else {
			return 0;
		}
	}
}
