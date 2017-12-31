package pl.asie.preston.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import pl.asie.preston.container.ItemCompressedBlock;

public class CompressedBlockTintHandler implements IItemColor {
	@Override
	public int colorMultiplier(ItemStack stack, int tintIndex) {
		ItemStack contained = ItemCompressedBlock.getContained(stack);
		return Minecraft.getMinecraft().getItemColors().colorMultiplier(contained, tintIndex);
	}
}
