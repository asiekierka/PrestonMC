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

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import pl.asie.preston.PrestonMod;
import pl.asie.preston.api.ICompressorRecipe;
import pl.asie.preston.container.ItemCompressedBlock;
import pl.asie.preston.network.PacketEmitParticlesOfHappiness;

import java.math.BigInteger;

public class CompressorRecipeCompress implements ICompressorRecipe {
	@Override
	public boolean matchesType(ItemStack stack) {
		return ItemCompressedBlock.canCompress(stack);
	}

	@Override
	public int getRequiredItemCount() {
		return 9;
	}

	@Override
	public BigInteger getEnergyUsage(ItemStack stack) {
		int targetLevel = ItemCompressedBlock.getLevel(stack) + 1;
		BigInteger value = BigInteger.valueOf(PrestonMod.ENERGY_MULTIPLIER);
		for (int j = 2; j <= targetLevel; j++) {
			value = value.multiply(BigInteger.valueOf(j));
		}
		return value;
	}

	@Override
	public ItemStack getResult(ItemStack stack) {
		return ItemCompressedBlock.shiftLevel(stack, 1);
	}

	@Override
	public void onCraftSuccess(IBlockAccess access, BlockPos pos, ItemStack crafted) {
		TileEntity tile = access.getTileEntity(pos);
		if (tile instanceof TileCompressor) {
			int compressedLevel = ItemCompressedBlock.getLevel(crafted);
			PrestonMod.packet.sendToWatching(new PacketEmitParticlesOfHappiness((TileCompressor) tile, compressedLevel), tile);
		}
	}
}
