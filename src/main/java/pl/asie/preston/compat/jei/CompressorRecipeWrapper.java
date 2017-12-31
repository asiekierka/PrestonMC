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
