package mcjty.theoneprobe;

import mcjty.theoneprobe.config.ConfigSetup;
import mcjty.theoneprobe.capability.player.PlayerFirstSpawn;
import mcjty.theoneprobe.capability.player.PlayerProperties;
import mcjty.theoneprobe.capability.player.FirstSpawnCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ForgeEventHandlers {


    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        ConfigSetup.setupStyleConfig(ConfigSetup.mainConfig);
        ConfigSetup.updateDefaultOverlayStyle();

        if (ConfigSetup.mainConfig.hasChanged()) {
            ConfigSetup.mainConfig.save();
        }
    }

    @SubscribeEvent
    public void onEntityConstructing(AttachCapabilitiesEvent<Entity> event){
        if (event.getObject() instanceof EntityPlayer) {
            if (!event.getObject().hasCapability(PlayerProperties.PLAYER_ALREADY_SPAWNED, null)) {
                event.addCapability(new ResourceLocation(TheOneProbe.MODID, "Properties"), new FirstSpawnCapability());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            // We need to copyFrom the capabilities
            if (event.getOriginal().hasCapability(PlayerProperties.PLAYER_ALREADY_SPAWNED, null)) {
                PlayerFirstSpawn oldStore = event.getOriginal().getCapability(PlayerProperties.PLAYER_ALREADY_SPAWNED, null);
                PlayerFirstSpawn newStore = PlayerProperties.getPlayerGotNote(event.getEntityPlayer());
                newStore.copyFrom(oldStore);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (ConfigSetup.spawnNotification) {
            PlayerFirstSpawn note = PlayerProperties.getPlayerGotNote(event.player);
            if (!note.isPlayerAlreadySpawned()) {
                event.player.sendMessage(new TextComponentTranslation("top.notification"));
                note.setAlreadySpawned(true);
            }
        }
    }
}