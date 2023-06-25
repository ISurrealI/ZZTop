package mcjty.theoneprobe.apiimpl.providers;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.config.ConfigSetup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import static mcjty.theoneprobe.api.TextStyleClass.LABEL;

public class DefaultProbeInfoEntityProvider implements IProbeInfoEntityProvider {

    @Override
    public String getID() {
        return TheOneProbe.MODID + ":entity.default";
    }

    @Override
    public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {
        IProbeConfig config = ConfigSetup.getRealConfig();

        if (Tools.show(mode, config.getShowMobGrowth()) && entity instanceof EntityAgeable) {
            int age = ((EntityAgeable) entity).getGrowingAge();
            if (age < 0) {
                probeInfo.text(LABEL + "Growing time: " + ((age * -1) / 20) + "s");
            }
        }
    }
}
