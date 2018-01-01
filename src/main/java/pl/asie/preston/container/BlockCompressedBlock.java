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
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
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
import java.util.function.Consumer;

public class BlockCompressedBlock extends BlockBase implements ITileEntityProvider {
	public static UnlistedPropertyGeneric<CBModelCacheKey> MODEL_CACHE_KEY = new UnlistedPropertyGeneric<>("key", CBModelCacheKey.class);

	public BlockCompressedBlock() {
		super(Material.ROCK);
		setCreativeTab(PrestonMod.CREATIVE_TAB);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
		return world.getBlockState(pos.offset(face)).getBlock() == this;
	}

	private IBlockAccess getBlockAccess(IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileCompressedBlock) {
			return ((TileCompressedBlock) tile).getBlockAccess();
		} else {
			return NullBlockAccess.INSTANCE;
		}
	}

	private int getLevel(IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileCompressedBlock) {
			return ((TileCompressedBlock) tile).getLevel();
		} else {
			return 1;
		}
	}

	/* BEGIN PROXYING MARATHON */

	@Override
	public float getSlipperiness(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable Entity entity) {
		IBlockAccess fakeAccess = getBlockAccess(world, pos);
		IBlockState fakeState = fakeAccess.getBlockState(pos);
		try {
			float s = fakeState.getBlock().getSlipperiness(fakeState, fakeAccess, pos, entity);
			float mul = (entity instanceof EntityLivingBase ? 2.2f : 2.5f);
			return (float) Math.pow((s - 0.6f) * mul, 1f / (getLevel(world, pos) + 1)) / mul + 0.6f;
		} catch (Throwable t) {
			return 0.6f;
		}
	}
/*
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		IBlockAccess fakeAccess = getBlockAccess(world, pos);
		IBlockState fakeState = fakeAccess.getBlockState(pos);
		try {
			int oValue = fakeState.getLightValue(fakeAccess, pos);
			if (oValue <= 0) {
				oValue = fakeState.getLightValue(fakeAccess, pos);
				return oValue;
			}

			int value = oValue + getLevel(world, pos);
			return value > 15 ? 15 : value;
		} catch (Throwable t) {
			return 0;
		}
	}
*/
	@Override
	public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
		IBlockAccess fakeAccess = getBlockAccess(world, pos);
		IBlockState fakeState = fakeAccess.getBlockState(pos);
		try {
			int value = fakeState.getLightValue(fakeAccess, pos) + getLevel(world, pos);
			value += getLevel(world, pos) * 128 / PrestonMod.MAX_COMPRESSION_LEVELS;
			return value > 255 ? 255 : value;
		} catch (Throwable t) {
			return 0;
		}
	}

	@Override
	public boolean isBeaconBase(IBlockAccess world, BlockPos pos, BlockPos beacon) {
		IBlockAccess fakeAccess = getBlockAccess(world, pos);
		IBlockState fakeState = fakeAccess.getBlockState(pos);
		try {
			return fakeState.getBlock().isBeaconBase(fakeAccess, pos, beacon);
		} catch (Throwable t) {
			return false;
		}
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		IBlockAccess fakeAccess = getBlockAccess(world, pos);
		IBlockState fakeState = fakeAccess.getBlockState(pos);
		try {
			return fakeState.getBlock().getFlammability(fakeAccess, pos, face) / getLevel(world, pos);
		} catch (Throwable t) {
			return 0;
		}
	}

	@Override
	public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
		IBlockAccess fakeAccess = getBlockAccess(world, pos);
		IBlockState fakeState = fakeAccess.getBlockState(pos);
		try {
			return fakeState.getBlock().isFlammable(fakeAccess, pos, face);
		} catch (Throwable t) {
			return false;
		}
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		IBlockAccess fakeAccess = getBlockAccess(world, pos);
		IBlockState fakeState = fakeAccess.getBlockState(pos);
		try {
			return fakeState.getBlock().getFireSpreadSpeed(fakeAccess, pos, face) / getLevel(world, pos);
		} catch (Throwable t) {
			return 0;
		}
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
		IBlockAccess fakeAccess = getBlockAccess(world, pos);
		IBlockState fakeState = fakeAccess.getBlockState(pos);
		try {
			return fakeState.getBlock().getExplosionResistance(exploder) * getLevel(world, pos);
		} catch (Throwable t) {
			return 0;
		}
	}

	@Override
	public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
		if (side != EnumFacing.UP) {
			return false;
		}
		IBlockAccess fakeAccess = getBlockAccess(world, pos);
		IBlockState fakeState = fakeAccess.getBlockState(pos);
		return fakeState.getBlock() == Blocks.NETHERRACK || fakeState.getBlock() == Blocks.MAGMA;
	}

	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
		IBlockAccess fakeAccess = getBlockAccess(world, pos);
		IBlockState fakeState = fakeAccess.getBlockState(pos);
		return fakeState.getBlock().getSoundType();
	}

	@Override
	public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileCompressedBlock) {
			float hardness = 2.0f;
			IBlockAccess access = ((TileCompressedBlock) tile).getBlockAccess();
			try {
				hardness = access.getBlockState(pos).getBlockHardness(world, pos);
			} catch (Throwable t) {

			}

			return hardness * (float) Math.sqrt(((TileCompressedBlock) tile).getLevel());
		} else {
			return 2.0f;
		}
	}

	/* END PROXYING MARATHON */

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
