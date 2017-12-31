package pl.asie.preston.compat.jei;

import mezz.jei.api.recipe.*;
import net.minecraft.item.ItemStack;
import pl.asie.preston.PrestonMod;
import pl.asie.preston.container.ItemCompressedBlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class CraftingCompressionRecipeRegistryPlugin implements IRecipeRegistryPlugin {
	@Override
	public <V> List<String> getRecipeCategoryUids(IFocus<V> focus) {
		return Collections.singletonList(VanillaRecipeCategoryUid.CRAFTING);
	}

	@Override
	public <T extends IRecipeWrapper, V> List<T> getRecipeWrappers(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
		if (!(VanillaRecipeCategoryUid.CRAFTING.equals(recipeCategory.getUid()))) {
			return Collections.emptyList();
		}

		ItemStack inputStack = (ItemStack) focus.getValue();
		ItemStack stackPre = inputStack;
		ItemStack stackPost = inputStack;
		List<IRecipeWrapper> recipeWrappers = new ArrayList<>(2);

		if (focus.getMode() == IFocus.Mode.OUTPUT) {
			stackPre = ItemCompressedBlock.shiftLevel(inputStack, -1);
			stackPost = ItemCompressedBlock.shiftLevel(inputStack, 1);
		} else {
			if (!ItemCompressedBlock.canCompress(stackPre)) {
				stackPre = null;
			}

			if (!ItemCompressedBlock.canDecompress(stackPost)) {
				stackPost = null;
			}
		}

		if (stackPre != null && PrestonMod.recipeCompress != null) {
			recipeWrappers.add(new JEIRecipeCompressWrapper(stackPre));
		}

		if (stackPost != null && PrestonMod.recipeDecompress != null) {
			recipeWrappers.add(new JEIRecipeDecompressWrapper(stackPost));
		}

		return (List<T>) recipeWrappers;
	}

	@Override
	public <T extends IRecipeWrapper> List<T> getRecipeWrappers(IRecipeCategory<T> recipeCategory) {
		return Collections.emptyList();
	}
}
