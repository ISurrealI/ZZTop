package mcjty.theoneprobe.apiimpl;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mcjty.theoneprobe.config.ConfigSetup;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.elements.*;

import java.util.*;

public class TheOneProbeImp implements ITheOneProbe {

    public static int ELEMENT_TEXT;
    public static int ELEMENT_ITEM;
    public static int ELEMENT_PROGRESS;
    public static int ELEMENT_HORIZONTAL;
    public static int ELEMENT_VERTICAL;
    public static int ELEMENT_ENTITY;
    public static int ELEMENT_ICON;
    public static int ELEMENT_ITEMLABEL;

    private List<IProbeConfigProvider> configProviders = new ObjectArrayList<>();

    private List<IProbeInfoProvider> providers = new ObjectArrayList<>();
    private List<IProbeInfoProvider> clientProviders = new ObjectArrayList<>();

    private List<IProbeInfoEntityProvider> entityProviders = new ObjectArrayList<>();
    private List<IProbeInfoEntityProvider> entityClientProviders = new ObjectArrayList<>();
    private final List<IBlockDisplayOverride> blockOverrides = new ObjectArrayList<>();
    private final List<IEntityDisplayOverride> entityOverrides = new ObjectArrayList<>();


    private final Map<Integer,IElementFactory> factories = new Int2ObjectOpenHashMap<>();
    private int lastId = 0;

    public TheOneProbeImp() {
    }

    public static void registerElements() {
        ELEMENT_TEXT = TheOneProbe.theOneProbeImp.registerElementFactory(ElementText::new);
        ELEMENT_ITEM = TheOneProbe.theOneProbeImp.registerElementFactory(ElementItemStack::new);
        ELEMENT_PROGRESS = TheOneProbe.theOneProbeImp.registerElementFactory(ElementProgress::new);
        ELEMENT_HORIZONTAL = TheOneProbe.theOneProbeImp.registerElementFactory(ElementHorizontal::new);
        ELEMENT_VERTICAL = TheOneProbe.theOneProbeImp.registerElementFactory(ElementVertical::new);
        ELEMENT_ENTITY = TheOneProbe.theOneProbeImp.registerElementFactory(ElementEntity::new);
        ELEMENT_ICON = TheOneProbe.theOneProbeImp.registerElementFactory(ElementIcon::new);
        ELEMENT_ITEMLABEL = TheOneProbe.theOneProbeImp.registerElementFactory(ElementItemLabel::new);
    }


    @Override
    public void registerProvider(IProbeInfoProvider provider) {
        if (!ConfigSetup.excludedProviders.contains(provider.getID())) {
            if (provider.onlyClientSide()) clientProviders.add(provider);
            else providers.add(provider);
        }
    }

    @Override
    public void registerEntityProvider(IProbeInfoEntityProvider provider) {
        if (!ConfigSetup.excludedEntityProviders.contains(provider.getID())) {
            if (provider.onlyClientSide()) entityClientProviders.add(provider);
            else entityProviders.add(provider);
        }
    }

    @Override
    public IElementFactory getElementFactory(int id) {
        return factories.get(id);
    }

    public ProbeInfo create() {
        return new ProbeInfo();
    }

    public List<IProbeInfoProvider> getProviders() {
        return providers;
    }

    public List<IProbeInfoProvider> getClientProviders() {
        return clientProviders;
    }

    public List<IProbeInfoEntityProvider> getEntityProviders() {
        return entityProviders;
    }

    public List<IProbeInfoEntityProvider> getEntityClientProviders() {
        return entityClientProviders;
    }

    public IProbeInfoProvider getProviderByID(String id) {
        return providers.get(getIDFromString(id));
    }

    public IProbeInfoEntityProvider getEntityProviderByID(String id) {
        return entityProviders.get(getIDFromString(id));
    }

    @Override
    public int registerElementFactory(IElementFactory factory) {
        factories.put(lastId, factory);
        int id = lastId;
        lastId++;
        return id;
    }

    @Override
    public IOverlayRenderer getOverlayRenderer() {
        return new DefaultOverlayRenderer();
    }

    @Override
    public IProbeConfig createProbeConfig() {
        return ConfigSetup.getDefaultConfig().lazyCopy();
    }

    @Override
    public void registerProbeConfigProvider(IProbeConfigProvider provider) {
        configProviders.add(provider);
    }

    public List<IProbeConfigProvider> getConfigProviders() {
        return configProviders;
    }

    @Override
    public void registerBlockDisplayOverride(IBlockDisplayOverride override) {
        blockOverrides.add(override);
    }

    public List<IBlockDisplayOverride> getBlockOverrides() {
        return blockOverrides;
    }

    @Override
    public void registerEntityDisplayOverride(IEntityDisplayOverride override) {
        entityOverrides.add(override);
    }

    public List<IEntityDisplayOverride> getEntityOverrides() {
        return entityOverrides;
    }

    private static int getIDFromString(String name) {

        int i = 0;

        if (name.contains(":")) {
            String[] strs = name.split(":");
            for (String str : strs) {
                i += str.charAt(0) * str.charAt(str.length() / 2) * str.charAt(str.length() - 1) * name.length();
            }

            return i;
        } else {
            i += name.charAt(0) * name.charAt(name.length() / 2) * name.charAt(name.length() - 1) * name.length();
        }

        return i;
    }
}
