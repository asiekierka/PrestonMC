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

import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import pl.asie.preston.PrestonMod;

import javax.annotation.Nonnull;

@JEIPlugin
public class JEIPluginPreston implements IModPlugin {
	public static IGuiHelper guiHelper;

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
		subtypeRegistry.useNbtForSubtypes(PrestonMod.itemCompressedBlock);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		if (PrestonMod.blockCompressor != null && PrestonMod.ENABLE_JEI_COMPRESSOR_SUPPORT) {
			registry.addRecipeCategories(new CompressorRecipeCategory());
		}
	}

	@Override
	public void register(@Nonnull IModRegistry registry) {
		guiHelper = registry.getJeiHelpers().getGuiHelper();

		if (PrestonMod.ENABLE_JEI_CRAFTING_SUPPORT) {
			registry.addRecipeRegistryPlugin(new CraftingCompressionRecipeRegistryPlugin());
		}

		if (PrestonMod.blockCompressor != null && PrestonMod.ENABLE_JEI_COMPRESSOR_SUPPORT) {
			registry.addRecipeCatalyst(new ItemStack(PrestonMod.blockCompressor), CompressorRecipeCategory.UID);
			registry.addRecipeRegistryPlugin(new CompressorRecipeRegistryPlugin());
		}
	}
}
