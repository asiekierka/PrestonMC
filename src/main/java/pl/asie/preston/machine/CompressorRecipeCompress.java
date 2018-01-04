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

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.items.ItemStackHandler;
import pl.asie.preston.PrestonMod;
import pl.asie.preston.api.ICompressorRecipe;
import pl.asie.preston.container.ItemCompressedBlock;
import pl.asie.preston.network.PacketEmitParticlesOfHappiness;
import pl.asie.preston.util.EnergySystem;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompressorRecipeCompress implements ICompressorRecipe {
	private static final TIntObjectMap<BigInteger> compressionCostMap = new TIntObjectHashMap<>();

	@Override
	public boolean matchesType(ItemStack stack) {
		return ItemCompressedBlock.canCompress(stack);
	}

	@Override
	public int getRequiredItemCount() {
		return PrestonMod.COMPRESSED_BLOCK_AMOUNT;
	}

	@Override
	public BigInteger getEnergyUsage(ItemStack stack) {
		int targetLevel = ItemCompressedBlock.getLevel(stack) + 1;
		BigInteger value = compressionCostMap.get(targetLevel);
		if (value == null) {
			if (targetLevel <= 3) {
				int mul = EnergySystem.FORGE.getMultiplier();
				int minMul = mul > 10 ? 10 : mul;
				int v = mul >> (3 - targetLevel);
				if (v < minMul) v = minMul;
				value = BigInteger.valueOf(v);
			} else {
				value = BigInteger.valueOf(PrestonMod.MAX_COMPRESSION_LEVELS);
				value = value.pow(targetLevel - 3);
				value = value.multiply(BigInteger.valueOf(EnergySystem.FORGE.getMultiplier()));
			}
			compressionCostMap.put(targetLevel, value);
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

	@Override
	public boolean matchesOutput(ItemStack stack) {
		return ItemCompressedBlock.canDecompress(stack);
	}

	@Override
	public List<ItemStack> getInputsForOutput(ItemStack output) {
		ItemStack stack = ItemCompressedBlock.shiftLevel(output, -1);
		stack.setCount(PrestonMod.COMPRESSED_BLOCK_AMOUNT);
		return Collections.singletonList(stack);
	}

	private static List<ItemStack> stackListCache;
	public static void clearCache() {
		stackListCache = null;
	}

	@Override
	public List<ItemStack> getExampleInputs() {
		if (stackListCache != null) {
			return stackListCache;
		}

		stackListCache = new ArrayList<>();

		// TODO - Apparently JEI needs at least one here...
		stackListCache.add(new ItemStack(Blocks.STONE));

		/* for (Item i : Item.REGISTRY) {
			if (i instanceof ItemCompressedBlock) {
				continue;
			}

			NonNullList<ItemStack> list = NonNullList.create();
			i.getSubItems(CreativeTabs.SEARCH, list);
			for (ItemStack stack : list) {
				if (ItemCompressedBlock.canCompress(stack)) {
					stackListCache.add(stack);
				}
			}
		} */

		return stackListCache;
	}
}
