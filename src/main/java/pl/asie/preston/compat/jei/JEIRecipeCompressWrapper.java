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

package pl.asie.preston.compat.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import pl.asie.preston.PrestonMod;
import pl.asie.preston.container.ItemCompressedBlock;

import java.util.ArrayList;
import java.util.List;

public class JEIRecipeCompressWrapper implements IShapedCraftingRecipeWrapper {
	private final ItemStack inputStack;

	@Override
	public ResourceLocation getRegistryName() {
		return PrestonMod.recipeCompress.getRegistryName();
	}

	@Override
	public int getWidth() {
		return PrestonMod.recipeCompress.getRecipeWidth();
	}

	@Override
	public int getHeight() {
		return PrestonMod.recipeCompress.getRecipeHeight();
	}

	public JEIRecipeCompressWrapper(ItemStack inputStack) {
		this.inputStack = inputStack;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ItemStack result = ItemCompressedBlock.shiftLevel(inputStack, 1);
		if (result != null) {
			List<ItemStack> inputs = new ArrayList<>(getWidth() * getHeight());
			for (int i = 0; i < getWidth() * getHeight(); i++) {
				inputs.add(inputStack);
			}

			ingredients.setInputs(ItemStack.class, inputs);
			ingredients.setOutput(ItemStack.class, result);
		}
	}
}
