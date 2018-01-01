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

import buildcraft.api.mj.IMjConnector;
import buildcraft.api.mj.IMjReadable;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjAPI;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.function.Consumer;

public class MjEnergyStorageWrapper extends EnergyWrapper implements IMjReceiver, IMjReadable {
	private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);
	private final VeryLargeMachineEnergyStorage storage;
	private long remainder;

	public MjEnergyStorageWrapper(Consumer<EnergySystem> energySystemConsumer, VeryLargeMachineEnergyStorage storage) {
		super(energySystemConsumer);
		this.storage = storage;
	}

	private long asLong(BigInteger bigInteger) {
		BigInteger[] translated = EnergySystem.translate(bigInteger, EnergySystem.FORGE, EnergySystem.MJ);
		if (translated[0].compareTo(LONG_MAX) >= 0) {
			return Long.MAX_VALUE;
		} else {
			return translated[0].longValueExact();
		}
	}

	@Override
	public long getStored() {
		return asLong(storage.getBigEnergyStored());
	}

	@Override
	public long getCapacity() {
		return asLong(storage.getCurrentMaxEnergy());
	}

	@Override
	public boolean canConnect(@Nonnull IMjConnector other) {
		return true;
	}

	@Override
	public void update() {

	}

	@Override
	public long getPowerRequested() {
		return asLong(storage.getCurrentMaxEnergy().subtract(storage.getBigEnergyStored()));
	}

	@Override
	public long receivePower(long microJoules, boolean simulate) {
		long count = Math.min(getPowerRequested(), microJoules);

		if (!simulate) {
			BigInteger[] translated = EnergySystem.translate(BigInteger.valueOf(count), EnergySystem.MJ, EnergySystem.FORGE);
			storage.setBigEnergyStored(storage.getBigEnergyStored().add(translated[0]));
			energySystemConsumer.accept(EnergySystem.MJ);
			if (remainder > 0) {
				remainder += translated[1].longValueExact();
				translated = EnergySystem.translate(BigInteger.valueOf(remainder), EnergySystem.MJ, EnergySystem.FORGE);
				storage.setBigEnergyStored(storage.getBigEnergyStored().add(translated[0]));
				remainder = translated[1].longValueExact();
			} else {
				remainder += translated[1].longValueExact();
			}
		}

		return count;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == MjAPI.CAP_CONNECTOR || capability == MjAPI.CAP_READABLE || capability == MjAPI.CAP_RECEIVER;
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == MjAPI.CAP_CONNECTOR) {
			return MjAPI.CAP_CONNECTOR.cast(this);
		} else if (capability == MjAPI.CAP_RECEIVER) {
			return MjAPI.CAP_RECEIVER.cast(this);
		} else if (capability == MjAPI.CAP_READABLE) {
			return MjAPI.CAP_READABLE.cast(this);
		} else {
			return null;
		}
	}
}
