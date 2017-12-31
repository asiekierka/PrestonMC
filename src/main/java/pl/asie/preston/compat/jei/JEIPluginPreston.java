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
		if (PrestonMod.blockCompressor != null) {
			registry.addRecipeCategories(new CompressorRecipeCategory());
		}
	}

	@Override
	public void register(@Nonnull IModRegistry registry) {
		guiHelper = registry.getJeiHelpers().getGuiHelper();

		registry.addRecipeRegistryPlugin(new CraftingCompressionRecipeRegistryPlugin());

		if (PrestonMod.blockCompressor != null) {
			registry.addRecipeCatalyst(new ItemStack(PrestonMod.blockCompressor), CompressorRecipeCategory.UID);
			registry.addRecipeRegistryPlugin(new CompressorRecipeRegistryPlugin());
		}
	}
}
