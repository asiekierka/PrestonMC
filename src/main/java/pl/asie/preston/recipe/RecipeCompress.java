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

package pl.asie.preston.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import pl.asie.preston.util.PrestonUtils;
import pl.asie.preston.container.ItemCompressedBlock;

public class RecipeCompress extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		int stackCount = 0;
		for (int i = 0; i < inv.getWidth() * inv.getHeight(); i++) {
			ItemStack stack = inv.getStackInRowAndColumn(i % inv.getWidth(), i / inv.getWidth());
			if (!stack.isEmpty()) {
				stackCount++;
			}
		}

		if (stackCount != 9) {
			return false;
		}

		for (int yShift = 0; yShift <= inv.getHeight() - 3; yShift++) {
			for (int xShift = 0; xShift <= inv.getWidth() - 3; xShift++) {
				boolean found = true;
				ItemStack cmpStack = null;

				for (int i = 0; i < 9; i++) {
					ItemStack stack = inv.getStackInRowAndColumn(xShift + i % 3, yShift + i / 3);
					if (stack.isEmpty()) {
						found = false;
						break;
					} else if (!ItemCompressedBlock.canCompress(stack) || (cmpStack != null && !PrestonUtils.canMerge(cmpStack, stack))) {
						return false;
					} else {
						cmpStack = stack;
					}
				}

				if (found) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		for (int yShift = 0; yShift <= inv.getHeight() - 3; yShift++) {
			for (int xShift = 0; xShift <= inv.getWidth() - 3; xShift++) {
				ItemStack stack = inv.getStackInRowAndColumn(xShift, yShift);
				if (!stack.isEmpty()) {
					return ItemCompressedBlock.shiftLevel(stack, 1);
				}
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width >= 3 && height >= 3;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}
}
