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

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import pl.asie.preston.machine.TileCompressor;
import pl.asie.preston.util.VeryLargeMachineEnergyStorage;

import java.util.Random;

public class PacketEmitParticlesOfHappiness extends PacketTile {
	private static final Random PARTICLE_RAND = new Random();

	private TileCompressor compressor;
	private int successLevel;

	public PacketEmitParticlesOfHappiness() {
		super();
	}

	public PacketEmitParticlesOfHappiness(TileCompressor tileEntity, int successLevel) {
		super(tileEntity);
		this.compressor = tileEntity;
		this.successLevel = successLevel;
	}

	@Override
	public void readData(INetHandler handler, PacketBuffer buf) {
		super.readData(handler, buf);
		successLevel = buf.readVarInt();
	}

	@Override
	public void writeData(PacketBuffer buf) {
		super.writeData(buf);
		buf.writeVarInt(successLevel);
	}

	@Override
	public void apply(INetHandler handler) {
		super.apply(handler);
		if (tile instanceof TileCompressor) {
			compressor = (TileCompressor) tile;
			if (compressor.getWorld().isSideSolid(compressor.getPos().up(), EnumFacing.DOWN, false)) {
				return;
			}

			int particleLimit = Minecraft.getMinecraft().gameSettings.particleSetting;
			int particleCount;

			if (particleLimit >= 2) {
				return;
			} else if (particleLimit == 1) {
				particleCount = 4 * (1 + successLevel);
				if (particleCount > 64) {
					particleCount = 64;
				}
			} else {
				particleCount = 4 * (int) Math.round(Math.pow(successLevel, 1.5f));
			}

			for (int i = 0; i < particleCount; i++) {
				compressor.getWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
						tile.getPos().getX() + 0.25 + (PARTICLE_RAND.nextDouble() * 0.5),
						tile.getPos().getY() + 1.025,
						tile.getPos().getZ() + 0.25 + (PARTICLE_RAND.nextDouble() * 0.5),
						(PARTICLE_RAND.nextFloat() * 0.05f) - 0.025f,
						(PARTICLE_RAND.nextFloat() * 0.05f) + 0.025f,
						(PARTICLE_RAND.nextFloat() * 0.05f) - 0.025f
				);
			}
		}
	}

	@Override
	public boolean isAsynchronous() {
		return false;
	}
}
