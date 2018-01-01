package pl.asie.preston.compat.hwyla;

import mcp.mobius.waila.api.ITaggedList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import pl.asie.preston.machine.TileCompressor;

import javax.annotation.Nonnull;
import java.util.List;

public class HUDHandlerCompressor implements IWailaDataProvider {
	@Nonnull
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if (!config.getConfig("preston.compressor") || !(accessor.getTileEntity() instanceof TileCompressor)) {
			return currenttip;
		}

		if (accessor.getNBTData().hasKey("preston:compressor", Constants.NBT.TAG_COMPOUND)) {
			TileCompressor compressor = ((TileCompressor) accessor.getTileEntity());
			compressor.readNBTData(accessor.getNBTData().getCompoundTag("preston:compressor"), true);

			// override the previous tag???
			((ITaggedList<String, String>) currenttip).add(compressor.currentSystem.getMeter(compressor.getStorage()), "IEnergyStorage");
			return currenttip;
		}

		return currenttip;
	}

	@Nonnull
	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		if (te != null && te instanceof TileCompressor) {
			TileCompressor compressor = (TileCompressor) te;
			tag.setTag("preston:compressor", compressor.serializeNBTTooltip());
		}

		return tag;
	}
}
