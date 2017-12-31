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

package pl.asie.preston.machine;

import com.google.common.collect.Lists;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import pl.asie.preston.PrestonMod;
import pl.asie.preston.util.GuiContainerBase;
import pl.asie.preston.util.PrestonUtils;

import java.math.BigInteger;

public class GuiCompressor extends GuiContainerBase {
	public static final ResourceLocation TEXTURE = new ResourceLocation("preston:textures/gui/compressor.png");
	private final ContainerCompressor owner;

	public GuiCompressor(ContainerCompressor container) {
		super(container, 176, 150);
		this.owner = container;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String displayName = new ItemStack(PrestonMod.blockCompressor).getDisplayName();
		this.fontRenderer.drawString(displayName, this.xSize / 2 - this.fontRenderer.getStringWidth(displayName) / 2, 6, 4210752);
		this.fontRenderer.drawString(owner.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void renderHoveredToolTip(int mouseX, int mouseY) {
		super.renderHoveredToolTip(mouseX, mouseY);

		if (insideRect(mouseX, mouseY, xCenter + 44 - 1, yCenter + 38 - 1, 88 + 2, 5 + 2)) {
			this.drawHoveringText(Lists.newArrayList(
					I18n.translateToLocalFormatted("gui.preston.energy_bar", owner.owner.getStorage().getBigEnergyStored().toString(), owner.owner.getStorage().getCurrentMaxEnergy().toString(), PrestonMod.ENERGY_UNIT_NAME)
			), mouseX, mouseY, fontRenderer);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		super.drawGuiContainerBackgroundLayer(f, i, j);

		this.mc.getTextureManager().bindTexture(TEXTURE);
		this.drawTexturedModalRect(this.xCenter, this.yCenter, 0, 0, this.xSize, this.ySize);
		int length = (int) Math.floor(PrestonUtils.clamp((float) owner.owner.getStorage().getFilledAmount(), 0, 1) * 88);
		this.drawTexturedModalRect(this.xCenter + 44, this.yCenter + 39, 0, this.ySize, length, 5);
	}
}
