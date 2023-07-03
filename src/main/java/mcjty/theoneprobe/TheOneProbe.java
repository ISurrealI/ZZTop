package mcjty.theoneprobe;

import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.apiimpl.TheOneProbeImp;
import mcjty.theoneprobe.setup.IProxy;
import mcjty.theoneprobe.setup.ModSetup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Optional;
import java.util.function.Function;

@Mod(modid = TheOneProbe.MODID, name = Tags.TAG_NAME, version = Tags.TAG_VERSION, guiFactory = "mcjty.theoneprobe.config.TopModGuiFactory", useMetadata = true)
public class TheOneProbe {

    public static final String MODID = "theoneprobe";

    @SidedProxy(clientSide="mcjty.theoneprobe.setup.ClientProxy", serverSide="mcjty.theoneprobe.setup.ServerProxy")
    public static IProxy proxy;
    public static ModSetup setup = new ModSetup();

    @Mod.Instance
    public static TheOneProbe instance;

    public static TheOneProbeImp theOneProbeImp = new TheOneProbeImp();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        setup.preInit(e);
        proxy.preInit(e);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        setup.init(e);
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        setup.postInit(e);
        proxy.postInit(e);
    }

    @Mod.EventHandler
    public void imcCallback(FMLInterModComms.IMCEvent event) {
        for (FMLInterModComms.IMCMessage message : event.getMessages()) {
            if (message.key.equalsIgnoreCase("getTheOneProbe")) {
                Optional<Function<ITheOneProbe, Void>> value = message.getFunctionValue(ITheOneProbe.class, Void.class);
                if (value.isPresent()) {
                    value.get().apply(theOneProbeImp);
                } else {
                    setup.getLogger().warn("Some mod didn't return a valid result with getTheOneProbe!");
                }
            }
        }
    }
}
