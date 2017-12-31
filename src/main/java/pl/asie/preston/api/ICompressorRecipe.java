/*
 * Copyright (c) 2015, 2016, 2017 Adrian Siekierka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package pl.asie.preston.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

public interface ICompressorRecipe {
	boolean matchesType(ItemStack stack);
	int getRequiredItemCount();
	BigInteger getEnergyUsage(ItemStack stack);

	/**
	 * Only give one item as a result. Assume the available count
	 * is >= getRequiredItemCount().
	 */
	ItemStack getResult(ItemStack stack);

	default void onCraftSuccess(IBlockAccess access, BlockPos pos, ItemStack crafted) {

	}

	default boolean canMerge(ItemStack one, ItemStack two) {
		return ItemHandlerHelper.canItemStacksStack(one, two);
	}

	boolean matchesOutput(ItemStack stack);
	List<ItemStack> getInputsForOutput(ItemStack output);
	default List<ItemStack> getExampleInputs() {
		return Collections.emptyList();
	}
}
