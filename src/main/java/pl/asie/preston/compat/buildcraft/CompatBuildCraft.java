package pl.asie.preston.compat.buildcraft;

import buildcraft.api.tiles.TilesAPI;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pl.asie.preston.machine.TileCompressor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CompatBuildCraft {
	@SubscribeEvent
	@Optional.Method(modid = "BuildCraftAPI|tiles")
	public void onAttachCapabilities(AttachCapabilitiesEvent<TileEntity> event) {
		if (event.getObject() instanceof TileCompressor) {
			final TileCompressor tile = (TileCompressor) event.getObject();
			event.addCapability(new ResourceLocation("preston:buildcraft_tiles"), new ICapabilityProvider() {
				@Override
				public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
					return capability == TilesAPI.CAP_HAS_WORK;
				}

				@Nullable
				@Override
				public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
					return capability == TilesAPI.CAP_HAS_WORK ? TilesAPI.CAP_HAS_WORK.cast(tile::hasWork) : null;
				}
			});
		}
	}
}
