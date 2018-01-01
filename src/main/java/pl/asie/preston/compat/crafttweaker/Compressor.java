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

package pl.asie.preston.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import pl.asie.preston.api.ICompressorRecipe;
import pl.asie.preston.api.PrestonAPI;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@ZenRegister
@ZenClass("mods.preston.Compressor")
@ModOnly("preston")
public class Compressor {
	@ZenMethod
	public static void addRecipe(IItemStack output, long energy, IIngredient input) {
		final BigInteger energyBig = BigInteger.valueOf(energy);

		CraftTweakerAPI.apply(new AddRecipeAction(new ICompressorRecipe() {
			@Override
			public boolean matchesType(ItemStack stack) {
				return input.amount(1).matches(CraftTweakerMC.getIItemStack(stack).amount(1));
			}

			@Override
			public int getRequiredItemCount() {
				return input.getAmount();
			}

			@Override
			public BigInteger getEnergyUsage(ItemStack stack) {
				return energyBig;
			}

			@Override
			public ItemStack getResult(ItemStack stack) {
				return CraftTweakerMC.getItemStack(output).copy();
			}

			@Override
			public boolean matchesOutput(ItemStack stack) {
				return output.amount(1).matches(CraftTweakerMC.getIItemStack(stack).amount(1));
			}

			@Override
			public List<ItemStack> getInputsForOutput(ItemStack output) {
				return input.getItems().stream().map(CraftTweakerMC::getItemStack).map(ItemStack::copy).collect(Collectors.toList());
			}
		}, output));
	}

	public static class AddRecipeAction implements IAction {
		private final ICompressorRecipe recipe;
		private final IItemStack output;

		public AddRecipeAction(ICompressorRecipe recipe, IItemStack output) {
			this.recipe = recipe;
			this.output = output;
		}

		@Override
		public void apply() {
			PrestonAPI.registerRecipe(recipe);
		}

		@Override
		public String describe() {
			return "Adding compressor recipe for " + output;
		}
	}
}
