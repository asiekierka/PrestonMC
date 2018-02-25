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

package pl.asie.preston;

import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import pl.asie.preston.container.ItemCompressedBlock;
import pl.asie.preston.machine.ContainerCompressor;
import pl.asie.preston.machine.TileCompressor;

public class ProxyCommon implements IThreadListener {
	public void preInit() {
		GuiHandlerPreston.INSTANCE.register(GuiHandlerPreston.COMPRESSOR, Side.SERVER, (a) -> new ContainerCompressor((TileCompressor) a.getTileEntity(), a.player.inventory));
	}

	@Override
	public boolean isCallingFromMinecraftThread() {
		return FMLCommonHandler.instance().getMinecraftServerInstance().isCallingFromMinecraftThread();
	}

	public Object startProgressBar(String name, int steps) {
		PrestonMod.logger.info(name);
		return null;
	}

	public void stepProgressBar(Object o, String value) {

	}

	public void stopProgressBar(Object o) {

	}

	@Override
	public ListenableFuture<Object> addScheduledTask(Runnable runnable) {
		return FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(runnable);
	}

	public EntityPlayer getPlayer(INetHandler handler) {
		return handler instanceof NetHandlerPlayServer ? ((NetHandlerPlayServer) handler).player : null;
	}

	public World getLocalWorld(INetHandler handler, int dim) {
		return DimensionManager.getWorld(dim);
	}

	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		for (int i = 1; i <= PrestonMod.MAX_COMPRESSION_LEVELS; i++) {
			items.add(ItemCompressedBlock.setLevel(new ItemStack(Blocks.COBBLESTONE), i));
		}
	}

	public void init() {
	}
}
