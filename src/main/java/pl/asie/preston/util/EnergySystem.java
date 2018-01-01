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

import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import pl.asie.preston.PrestonMod;

import java.math.BigInteger;
import java.util.function.Consumer;

public enum EnergySystem {
	FORGE(100),
	MJ(10000000);
	//EU(25);

	private final int defaultMultiplier;
	private int multiplier;
	private boolean enabled;

	EnergySystem(int defaultMultiplier) {
		this.defaultMultiplier = defaultMultiplier;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void updateConfig(Configuration config) {
		enabled = config.getBoolean("enableEnergy" + getNameCfgEntry(), "balance", true, "");
		multiplier = config.getInt("compressorRecipeMultiplier" + getNameCfgEntry(), "balance", defaultMultiplier, defaultMultiplier / 5, Integer.MAX_VALUE,
				this == MJ ? "Warning: MJ is specified in micro-joules (x10^6)!" : "");
	}

	private String getNameCfgEntry() {
		if (this == FORGE) {
			return "RF";
		} else {
			return this.name();
		}
	}

	public String getName() {
		if (this == FORGE) {
			return PrestonMod.ENERGY_UNIT_NAME;
		} else {
			return this.name();
		}
	}

	public static BigInteger[] translate(BigInteger value, EnergySystem from, EnergySystem to) {
		if (from == to) {
			return new BigInteger[] { value, BigInteger.ZERO };
		}

		BigInteger mul = BigInteger.valueOf(to.multiplier);
		BigInteger div = BigInteger.valueOf(from.multiplier);
		return value.multiply(mul).divideAndRemainder(div);
	}

	public String getTooltipEntry(BigInteger value, EnergySystem system) {
		BigInteger vTranslated = translate(value, FORGE, system)[0];
		if (system == MJ) {
			vTranslated = value.divide(BigInteger.valueOf(1000000));
		}
		
		return String.format("%s %s", vTranslated, system.getName());
	}

	public String getMeter(VeryLargeMachineEnergyStorage storage) {
		BigInteger first = storage.getBigEnergyStored();
		BigInteger second = storage.getCurrentMaxEnergy();

		BigInteger mul = BigInteger.valueOf(this.multiplier);
		BigInteger div = BigInteger.valueOf(FORGE.multiplier);

		if (this == MJ) {
			div = div.multiply(BigInteger.valueOf(1000000));
		}

		if (!mul.equals(div)) {
			first = first.multiply(mul).divide(div);
			second = second.multiply(mul).divide(div);
		}

		return I18n.translateToLocalFormatted("gui.preston.energy_bar", first.toString(), second.toString(), getName());
	}

	public EnergyWrapper createWrapper(Consumer<EnergySystem> energySystemConsumer, VeryLargeMachineEnergyStorage storage) {
		try {
			switch (this) {
				case MJ:
					return (EnergyWrapper) Class.forName("pl.asie.preston.util.MjEnergyStorageWrapper").getConstructor(Consumer.class, VeryLargeMachineEnergyStorage.class).newInstance(energySystemConsumer, storage);
				case FORGE:
				default:
					return null;
			/* case EU:
				return null; */
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean canFunction() {
		switch (this) {
			case MJ:
				return Loader.isModLoaded("buildcraftlib");
			case FORGE:
				return true;
			/* case EU:
				return false; */
			default:
				return false;
		}
	}
}
