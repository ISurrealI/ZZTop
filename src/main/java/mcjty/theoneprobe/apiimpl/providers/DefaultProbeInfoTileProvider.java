package mcjty.theoneprobe.apiimpl.providers;

import blusunrize.immersiveengineering.api.energy.ThermoelectricHandler;
import blusunrize.immersiveengineering.common.Config;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityDynamo;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityThermoelectricGen;
import blusunrize.immersiveengineering.common.blocks.wooden.TileEntityWatermill;
import blusunrize.immersiveengineering.common.blocks.wooden.TileEntityWindmill;
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
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.Loader;

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

        TileEntity te = world.getTileEntity(pos);

        IProbeConfig config = ConfigSetup.getRealConfig();

        ChestInfoTools.showChestInfo(mode, probeInfo, world, pos, config);

        if (config.getRFMode() > 0) {
            showRF(probeInfo, te);
        }
        if (Tools.show(mode, config.getShowTankSetting())) {
            if (config.getTankMode() > 0) {
                showTankInfo(probeInfo, te);
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
                        .text(LABEL + I18n.format("top.block.brewer.fuel", "" + INFO + fuel));
                if (brewtime > 0) {
                    probeInfo.text(LABEL + I18n.format("top.block.brewer.time", "" + INFO + brewtime + " ticks"));
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
                        .text(LABEL + I18n.format("top.block.spawner.mob", "" + INFO + mobName));
            }
        }
    }

    private void showTankInfo(IProbeInfo probeInfo, TileEntity te) {
        
        ProbeConfig config = ConfigSetup.getDefaultConfig();
        
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
            probeInfo.text(NAME + I18n.format("top.block.liquid", fluidStack.getLocalizedName()));
        }
        
        if (config.getTankMode() == 1) {
            probeInfo.progress(contents, maxContents,
                    probeInfo.defaultProgressStyle()
                            .suffix(I18n.format("top.fluid"))
                            .filledColor(ConfigSetup.tankbarFilledColor)
                            .alternateFilledColor(ConfigSetup.tankbarAlternateFilledColor)
                            .borderColor(ConfigSetup.tankbarBorderColor)
                            .numberFormat(ConfigSetup.tankFormat));
        } else 
            probeInfo.text(PROGRESS + ElementProgress.format(contents, ConfigSetup.tankFormat, I18n.format("top.fluid")));
    }

    private void showRF(IProbeInfo probeInfo, TileEntity te) {
        
        ProbeConfig config = ConfigSetup.getDefaultConfig();
        
        if (Loader.isModLoaded("immersiveengineering")) {
            if (te instanceof TileEntityThermoelectricGen) {
                int[] energyValues = getThermoelectricEnergy(te.getWorld(), te.getPos());

                if (energyValues[0] != 0) {
                    int maxValue = energyValues[0] * 2/energyValues[1];
                    addRFInfo(probeInfo, config, energyValues[0], maxValue);
                }
                else addRFInfo(probeInfo, config, 0, 0);
                
                return;
            }
            else if (te instanceof TileEntityDynamo) {

                World world = te.getWorld();

                TileEntityDynamo dynamo = (TileEntityDynamo) te;
                BlockPos fPos = te.getPos().offset(dynamo.facing);

                TileEntity rotatingTile;
                if (!world.isAirBlock(fPos) && (rotatingTile = world.getTileEntity(fPos)) != null) {

                    if (rotatingTile instanceof TileEntityWindmill) {
                        TileEntityWindmill windmill = (TileEntityWindmill) rotatingTile;
                        int[] energies = getWindmillPower(windmill);
                        addRFInfo(probeInfo, config, energies[0], energies[1]);
                        return;
                    }
                    else if (rotatingTile instanceof TileEntityWatermill) {
                        TileEntityWatermill watermill = (TileEntityWatermill) rotatingTile;
                        int energy = (int) (Config.IEConfig.Machines.dynamo_output * getWatermillPower(watermill));
                        addRFInfo(probeInfo, config, energy, Math.max(30, energy));
                        return;
                    }
                }
            }
        }
        
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
                            .suffix(I18n.format("top.energy"))
                            .filledColor(ConfigSetup.rfbarFilledColor)
                            .alternateFilledColor(ConfigSetup.rfbarAlternateFilledColor)
                            .borderColor(ConfigSetup.rfbarBorderColor)
                            .numberFormat(ConfigSetup.rfFormat));
        } else {
            String suffix = I18n.format("top.energy");
            probeInfo.text(PROGRESS + suffix + ": " + ElementProgress.format(energy, ConfigSetup.rfFormat, suffix));
        }
    }

    // Immersive Engineering
    private int[] getThermoelectricEnergy(World world, BlockPos pos) {

        int[] array = new int[2];

        int energy = 0;
        int times = 0;

        for(EnumFacing fd : new EnumFacing[]{EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.WEST}) {

            BlockPos fPos = pos.offset(fd);
            BlockPos oPos = pos.offset(fd.getOpposite());

            if (!world.isAirBlock(fPos) && !world.isAirBlock(oPos)) {

                int temp0 = this.getTemperature(world, fPos);
                int temp1 = this.getTemperature(world, oPos);

                if (temp0 > -1 && temp1 > -1) {
                    int diff = Math.abs(temp0 - temp1);
                    int value = (int)(Math.sqrt(diff) / 2.0 * Config.IEConfig.Machines.thermoelectric_output);
                    if (value > 0) times++;
                    energy += value;
                }
            }
        }

        array[0] = energy;
        array[1] = times;

        return array;
    }

    private int getTemperature(World world, BlockPos pos) {

        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof IFluidBlock) {
            return ((IFluidBlock) block).getFluid().getTemperature(world, pos);
        }
        else return ThermoelectricHandler.getTemperature(block, block.getMetaFromState(state));
    }

    private int[] getWindmillPower(TileEntityWindmill windmill) {

        int[] powers = new int[2];

        if (!windmill.canTurn) {
            return powers;
        }

        double mod = 5.0E-5;
        double b = 800;

        if (!windmill.getWorld().isRaining()) {
            mod *= 0.75;
        }

        if (!windmill.getWorld().isThundering()) {
            mod *= 0.66;
        }

        powers[0] = (int) Math.abs(windmill.turnSpeed * mod * b * (0.5F + windmill.sails * 0.125F) * Config.IEConfig.Machines.dynamo_output);
        powers[1] = (int) Math.abs(windmill.turnSpeed * mod * b * (0.5F + 8 * 0.125F) * Config.IEConfig.Machines.dynamo_output);
        return powers;
    }

    private double getWatermillPower(TileEntityWatermill watermill) {
        return Math.abs(watermill.getPower() * 0.75);
    }
}
