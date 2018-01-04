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
import pl.asie.preston.PrestonMod;
import pl.asie.preston.container.ItemCompressedBlock;

public class RecipeDecompress extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		ItemStack foundStack = null;

		for (int i = 0; i < inv.getWidth() * inv.getHeight(); i++) {
			ItemStack stack = inv.getStackInRowAndColumn(i % inv.getWidth(), i / inv.getWidth());
			if (!stack.isEmpty()) {
				if (foundStack == null) {
					foundStack = stack;
					if (!ItemCompressedBlock.canDecompress(foundStack)) {
						return false;
					}
				} else {
					return false;
				}
			}
		}

		return foundStack != null;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		for (int i = 0; i < inv.getWidth() * inv.getHeight(); i++) {
			ItemStack stack = inv.getStackInRowAndColumn(i % inv.getWidth(), i / inv.getWidth());
			if (!stack.isEmpty()) {
				ItemStack stack1 = ItemCompressedBlock.shiftLevel(stack, -1);
				stack1.setCount(PrestonMod.COMPRESSED_BLOCK_AMOUNT);
				return stack1;
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canFit(int width, int height) {
		return (width * height) >= 1;
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
