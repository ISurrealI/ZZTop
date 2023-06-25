package mcjty.theoneprobe.capability.player;

import net.minecraft.nbt.NBTTagCompound;

public class PlayerFirstSpawn {

    private boolean alreadySpawned = false;

    public boolean isPlayerAlreadySpawned() {
        return alreadySpawned;
    }

    public void setAlreadySpawned(boolean playerGotNote) {
        this.alreadySpawned = playerGotNote;
    }

    public void copyFrom(PlayerFirstSpawn source) {
        alreadySpawned = source.alreadySpawned;
    }


    public void saveNBTData(NBTTagCompound compound) {
        compound.setBoolean("gotNote", alreadySpawned);
    }

    public void loadNBTData(NBTTagCompound compound) {
        alreadySpawned = compound.getBoolean("gotNote");
    }
}
