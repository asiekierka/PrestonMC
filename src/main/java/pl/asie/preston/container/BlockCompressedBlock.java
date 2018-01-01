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

package pl.asie.preston.container;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.asie.preston.PrestonMod;
import pl.asie.preston.client.CBModelCacheKey;
import pl.asie.preston.util.BlockBase;
import pl.asie.preston.util.UnlistedPropertyGeneric;

import javax.annotation.Nullable;

public class BlockCompressedBlock extends BlockBase implements ITileEntityProvider {
	public static UnlistedPropertyGeneric<CBModelCacheKey> MODEL_CACHE_KEY = new UnlistedPropertyGeneric<>("key", CBModelCacheKey.class);

	public BlockCompressedBlock() {
		super(Material.ROCK);
		setCreativeTab(PrestonMod.CREATIVE_TAB);
	}

	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
		return world.getBlockState(pos.offset(face)).getBlock() == this;
	}

	@Override
	public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileCompressedBlock) {
			return 2.0f * (float) Math.sqrt(((TileCompressedBlock) tile).getLevel());
		} else {
			return 2.0f;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileCompressedBlock) {
			return ((TileCompressedBlock) tile).getDroppedBlock();
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, @Nullable TileEntity tile, int fortune, boolean silkTouch) {
		if (tile instanceof TileCompressedBlock) {
			drops.add(((TileCompressedBlock) tile).getDroppedBlock());
		}
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileCompressedBlock) {
			return ((IExtendedBlockState) state)
					.withProperty(MODEL_CACHE_KEY, new CBModelCacheKey((TileCompressedBlock) tile));
		} else {
			return state;
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileCompressedBlock) {
			((TileCompressedBlock) tile).initFromStack(stack);
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{MODEL_CACHE_KEY});
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileCompressedBlock();
	}
}
