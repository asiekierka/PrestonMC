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

import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import pl.asie.preston.machine.GuiCompressor;

import java.util.List;

public class CompressorRecipeCategory implements IRecipeCategory<IRecipeWrapper> {
	public static final String UID = "preston:compressor";
	private IDrawable background, energyBar;
	private final String title;

	public CompressorRecipeCategory() {
		title = I18n.translateToLocal("tile.preston.compressor.name");
	}

	@Override
	public String getUid() {
		return UID;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getModName() {
		return "preston";
	}

	public IDrawable getEnergyBar() {
		if (energyBar == null) {
			energyBar = JEIPluginPreston.guiHelper.createDrawable(GuiCompressor.TEXTURE, 0, 150, 88, 5);
		}
		return energyBar;
	}

	@Override
	public IDrawable getBackground() {
		if (background == null) {
			background = JEIPluginPreston.guiHelper.createDrawable(GuiCompressor.TEXTURE, 7, 17, 162, 41);
		}
		return background;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(0, true, 0, 0);
		guiItemStacks.init(1, false, 8 * 18, 23);

		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);

		guiItemStacks.set(0, inputs.get(0));
		guiItemStacks.set(1, outputs.get(0));
	}
}
