package mcjty.theoneprobe.apiimpl.providers;

import mcjty.theoneprobe.config.ConfigSetup;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.LayoutStyle;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import static mcjty.theoneprobe.api.TextStyleClass.INFO;
import static mcjty.theoneprobe.api.TextStyleClass.LABEL;

public class DebugProbeInfoEntityProvider implements IProbeInfoEntityProvider {

    @Override
    public String getID() {
        return TheOneProbe.MODID + ":entity.debug";
    }

    @Override
    public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {
        if (mode == ProbeMode.DEBUG && ConfigSetup.showDebugInfo) {

            IProbeInfo vertical;

            if (entity instanceof EntityLivingBase) {

                vertical = probeInfo.vertical(new LayoutStyle().borderColor(0xffff4444).spacing(2));

                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                int totalArmorValue = entityLivingBase.getTotalArmorValue();
                int age = entityLivingBase.getIdleTime();
                float absorptionAmount = entityLivingBase.getAbsorptionAmount();
                float aiMoveSpeed = entityLivingBase.getAIMoveSpeed();
                int revengeTimer = entityLivingBase.getRevengeTimer();

                vertical
                        .text(LABEL + I18n.format("top.debug.entity.armor", "" + INFO + totalArmorValue))
                        .text(LABEL + I18n.format("top.debug.entity.age", "" + INFO + age))
                        .text(LABEL + I18n.format("top.debug.entity.absorption", "" + INFO + absorptionAmount))
                        .text(LABEL + I18n.format("top.debug.entity.move_speed", "" + INFO + aiMoveSpeed))
                        .text(LABEL + I18n.format("top.debug.entity.revenge_timer", "" + INFO + revengeTimer));

                if (entity instanceof EntityAgeable) {

                    EntityAgeable entityAgeable = (EntityAgeable) entity;
                    int growingAge = entityAgeable.getGrowingAge();
                    vertical
                            .text(LABEL + I18n.format("top.debug.entity.growing_age", "" + INFO + growingAge));
                }
            }

        }
    }
}
