package mcjty.theoneprobe.apiimpl;

import mcjty.theoneprobe.api.IProbeConfig;

import static mcjty.theoneprobe.api.IProbeConfig.ConfigMode.EXTENDED;
import static mcjty.theoneprobe.api.IProbeConfig.ConfigMode.NORMAL;

public class ProbeConfig implements IProbeConfig {

    private int showRF = 1;
    private int showTank = 1;

    private static IProbeConfig.ConfigMode showHarvestLevel = NORMAL;
    private static IProbeConfig.ConfigMode showCanBeHarvested = NORMAL;
    private static IProbeConfig.ConfigMode showModName = NORMAL;
    private static IProbeConfig.ConfigMode showCropPercentage = NORMAL;
    private static IProbeConfig.ConfigMode showChestContents = EXTENDED;
    private static IProbeConfig.ConfigMode showRedstone = NORMAL;
    private static IProbeConfig.ConfigMode showMobHealth = NORMAL;
    private static IProbeConfig.ConfigMode showMobPotionEffects = EXTENDED;
    private static IProbeConfig.ConfigMode showLeverSetting = NORMAL;
    private static IProbeConfig.ConfigMode showTankSetting = EXTENDED;

    /// Make a lazy copy of this probe config.
    public IProbeConfig lazyCopy() {
        return new LazyProbeConfig(this);
    }

    @Override
    public IProbeConfig setTankMode(int mode) {
        this.showTank = mode;
        return this;
    }

    @Override
    public int getTankMode() {
        return showTank;
    }

    @Override
    public IProbeConfig setRFMode(int showRF) {
        this.showRF = showRF;
        return this;
    }

    @Override
    public int getRFMode() {
        return showRF;
    }

    @Override
    public IProbeConfig showModName(ConfigMode mode) {
        showModName = mode;
        return this;
    }

    @Override
    public ConfigMode getShowModName() {
        return showModName;
    }

    @Override
    public IProbeConfig showHarvestLevel(ConfigMode mode) {
        showHarvestLevel = mode;
        return this;
    }

    @Override
    public ConfigMode getShowHarvestLevel() {
        return showHarvestLevel;
    }

    @Override
    public IProbeConfig showCanBeHarvested(ConfigMode mode) {
        showCanBeHarvested = mode;
        return this;
    }

    @Override
    public ConfigMode getShowCanBeHarvested() {
        return showCanBeHarvested;
    }

    @Override
    public IProbeConfig showCropPercentage(ConfigMode mode) {
        showCropPercentage = mode;
        return this;
    }

    @Override
    public ConfigMode getShowCropPercentage() {
        return showCropPercentage;
    }

    @Override
    public IProbeConfig showChestContents(ConfigMode mode) {
        showChestContents = mode;
        return this;
    }

    @Override
    public ConfigMode getShowChestContents() {
        return showChestContents;
    }

    @Override
    public IProbeConfig showRedstone(ConfigMode mode) {
        showRedstone = mode;
        return this;
    }

    @Override
    public ConfigMode getShowRedstone() {
        return showRedstone;
    }

    @Override
    public IProbeConfig showMobHealth(ConfigMode mode) {
        showMobHealth = mode;
        return this;
    }

    @Override
    public ConfigMode getShowMobHealth() {
        return showMobHealth;
    }

    @Override
    public IProbeConfig showMobPotionEffects(ConfigMode mode) {
        showMobPotionEffects = mode;
        return this;
    }

    @Override
    public ConfigMode getShowMobPotionEffects() {
        return showMobPotionEffects;
    }

    @Override
    public IProbeConfig showLeverSetting(ConfigMode mode) {
        showLeverSetting = mode;
        return this;
    }

    @Override
    public ConfigMode getShowLeverSetting() {
        return showLeverSetting;
    }

    @Override
    public IProbeConfig showTankSetting(ConfigMode mode) {
        showTankSetting = mode;
        return this;
    }

    @Override
    public ConfigMode getShowTankSetting() {
        return showTankSetting;
    }
}
