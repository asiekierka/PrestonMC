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

package pl.asie.preston.network;

import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.preston.PrestonMod;

public abstract class PacketTile extends Packet {
	protected TileEntity tile;
	private int dim;
	private BlockPos pos;

	public PacketTile() {

	}

	public PacketTile(TileEntity tile) {
		this.tile = tile;
	}

	@Override
	public void readData(INetHandler handler, PacketBuffer buf) {
		dim = buf.readInt();
		pos = buf.readBlockPos();
	}

	@Override
	public void apply(INetHandler handler) {
		World w = PrestonMod.proxy.getLocalWorld(handler, dim);

		if (w != null && w.provider.getDimension() == dim) {
			tile = w.getTileEntity(pos);
		}
	}

	@Override
	public void writeData(PacketBuffer buf) {
		buf.writeInt(tile.getWorld().provider.getDimension());
		buf.writeBlockPos(tile.getPos());
	}
}
