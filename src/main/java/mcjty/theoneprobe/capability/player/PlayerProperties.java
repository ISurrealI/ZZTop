package mcjty.theoneprobe.capability.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class PlayerProperties {

    @CapabilityInject(PlayerFirstSpawn.class)
    public static Capability<PlayerFirstSpawn> PLAYER_ALREADY_SPAWNED;

    public static PlayerFirstSpawn getPlayerGotNote(EntityPlayer player) {
        return player.getCapability(PLAYER_ALREADY_SPAWNED, null);
    }


}
