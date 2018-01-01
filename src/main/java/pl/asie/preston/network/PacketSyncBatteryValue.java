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
import pl.asie.preston.machine.TileCompressor;
import pl.asie.preston.util.EnergySystem;
import pl.asie.preston.util.VeryLargeMachineEnergyStorage;

public class PacketSyncBatteryValue extends PacketTile {
	private TileCompressor compressor;
	private EnergySystem currentSystem;
	private VeryLargeMachineEnergyStorage storage;

	public PacketSyncBatteryValue() {
		super();
	}

	public PacketSyncBatteryValue(TileCompressor tileEntity) {
		super(tileEntity);
		this.compressor = tileEntity;
	}

	@Override
	public void readData(INetHandler handler, PacketBuffer buf) {
		super.readData(handler, buf);
		this.currentSystem = EnergySystem.values()[buf.readUnsignedByte()];
		this.storage = new VeryLargeMachineEnergyStorage();
		storage.deserializePacket(buf);
	}

	@Override
	public void apply(INetHandler handler) {
		super.apply(handler);
		if (tile instanceof TileCompressor) {
			compressor = (TileCompressor) tile;
			compressor.currentSystem = this.currentSystem;
			compressor.getStorage().copy(storage);
		}
	}

	@Override
	public void writeData(PacketBuffer buf) {
		super.writeData(buf);
		buf.writeByte(compressor.currentSystem.ordinal());
		compressor.getStorage().serializePacket(buf);
	}

	@Override
	public boolean isAsynchronous() {
		return false;
	}
}
