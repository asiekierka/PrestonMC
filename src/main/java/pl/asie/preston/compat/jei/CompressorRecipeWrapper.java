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
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import pl.asie.preston.PrestonMod;
import pl.asie.preston.api.ICompressorRecipe;

import java.util.Collections;
import java.util.List;

public class CompressorRecipeWrapper implements IRecipeWrapper {
	private final ICompressorRecipe recipe;
	private final List<ItemStack> input;
	private final ItemStack output;

	public CompressorRecipeWrapper(ICompressorRecipe recipe, ItemStack input, ItemStack output) {
		this.recipe = recipe;
		this.input = input != null ? Collections.singletonList(input) : recipe.getInputsForOutput(output);
		this.output = output != null ? output : recipe.getResult(input);
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		if (mouseX >= 36 && mouseX < (36+90) && mouseY >= 21 && mouseY < 28) {
			return Collections.singletonList(
				String.format("%s %s", recipe.getEnergyUsage(input.get(0)), PrestonMod.ENERGY_UNIT_NAME)
			);
		}
		return Collections.emptyList();
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, Collections.singletonList(input));
		ingredients.setOutput(ItemStack.class, output);
	}
}
