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

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.SlotItemHandler;
import pl.asie.preston.util.ContainerBase;

public class ContainerCompressor extends ContainerBase {
	public final long worldTimeStart;
	public final TileCompressor owner;

	public ContainerCompressor(TileCompressor owner, InventoryPlayer inventoryPlayer) {
		super(inventoryPlayer);
		this.owner = owner;
		this.worldTimeStart = owner.getWorld().getTotalWorldTime();

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new SlotItemHandler(this.owner.getStackHandler(), i, 8 + i*18, 18));
		}
		addSlotToContainer(new SlotItemHandler(this.owner.getStackHandler(), 9, 8 + 8*18, 41));
		bindPlayerInventory(inventoryPlayer, 8, 68);
	}
}
