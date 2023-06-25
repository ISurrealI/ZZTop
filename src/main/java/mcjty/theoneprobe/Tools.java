package mcjty.theoneprobe;

import mcjty.theoneprobe.api.IProbeConfig;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.config.ConfigSetup;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.apache.commons.lang3.text.WordUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static mcjty.theoneprobe.api.IProbeConfig.ConfigMode.EXTENDED;
import static mcjty.theoneprobe.api.IProbeConfig.ConfigMode.NORMAL;

public class Tools {

    private final static Map<String, String> modNamesForIds = new HashMap<>();

    private static void init() {
        Map<String, ModContainer> modMap = Loader.instance().getIndexedModList();
        for (Map.Entry<String, ModContainer> modEntry : modMap.entrySet()) {
            String lowercaseId = modEntry.getKey().toLowerCase(Locale.ENGLISH);
            String modName = modEntry.getValue().getName();
            modNamesForIds.put(lowercaseId, modName);
        }
    }

    public static String getModName(Block block) {
        if (modNamesForIds.isEmpty()) {
            init();
        }
        ResourceLocation itemResourceLocation = block.getRegistryName();
        String modId = itemResourceLocation.getNamespace();
        String lowercaseModId = modId.toLowerCase(Locale.ENGLISH);
        String modName = modNamesForIds.get(lowercaseModId);
        if (modName == null) {
            modName = WordUtils.capitalize(modId);
            modNamesForIds.put(lowercaseModId, modName);
        }
        return modName;
    }

    public static String getModName(Entity entity) {
        if (modNamesForIds.isEmpty()) {
            init();
        }
        EntityRegistry.EntityRegistration modSpawn = EntityRegistry.instance().lookupModSpawn(entity.getClass(), true);
        if (modSpawn == null) {
            return "Minecraft";
        }
        ModContainer container = modSpawn.getContainer();
        if (container == null) {
            return "Minecraft";
        }
        String modId = container.getModId();
        String lowercaseModId = modId.toLowerCase(Locale.ENGLISH);
        String modName = modNamesForIds.get(lowercaseModId);
        if (modName == null) {
            modName = WordUtils.capitalize(modId);
            modNamesForIds.put(lowercaseModId, modName);
        }
        return modName;
    }

    public static boolean show(ProbeMode mode, IProbeConfig.ConfigMode cfg) {
        return cfg == NORMAL || (cfg == EXTENDED && mode == ProbeMode.EXTENDED);
    }

    public static ProbeMode getModeForPlayer() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;

        ProbeMode mode = ProbeMode.NORMAL;

        if (player.isCreative()) {
            if (ConfigSetup.debugMode) mode = ProbeMode.DEBUG;
            else mode = ProbeMode.EXTENDED;
        } else if (player.isSneaking()) mode = ProbeMode.EXTENDED;

        return mode;
    }

    public static String upperecacs(String name) {

        StringBuilder builder = new StringBuilder().append(Character.toUpperCase(name.charAt(0)));

        boolean b = false;
        for (int i = 1; i < name.length(); i++) {


            char ch = name.charAt(i);

            if (b) {
                builder.append(Character.toUpperCase(ch));
                b = false;
                continue;
            }

            if (ch == '_') {
                builder.append(' ');
                b = true;
                continue;
            }

            builder.append(ch);
        }

        return builder.toString();
    }

    public static TextFormatting getFormatFromColor(EnumDyeColor color) {
        switch (color) {
            default: return TextFormatting.WHITE;
            case ORANGE:
            case BROWN:
                return TextFormatting.GOLD;
            case MAGENTA:
            case PINK:
                return TextFormatting.LIGHT_PURPLE;
            case LIGHT_BLUE: return TextFormatting.AQUA;
            case YELLOW: return TextFormatting.YELLOW;
            case LIME: return TextFormatting.GREEN;
            case GRAY: return TextFormatting.DARK_GRAY;
            case SILVER: return TextFormatting.GRAY;
            case CYAN: return TextFormatting.DARK_AQUA;
            case PURPLE: return TextFormatting.DARK_PURPLE;
            case BLUE: return TextFormatting.DARK_BLUE;
            case GREEN: return TextFormatting.DARK_GREEN;
            case RED: return TextFormatting.DARK_RED;
            case BLACK: return TextFormatting.BLACK;
        }
    }
}
