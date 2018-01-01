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

import net.minecraft.item.ItemStack;
import pl.asie.preston.container.TileCompressedBlock;
import pl.asie.preston.util.PrestonUtils;
import pl.asie.preston.container.ItemCompressedBlock;

public class CBModelCacheKey {
	protected final ItemStack stack;
	protected final int count;

	public CBModelCacheKey(ItemStack stack, int count) {
		this.stack = stack;
		this.count = count;
	}

	public CBModelCacheKey(ItemStack stack) {
		this(ItemCompressedBlock.getContained(stack), ItemCompressedBlock.getLevel(stack));
	}

	public CBModelCacheKey(TileCompressedBlock tile) {
		this(tile.getContainedStack(), tile.getLevel());
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof CBModelCacheKey)) {
			return false;
		} else {
			CBModelCacheKey o = (CBModelCacheKey) other;
			return PrestonUtils.canMerge(o.stack, stack) && o.count == count;
		}
	}

	@Override
	public int hashCode() {
		return 31 * stack.getItem().hashCode() + 7 * stack.getItemDamage() + (stack.hasTagCompound() ? stack.getTagCompound().hashCode() : 1);
	}
}
