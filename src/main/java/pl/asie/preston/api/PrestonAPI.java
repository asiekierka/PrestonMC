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
import pl.asie.preston.PrestonMod;
import pl.asie.preston.container.ItemCompressedBlock;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Don't bundle me. Or anything in here. Thanks!
 */
public class PrestonAPI {
	public static int getCompressionLevel(ItemStack stack) {
		return ItemCompressedBlock.getLevel(stack);
	}

	public static void registerRecipe(ICompressorRecipe recipe) {
		PrestonMod.recipes.add(recipe);
	}

	public static List<ICompressorRecipe> getCompressorRecipes() {
		return Collections.unmodifiableList(PrestonMod.recipes);
	}

	public static ItemStack getCompressedStack(ItemStack stack) {
		return ItemCompressedBlock.getContained(stack);
	}

	public static ItemStack setCompressionLevel(ItemStack stack, int level) {
		// Sanity checks
		if (level < 0) {
			return ItemStack.EMPTY;
		} else if (level == 0 && !ItemCompressedBlock.canDecompress(stack)) {
			return ItemStack.EMPTY;
		} else if (level > 0 && !ItemCompressedBlock.canCompress(stack)) {
			return ItemStack.EMPTY;
		}

		// Logic
		return ItemCompressedBlock.setLevel(stack, level);
	}
}
