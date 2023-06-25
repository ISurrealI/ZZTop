package mcjty.theoneprobe.setup;

import mcjty.theoneprobe.ForgeEventHandlers;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.apiimpl.TheOneProbeImp;
import mcjty.theoneprobe.apiimpl.providers.*;
import mcjty.theoneprobe.config.ConfigSetup;
import mcjty.theoneprobe.network.PacketHandler;
import mcjty.theoneprobe.capability.player.PlayerFirstSpawn;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class ModSetup {

    private Logger logger;
    public static File modConfigDir;

    public static boolean baubles = false;
    public static boolean tesla = false;
    public static boolean redstoneflux = false;

    public void preInit(FMLPreInitializationEvent e) {
        logger = e.getModLog();

        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());

        registerCapabilities();
        TheOneProbeImp.registerElements();

        modConfigDir = e.getModConfigurationDirectory();
        ConfigSetup.init();

        TheOneProbe.theOneProbeImp.registerProvider(new DefaultProbeInfoProvider());
        TheOneProbe.theOneProbeImp.registerProvider(new BlockProbeInfoProvider());
        TheOneProbe.theOneProbeImp.registerEntityProvider(new DefaultClientProbeInfoEntityProvider());
        TheOneProbe.theOneProbeImp.registerEntityProvider(new EntityProbeInfoEntityProvider());
        TheOneProbe.theOneProbeImp.registerProvider(new DefaultProbeInfoTileProvider());
        TheOneProbe.theOneProbeImp.registerEntityProvider(new DefaultProbeInfoEntityProvider());

        TheOneProbe.theOneProbeImp.registerProvider(new DebugProbeInfoProvider());
        TheOneProbe.theOneProbeImp.registerEntityProvider(new DebugProbeInfoEntityProvider());


        PacketHandler.registerMessages("theoneprobe");

        setupModCompat();
    }

    public Logger getLogger() {
        return logger;
    }

    private void setupModCompat() {
        tesla = Loader.isModLoaded("tesla");
        if (tesla) {
            logger.log(Level.INFO, "The One Probe Detected TESLA: enabling support");
        }

        redstoneflux = Loader.isModLoaded("redstoneflux");
        if (redstoneflux) {
            logger.log(Level.INFO, "The One Probe Detected RedstoneFlux: enabling support");
        }

        baubles = Loader.isModLoaded("Baubles") || Loader.isModLoaded("baubles");
        if (baubles) {
            if (ConfigSetup.supportBaubles) {
                logger.log(Level.INFO, "The One Probe Detected Baubles: enabling support");
            } else {
                logger.log(Level.INFO, "The One Probe Detected Baubles but support disabled in config");
                baubles = false;
            }
        }
    }

    private static void registerCapabilities(){
        CapabilityManager.INSTANCE.register(PlayerFirstSpawn.class, new Capability.IStorage<PlayerFirstSpawn>() {

            @Override
            public NBTBase writeNBT(Capability<PlayerFirstSpawn> capability, PlayerFirstSpawn instance, EnumFacing side) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void readNBT(Capability<PlayerFirstSpawn> capability, PlayerFirstSpawn instance, EnumFacing side, NBTBase nbt) {
                throw new UnsupportedOperationException();
            }

        }, () -> {
            throw new UnsupportedOperationException();
        });
    }


    public void init(FMLInitializationEvent e) {
        NetworkRegistry.INSTANCE.registerGuiHandler(TheOneProbe.instance, new GuiProxy());
    }

    public void postInit(FMLPostInitializationEvent e) {
        if (ConfigSetup.mainConfig.hasChanged()) {
            ConfigSetup.mainConfig.save();
        }
    }
}
