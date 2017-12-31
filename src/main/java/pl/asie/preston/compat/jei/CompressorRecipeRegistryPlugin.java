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
		return Collections.singletonList(CompressorRecipeCategory.UID);
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
					wrappers.add(new CompressorRecipeWrapper(recipe, stack, null));
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
