package mcjty.theoneprobe.apiimpl.providers;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.ProbeConfig;
import mcjty.theoneprobe.apiimpl.elements.ElementProgress;
import mcjty.theoneprobe.config.ConfigSetup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import static mcjty.theoneprobe.api.TextStyleClass.*;
import static mcjty.theoneprobe.api.TextStyleClass.PROGRESS;

public class DefaultProbeInfoTileProvider implements IProbeInfoProvider {

    @Override
    public String getID() {
        return TheOneProbe.MODID + ":default_tile";
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        Block block = blockState.getBlock();
        BlockPos pos = data.getPos();

        IProbeConfig config = ConfigSetup.getRealConfig();

        ChestInfoTools.showChestInfo(mode, probeInfo, world, pos, config);

        if (config.getRFMode() > 0) {
            showRF(probeInfo, world, pos);
        }
        if (Tools.show(mode, config.getShowTankSetting())) {
            if (config.getTankMode() > 0) {
                showTankInfo(probeInfo, world, pos);
            }
        }

        if (Tools.show(mode, config.getShowBrewStandSetting())) {
            showBrewingStandInfo(probeInfo, world, data, block);
        }

        if (Tools.show(mode, config.getShowMobSpawnerSetting())) {
            showMobSpawnerInfo(probeInfo, world, data, block);
        }
    }

    private void showBrewingStandInfo(IProbeInfo probeInfo, World world, IProbeHitData data, Block block) {
        if (block instanceof BlockBrewingStand) {
            TileEntity te = world.getTileEntity(data.getPos());
            if (te instanceof TileEntityBrewingStand) {
                int brewtime = ((TileEntityBrewingStand) te).getField(0);
                int fuel = ((TileEntityBrewingStand) te).getField(1);
                probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                        .item(new ItemStack(Items.BLAZE_POWDER), probeInfo.defaultItemStyle().width(16).height(16))
                        .text(LABEL + "Fuel: " + INFO + fuel);
                if (brewtime > 0) {
                    probeInfo.text(LABEL + "Time: " + INFO + brewtime + " ticks");
                }

            }
        }
    }

    private void showMobSpawnerInfo(IProbeInfo probeInfo, World world, IProbeHitData data, Block block) {
        if (block instanceof BlockMobSpawner) {
            TileEntity te = world.getTileEntity(data.getPos());
            if (te instanceof TileEntityMobSpawner) {
                MobSpawnerBaseLogic logic = ((TileEntityMobSpawner) te).getSpawnerBaseLogic();
                String mobName = logic.getCachedEntity().getName();
                probeInfo.horizontal(probeInfo.defaultLayoutStyle()
                                .alignment(ElementAlignment.ALIGN_CENTER))
                        .text(LABEL + "Mob: " + INFO + mobName);
            }
        }
    }

    private void showTankInfo(IProbeInfo probeInfo, World world, BlockPos pos) {
        ProbeConfig config = ConfigSetup.getDefaultConfig();
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
            net.minecraftforge.fluids.capability.IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            if (handler != null) {
                IFluidTankProperties[] properties = handler.getTankProperties();
                if (properties != null) {
                    for (IFluidTankProperties property : properties) {
                        if (property != null) {
                            FluidStack fluidStack = property.getContents();
                            int maxContents = property.getCapacity();
                            addFluidInfo(probeInfo, config, fluidStack, maxContents);
                        }
                    }
                }
            }
        }
    }

    private void addFluidInfo(IProbeInfo probeInfo, ProbeConfig config, FluidStack fluidStack, int maxContents) {
        int contents = fluidStack == null ? 0 : fluidStack.amount;
        if (fluidStack != null) {
            probeInfo.text(NAME + "Liquid: " + fluidStack.getLocalizedName());
        }
        if (config.getTankMode() == 1) {
            probeInfo.progress(contents, maxContents,
                    probeInfo.defaultProgressStyle()
                            .suffix("mB")
                            .filledColor(ConfigSetup.tankbarFilledColor)
                            .alternateFilledColor(ConfigSetup.tankbarAlternateFilledColor)
                            .borderColor(ConfigSetup.tankbarBorderColor)
                            .numberFormat(ConfigSetup.tankFormat));
        } else {
            probeInfo.text(PROGRESS + ElementProgress.format(contents, ConfigSetup.tankFormat, "mB"));
        }
    }

    private void showRF(IProbeInfo probeInfo, World world, BlockPos pos) {
        ProbeConfig config = ConfigSetup.getDefaultConfig();
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te.hasCapability(CapabilityEnergy.ENERGY, null)) {
            net.minecraftforge.energy.IEnergyStorage handler = te.getCapability(CapabilityEnergy.ENERGY, null);
            if (handler != null) {
                addRFInfo(probeInfo, config, handler.getEnergyStored(), handler.getMaxEnergyStored());
            }
        }
    }

    private void addRFInfo(IProbeInfo probeInfo, ProbeConfig config, long energy, long maxEnergy) {
        if (config.getRFMode() == 1) {
            probeInfo.progress(energy, maxEnergy,
                    probeInfo.defaultProgressStyle()
                            .suffix("FE")
                            .filledColor(ConfigSetup.rfbarFilledColor)
                            .alternateFilledColor(ConfigSetup.rfbarAlternateFilledColor)
                            .borderColor(ConfigSetup.rfbarBorderColor)
                            .numberFormat(ConfigSetup.rfFormat));
        } else {
            probeInfo.text(PROGRESS + "RF: " + ElementProgress.format(energy, ConfigSetup.rfFormat, "RF"));
        }
    }
}
