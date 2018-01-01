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

import mezz.jei.api.recipe.*;
import net.minecraft.item.ItemStack;
import pl.asie.preston.api.ICompressorRecipe;
import pl.asie.preston.api.PrestonAPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompressorRecipeRegistryPlugin implements IRecipeRegistryPlugin {
	@Override
	public <V> List<String> getRecipeCategoryUids(IFocus<V> focus) {
		if (!(focus.getValue() instanceof ItemStack))
			return Collections.emptyList();

		ItemStack stack = (ItemStack) focus.getValue();

		for (ICompressorRecipe recipe : PrestonAPI.getCompressorRecipes()) {
			if (recipe.matchesType(stack) || recipe.matchesOutput(stack)) {
				return Collections.singletonList(CompressorRecipeCategory.UID);
			}
		}

		return Collections.emptyList();
	}

	@Override
	public <T extends IRecipeWrapper, V> List<T> getRecipeWrappers(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
		if (!(CompressorRecipeCategory.UID.equals(recipeCategory.getUid()))) {
			return Collections.emptyList();
		}

		ItemStack stack = (ItemStack) focus.getValue();

		if (focus.getMode() == IFocus.Mode.INPUT) {
			List<IRecipeWrapper> wrappers = new ArrayList<>();
			for (ICompressorRecipe recipe : PrestonAPI.getCompressorRecipes()) {
				if (recipe.matchesType(stack)) {
					wrappers.add(new CompressorRecipeWrapper(recipe, null, recipe.getResult(stack)));
				}
			}
			return (List<T>) wrappers;
		} else if (focus.getMode() == IFocus.Mode.OUTPUT) {
			List<IRecipeWrapper> wrappers = new ArrayList<>();
			for (ICompressorRecipe recipe : PrestonAPI.getCompressorRecipes()) {
				if (recipe.matchesOutput(stack)) {
					wrappers.add(new CompressorRecipeWrapper(recipe, null, stack));
				}
			}
			return (List<T>) wrappers;
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public <T extends IRecipeWrapper> List<T> getRecipeWrappers(IRecipeCategory<T> recipeCategory) {
		if (!(CompressorRecipeCategory.UID.equals(recipeCategory.getUid()))) {
			return Collections.emptyList();
		}

		List<IRecipeWrapper> wrappers = new ArrayList<>();
		for (ICompressorRecipe recipe : PrestonAPI.getCompressorRecipes()) {
			for (ItemStack stack : recipe.getExampleInputs()) {
				wrappers.add(new CompressorRecipeWrapper(recipe, stack, null));
			}
		}
		return (List<T>) wrappers;
	}
}
