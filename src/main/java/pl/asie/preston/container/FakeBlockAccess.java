package pl.asie.preston.container;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import pl.asie.preston.util.PrestonUtils;

import javax.annotation.Nullable;

public class FakeBlockAccess implements IBlockAccess {
	public static final BlockPos ACCESS_POS = BlockPos.ORIGIN;
	private final IBlockState state;
	private final BlockPos accessPos;
	private final IBlockAccess parent;
	private TileEntity tile;

	public FakeBlockAccess(IBlockAccess parent, BlockPos pos, IBlockState state) {
		this.parent = parent;
		this.accessPos = pos;
		this.state = state;
		this.tile = null;
	}

	public FakeBlockAccess(IBlockAccess parent, BlockPos pos, ItemStack stack) {
		this.parent = parent;
		this.state = PrestonUtils.getBlockState(stack);
		this.accessPos = pos;
		this.tile = null;
	}

	@Nullable
	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		return pos.equals(accessPos) ? tile : parent.getTileEntity(pos);
	}

	@Override
	public int getCombinedLight(BlockPos pos, int lightValue) {
		return parent.getCombinedLight(pos, lightValue);
	}

	@Override
	public IBlockState getBlockState(BlockPos pos) {
		return pos.equals(accessPos) ? state : parent.getBlockState(pos);
	}

	@Override
	public boolean isAirBlock(BlockPos pos) {
		return !pos.equals(accessPos) && parent.isAirBlock(pos);
	}

	@Override
	public Biome getBiome(BlockPos pos) {
		return parent.getBiome(pos);
	}

	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction) {
		return 0;
	}

	@Override
	public WorldType getWorldType() {
		return parent.getWorldType();
	}

	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
		return parent.isSideSolid(pos, side, _default);
	}
}
