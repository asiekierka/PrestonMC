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
