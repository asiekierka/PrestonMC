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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import pl.asie.preston.PrestonMod;
import pl.asie.preston.api.ICompressorRecipe;
import pl.asie.preston.container.ItemCompressedBlock;
import pl.asie.preston.network.PacketEmitParticlesOfHappiness;
import pl.asie.preston.network.PacketSyncHeadProgress;
import pl.asie.preston.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class TileCompressor extends TileEntity implements ITickable {
	public int armProgressClient;
	public EnergySystem currentSystem = EnergySystem.FORGE;
	private final VeryLargeMachineEnergyStorage storage;
	private final ItemStackHandler stackHandler;
	private final IItemHandler viewTop, viewSide, viewBottom;
	private final List<EnergyWrapper> wrapperList;
	private boolean shouldShift;

	public TileCompressor() {
		this.storage = new VeryLargeMachineEnergyStorage() {
			@Override
			public int receiveEnergy(int maxReceive, boolean simulate) {
				int r = super.receiveEnergy(maxReceive, simulate);
				if (!simulate && r > 0) {
					setCurrentSystem(EnergySystem.FORGE);
				}
				return r;
			}
		};

		this.wrapperList = new ArrayList<>();
		for (EnergySystem system : EnergySystem.values()) {
			if (system != EnergySystem.FORGE && system.isEnabled() && system.canFunction()) {
				EnergyWrapper w = system.createWrapper(this::setCurrentSystem, storage);
				if (w != null) {
					wrapperList.add(w);
				}
			}
		}

		this.stackHandler = new ItemStackHandler(10) {
			@Override
			@Nonnull
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				if (slot == 9) return stack;
				else return super.insertItem(slot, stack, simulate);
			}

			@Override
			protected void onContentsChanged(int slot) {
				shouldShift = true;
			}
		};


		viewTop = new ItemHandlerProxy(stackHandler, 0, 9, true, false);
		viewSide = new ItemHandlerProxy(stackHandler, 0, 10, true, true) {
			@Override
			@Nonnull
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				if (slot < 9) return ItemStack.EMPTY;
				else return super.extractItem(slot, amount, simulate);
			}
		};
		viewBottom = new ItemHandlerProxy(stackHandler, 9, 1, false, true);
	}

	private void setCurrentSystem(EnergySystem system) {
		this.currentSystem = system;
	}

	public static ICompressorRecipe getMatchingRecipe(ItemStack stack) {
		if (!stack.isEmpty()) {
			for (ICompressorRecipe recipe : PrestonMod.recipes) {
				if (recipe.matchesType(stack)) {
					return recipe;
				}
			}
		}

		return null;
	}

	public VeryLargeMachineEnergyStorage getStorage() {
		return storage;
	}

	public IItemHandler getStackHandler() {
		return stackHandler;
	}

	public boolean hasDataPacket() {
		return true;
	}

	public void readNBTData(NBTTagCompound compound, boolean isClient) {
		if (compound.hasKey("inv", Constants.NBT.TAG_COMPOUND)) {
			stackHandler.deserializeNBT(compound.getCompoundTag("inv"));
		}

		if (compound.hasKey("energy", Constants.NBT.TAG_COMPOUND)) {
			storage.deserializeNBT(compound.getCompoundTag("energy"));
		}

		if (isClient && compound.hasKey("p", Constants.NBT.TAG_ANY_NUMERIC)) {
			armProgressClient = compound.getInteger("p");
		}
	}

	public NBTTagCompound writeNBTData(NBTTagCompound compound, boolean isClient) {
		compound.setTag("inv", stackHandler.serializeNBT());
		compound.setTag("energy", storage.serializeNBT());
		if (isClient) {
			compound.setInteger("p", armProgressClient);
		}
		return compound;
	}

	@Override
	public final SPacketUpdateTileEntity getUpdatePacket() {
		return hasDataPacket() ? new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), writeNBTData(new NBTTagCompound(), true)) : null;
	}

	@Override
	public final NBTTagCompound getUpdateTag() {
		NBTTagCompound compound = super.writeToNBT(new NBTTagCompound());
		compound = writeNBTData(compound, true);
		return compound;
	}

	@Override
	public final void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		if (pkt != null) {
			readNBTData(pkt.getNbtCompound(), true);
		}
	}

	@Override
	public final void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readNBTData(compound, world != null && world.isRemote);
	}

	@Override
	public final NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		if (shouldShift) {
			shiftInv();
		}
		return writeNBTData(compound, false);
	}

	@Override
	@Nullable
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		for (EnergyWrapper wrapper : wrapperList) {
			if (wrapper.hasCapability(capability, facing)) {
				return true;
			}
		}
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || (capability == CapabilityEnergy.ENERGY && EnergySystem.FORGE.isEnabled()) || super.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		for (EnergyWrapper wrapper : wrapperList) {
			T value = wrapper.getCapability(capability, facing);
			if (value != null) {
				return value;
			}
		}

		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == null) {
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(stackHandler);
			} else {
				switch (facing) {
					case UP:
						return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(viewTop);
					case DOWN:
						return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(viewBottom);
					default:
						return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(viewSide);
				}
			}
		} else if (capability == CapabilityEnergy.ENERGY && EnergySystem.FORGE.isEnabled()) {
			return CapabilityEnergy.ENERGY.cast(storage);
		} else {
			return super.getCapability(capability, facing);
		}
	}

	private void shiftInv() {
		if (stackHandler.getStackInSlot(0).isEmpty()) {
			for (int i = 1; i < 9; i++) {
				ItemStack stack = stackHandler.getStackInSlot(i);
				if (!stack.isEmpty()) {
					int maxCopy = 9 - i;
					for (int j = 0; j < 9; j++) {
						if (j < maxCopy) {
							stackHandler.setStackInSlot(j, stackHandler.getStackInSlot(i + j));
						} else {
							stackHandler.setStackInSlot(j, ItemStack.EMPTY);
						}
					}
					break;
				}
			}
		}
		shouldShift = false;
	}

	public void calcArmProgressClient(boolean canProcess) {
		int newApc = 0;

		if (canProcess) {
			if (storage.getCurrentMaxEnergy().compareTo(BigInteger.ZERO) > 0) {
				newApc = 128 + storage.getBigEnergyStored().multiply(BigInteger.valueOf(127)).divide(storage.getCurrentMaxEnergy()).intValueExact();
			}
		}

		if (newApc != armProgressClient) {
			armProgressClient = newApc;
			PrestonMod.packet.sendToWatching(new PacketSyncHeadProgress(this), this);
		}
	}

	@Override
	public void update() {
		if (world.isRemote) {
			return;
		}

		if (shouldShift || getWorld().getTotalWorldTime() % 10 == 0) {
			shiftInv();
		}

		ItemStack job = stackHandler.getStackInSlot(0);
		ICompressorRecipe recipe = getMatchingRecipe(job);

		if (recipe == null) {
			calcArmProgressClient(false);
			storage.setCurrentMaxEnergy(BigInteger.ZERO);
			return;
		}

		int availableStacks = job.getCount();

		for (int i = 1; i < 9 && availableStacks < 9; i++) {
			ItemStack stack = stackHandler.getStackInSlot(i);
			if (!stack.isEmpty() && recipe.canMerge(stack, job)) {
				availableStacks += stack.getCount();
			}
		}

		storage.setCurrentMaxEnergy(recipe.getEnergyUsage(job));
		if (availableStacks < recipe.getRequiredItemCount()) {
			calcArmProgressClient(false);
			return;
		}

		calcArmProgressClient(true);

		if (storage.getBigEnergyStored().compareTo(storage.getCurrentMaxEnergy()) >= 0) {
			// Compress
			ItemStack compressed = recipe.getResult(job);
			if (compressed != null) {
				boolean pushedIn = false;
				ItemStack targetSlot = stackHandler.getStackInSlot(9);
				if (targetSlot.isEmpty()) {
					stackHandler.setStackInSlot(9, compressed);
					pushedIn = true;
				} else if (PrestonUtils.canMerge(targetSlot, compressed) && targetSlot.getCount() < targetSlot.getMaxStackSize()) {
					targetSlot.grow(1);
					pushedIn = true;
				}

				if (pushedIn) {
					recipe.onCraftSuccess(getWorld(), getPos(), compressed);

					storage.setBigEnergyStored(storage.getBigEnergyStored().subtract(storage.getCurrentMaxEnergy()));
					// Remove used stacks
					int toRemove = 9;
					for (int i = 0; i < 9 && toRemove > 0; i++) {
						ItemStack stack = stackHandler.getStackInSlot(i);
						if (!stack.isEmpty() && (i == 0 || recipe.canMerge(stack, job))) {
							if (stack.getCount() > toRemove) {
								stack.shrink(toRemove);
								toRemove = 0;
							} else {
								stackHandler.setStackInSlot(i, ItemStack.EMPTY);
								toRemove -= stack.getCount();
								shouldShift = true;
							}
						}
					}
				}
			}
		}
	}
}
