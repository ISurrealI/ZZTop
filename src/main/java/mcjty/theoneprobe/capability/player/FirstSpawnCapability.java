package mcjty.theoneprobe.capability.player;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;

public class FirstSpawnCapability implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {

    private final PlayerFirstSpawn playerFirstSpawn = new PlayerFirstSpawn();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return capability == PlayerProperties.PLAYER_ALREADY_SPAWNED;
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (capability == PlayerProperties.PLAYER_ALREADY_SPAWNED) {
            // noinspection unchecked
            return (T) playerFirstSpawn;
        }

        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        playerFirstSpawn.saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        playerFirstSpawn.loadNBTData(nbt);
    }
}
