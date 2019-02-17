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

package pl.asie.preston.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;

import java.math.BigInteger;

public class VeryLargeMachineEnergyStorage implements IEnergyStorage, INBTSerializable<NBTTagCompound> {
	private static final BigInteger MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);
	private BigInteger energy = BigInteger.ZERO, currentMaxEnergy = BigInteger.ZERO;

	public double getFilledAmount() {
		return currentMaxEnergy.compareTo(BigInteger.ZERO) == 0
		? (energy.compareTo(BigInteger.ZERO) > 0 ? 1.0 : 0.0)
		: ((double) energy.multiply(MAX_INT).divide(getCurrentMaxEnergy()).longValueExact() / Integer.MAX_VALUE);
	}

	public BigInteger getCurrentMaxEnergy() {
		if (currentMaxEnergy == null) {
			currentMaxEnergy = energy;
		}
		return currentMaxEnergy;
	}

	public void setCurrentMaxEnergy(BigInteger value) {
		this.currentMaxEnergy = value;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		BigInteger maxEnergyAccept = getCurrentMaxEnergy().subtract(energy);
		maxEnergyAccept = maxEnergyAccept.compareTo(MAX_INT) > 0 ? MAX_INT : maxEnergyAccept;
		if (getCurrentMaxEnergy().compareTo(MAX_INT) < 0) {
			maxReceive = Math.min(maxEnergyAccept.intValueExact(), maxReceive);
		}

		if (!simulate) {
			energy = energy.add(BigInteger.valueOf(maxReceive));
		}

		return maxReceive;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored() {
		if (energy.compareTo(MAX_INT) > 0) {
			return Integer.MAX_VALUE;
		} else {
			return energy.intValueExact();
		}
	}

	@Override
	public int getMaxEnergyStored() {
		if (getCurrentMaxEnergy().compareTo(MAX_INT) > 0) {
			return Integer.MAX_VALUE;
		} else {
			return getCurrentMaxEnergy().intValueExact();
		}
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	public boolean canReceive() {
		return true;
	}

	private void serializeBigInt(BigInteger bigInteger, PacketBuffer buf) {
		byte[] data = bigInteger.toByteArray();
		buf.writeVarInt(data.length);
		buf.writeBytes(data);
	}

	private void serializeBigInt(BigInteger bigInteger, NBTTagCompound compound, String name) {
		compound.setByteArray(name, bigInteger.toByteArray());
	}

	private BigInteger deserializeBigInt(PacketBuffer buf) {
		int length = buf.readVarInt();
		byte[] data = new byte[length];
		buf.readBytes(data);
		return new BigInteger(data);
	}

	private BigInteger deserializeBigInt(NBTTagCompound nbt, String name) {
		if (nbt.hasKey(name, Constants.NBT.TAG_BYTE_ARRAY)) {
			return new BigInteger(nbt.getByteArray(name));
		} else {
			return BigInteger.ZERO;
		}
	}

	public void serializePacket(PacketBuffer buf) {
		serializeBigInt(energy, buf);
		serializeBigInt(getCurrentMaxEnergy(), buf);
	}

	public void deserializePacket(PacketBuffer buf) {
		energy = deserializeBigInt(buf);
		setCurrentMaxEnergy(deserializeBigInt(buf));
	}

	public void copy(VeryLargeMachineEnergyStorage other) {
		energy = other.energy;
		currentMaxEnergy = other.currentMaxEnergy;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		serializeBigInt(energy, compound, "energy");
		serializeBigInt(getCurrentMaxEnergy(), compound, "maxEnergy");
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		energy = deserializeBigInt(nbt, "energy");
		setCurrentMaxEnergy(deserializeBigInt(nbt, "maxEnergy"));
	}

	public BigInteger getBigEnergyStored() {
		return energy;
	}

	public void setBigEnergyStored(BigInteger value) {
		energy = value;
	}
}
