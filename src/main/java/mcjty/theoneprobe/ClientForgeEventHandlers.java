package mcjty.theoneprobe;

import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.config.ConfigSetup;
import mcjty.theoneprobe.gui.GuiConfig;
import mcjty.theoneprobe.gui.GuiNote;
import mcjty.theoneprobe.keys.KeyBindings;
import mcjty.theoneprobe.rendering.OverlayRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class ClientForgeEventHandlers {

    public static boolean ignoreNextGuiClose = false;
    public static boolean serverHasMod = false;

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (ignoreNextGuiClose) {
            GuiScreen current = Minecraft.getMinecraft().currentScreen;
            if (event.getGui() == null && (current instanceof GuiConfig || current instanceof GuiNote)) {
                ignoreNextGuiClose = false;
                // We don't want our gui to be closed for a new 'null' gui
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void renderGameOverlayEvent(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) {
            return;
        }

        if (ConfigSetup.holdKeyToMakeVisible) {
            if (!KeyBindings.toggleVisible.isKeyDown()) {
                return;
            }
        } else {
            if (!ConfigSetup.isVisible) {
                return;
            }
        }

        OverlayRenderer.renderHUD(Tools.getModeForPlayer(), event.getPartialTicks());
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeyBindings.toggleLiquids.isPressed()) {
            ConfigSetup.setLiquids(!ConfigSetup.showLiquids);
        } else if (KeyBindings.toggleVisible.isPressed()) {
            if (!ConfigSetup.holdKeyToMakeVisible) {
                ConfigSetup.setVisible(!ConfigSetup.isVisible);
            }
//        } else if (KeyBindings.generateLag.isPressed()) {
//            PacketHandler.INSTANCE.sendToServer(new PacketGenerateLag());
        }
    }

    @SubscribeEvent
    public void onCustomPacketRegisteration(FMLNetworkEvent.CustomPacketRegistrationEvent<INetHandler> event) {
        if (event.getOperation().equals("REGISTER") && event.getRegistrations().contains("theoneprobe") && event.getHandler() instanceof INetHandlerPlayServer) {
            serverHasMod = true;
        }
    }
}