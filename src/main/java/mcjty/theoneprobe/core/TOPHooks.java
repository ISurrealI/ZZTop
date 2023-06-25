package mcjty.theoneprobe.core;

import net.minecraft.nbt.NBTTagCompound;

public class TOPHooks {

    public static void setTag(NBTTagCompound tag, NBTTagCompound tileTag) {
        if (tileTag != null) {
            tag.setTag("EntityNBT", tileTag);
        }
    }
}
