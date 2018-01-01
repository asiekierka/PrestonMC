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
import pl.asie.preston.PrestonMod;
import pl.asie.preston.api.ICompressorRecipe;
import pl.asie.preston.api.PrestonAPI;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@ZenRegister
@ZenClass("mods.preston.CompressedBlocks")
@ModOnly("preston")
public class CompressedBlocks {
	@ZenMethod
	public static IItemStack createStack(IItemStack from, int level) {
		ItemStack fromMc = CraftTweakerMC.getItemStack(from);
		ItemStack toMc = PrestonAPI.setCompressionLevel(fromMc, level);
		return CraftTweakerMC.getIItemStack(toMc == null ? ItemStack.EMPTY : toMc).amount(from.getAmount());
	}

	@ZenMethod
	public static int getMaximumCompressionLevel() {
		return PrestonMod.MAX_COMPRESSION_LEVELS;
	}
}
