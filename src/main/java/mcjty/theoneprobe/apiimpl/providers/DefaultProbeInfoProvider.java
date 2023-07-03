package mcjty.theoneprobe.apiimpl.providers;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.config.ConfigSetup;
import net.minecraft.block.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;

import java.util.Collections;

import static mcjty.theoneprobe.api.IProbeInfo.ENDLOC;
import static mcjty.theoneprobe.api.IProbeInfo.STARTLOC;
import static mcjty.theoneprobe.api.TextStyleClass.*;

public class DefaultProbeInfoProvider implements IProbeInfoProvider {

    @Override
    public String getID() {
        return TheOneProbe.MODID + ":default";
    }

    @Override
    public boolean onlyClientSide() {
        return true;
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        Block block = blockState.getBlock();
        BlockPos pos = data.getPos();

        IProbeConfig config = ConfigSetup.getRealConfig();

        boolean handled = false;
        for (IBlockDisplayOverride override : TheOneProbe.theOneProbeImp.getBlockOverrides()) {
            if (override.overrideStandardInfo(mode, probeInfo, player, world, blockState, data)) {
                handled = true;
                break;
            }
        }
        if (!handled) {
            showStandardBlockInfo(config, mode, probeInfo, blockState, block, world, pos, player, data);
        }

        if (Tools.show(mode, config.getShowCropPercentage())) {
            showGrowthLevel(probeInfo, blockState);
        }

        boolean showHarvestLevel = Tools.show(mode, config.getShowHarvestLevel());
        boolean showHarvested = Tools.show(mode, config.getShowCanBeHarvested());
        if (showHarvested && showHarvestLevel) {
            HarvestInfoTools.showHarvestInfo(probeInfo, world, pos, block, blockState, player);
        } else if (showHarvestLevel) {
            HarvestInfoTools.showHarvestLevel(probeInfo, blockState, block);
        } else if (showHarvested) {
            HarvestInfoTools.showCanBeHarvested(probeInfo, world, pos, block, player);
        }

        if (Tools.show(mode, config.getShowRedstone())) {
            showRedstonePower(probeInfo, world, blockState, data, block, Tools.show(mode, config.getShowLeverSetting()));
        }
        if (Tools.show(mode, config.getShowLeverSetting())) {
            showLeverSetting(probeInfo, world, blockState, data, block);
        }
    }

    private void showRedstonePower(IProbeInfo probeInfo, World world, IBlockState blockState, IProbeHitData data, Block block, boolean showLever) {

        if (showLever && block instanceof BlockLever) {
            // We are showing the lever setting so we don't show redstone in that case
            return;
        }

        int redstonePower;
        if (block instanceof BlockRedstoneWire) {
            redstonePower = blockState.getValue(BlockRedstoneWire.POWER);
        } else {
            redstonePower = world.getRedstonePower(data.getPos(), data.getSideHit().getOpposite());
        }

        if (redstonePower > 0) {
            probeInfo.horizontal()
                    .item(new ItemStack(Items.REDSTONE), probeInfo.defaultItemStyle().width(14).height(14))
                    .text(LABEL + I18n.format("top.block.power", "" + INFO + redstonePower));
        }
    }

    private void showLeverSetting(IProbeInfo probeInfo, World world, IBlockState blockState, IProbeHitData data, Block block) {
        if (block instanceof BlockLever) {
            Boolean powered = blockState.getValue(BlockLever.POWERED);
            probeInfo.horizontal().item(new ItemStack(Items.REDSTONE), probeInfo.defaultItemStyle().width(14).height(14))
                    .text(LABEL + I18n.format("top.block.lever.state", INFO + (powered ? "On" : "Off")));
        } else if (block instanceof BlockRedstoneComparator) {
            BlockRedstoneComparator.Mode mode = blockState.getValue(BlockRedstoneComparator.MODE);
            probeInfo.text(LABEL + I18n.format("top.block.lever.mode", INFO + mode.getName()));
        } else if (block instanceof BlockRedstoneRepeater) {
            Boolean locked = blockState.getValue(BlockRedstoneRepeater.LOCKED);
            Integer delay = blockState.getValue(BlockRedstoneRepeater.DELAY);
            probeInfo.text(LABEL + I18n.format("top.block.lever.delay", "" + INFO + delay + " ticks"));
            if (locked) {
                probeInfo.text(INFO + I18n.format("top.block.lever.locked"));
            }
        }
    }

    // The only downside is block needs to be BlockCrops.
    private void showGrowthLevel(IProbeInfo probeInfo, IBlockState blockState) {
        if (blockState.getBlock() instanceof BlockCrops) {
            BlockCrops crop = (BlockCrops) blockState.getBlock();

            int age = crop.getAge(blockState);
            int maxAge = crop.getMaxAge();

            if (age == maxAge) {
                probeInfo.text(OK + I18n.format("top.block.fully_grown"));
            }
            else probeInfo.text(LABEL + I18n.format("top.block.growth", "" + WARNING + (age * 100) / maxAge + "%"));
        }
        /*for (IProperty<?> property : blockState.getProperties().keySet()) {
            if(!"age".equals(property.getName())) continue;
            if(property.getValueClass() == Integer.class) {
                IProperty<Integer> integerProperty = (IProperty<Integer>)property;
                int age = blockState.getValue(integerProperty);
                int maxAge = Collections.max(integerProperty.getAllowedValues());
                if (age == maxAge) {
                    probeInfo.text(OK + I18n.format("top.block.fully_grown"));
                } else {
                    probeInfo.text(LABEL + I18n.format("top.block.growth", "" + WARNING + (age * 100) / maxAge + "%"));
                }
            }
            return;
        }*/
    }

    public static void showStandardBlockInfo(IProbeConfig config, ProbeMode mode, IProbeInfo probeInfo, IBlockState blockState, Block block, World world,
                                             BlockPos pos, EntityPlayer player, IProbeHitData data) {
        String modid = Tools.getModName(block);

        ItemStack pickBlock = data.getPickBlock();

        if (block instanceof BlockSilverfish && mode != ProbeMode.DEBUG && !Tools.show(mode,config.getShowSilverfish())) {
            BlockSilverfish.EnumType type = blockState.getValue(BlockSilverfish.VARIANT);
            blockState = type.getModelBlock();
            block = blockState.getBlock();
            pickBlock = new ItemStack(block, 1, block.getMetaFromState(blockState));
        }

        if (block instanceof BlockFluidBase || block instanceof BlockLiquid) {
            Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
            if (fluid != null) {
                FluidStack fluidStack = new FluidStack(fluid, Fluid.BUCKET_VOLUME);
                ItemStack bucketStack = FluidUtil.getFilledBucket(fluidStack);

                IProbeInfo horizontal = probeInfo.horizontal();
                if (fluidStack.isFluidEqual(FluidUtil.getFluidContained(bucketStack))) {
                    horizontal.item(bucketStack);
                } else {
                    horizontal.icon(fluid.getStill(), -1, -1, 16, 16, probeInfo.defaultIconStyle().width(20));
                }

                horizontal.vertical()
                        .text(NAME + fluidStack.getLocalizedName())
                        .text(MODNAME + modid);
                return;
            }
        }

        if (!pickBlock.isEmpty()) {
            if (Tools.show(mode, config.getShowModName())) {
                probeInfo.horizontal()
                        .item(pickBlock)
                        .vertical()
                        .itemLabel(pickBlock)
                        .text(MODNAME + modid);
            } else {
                probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                        .item(pickBlock)
                        .itemLabel(pickBlock);
            }
        } else {
            if (Tools.show(mode, config.getShowModName())) {
                probeInfo.vertical()
                        .text(NAME + getBlockUnlocalizedName(block))
                        .text(MODNAME + modid);
            } else {
                probeInfo.vertical()
                        .text(NAME + getBlockUnlocalizedName(block));
            }
        }
    }

    private static String getBlockUnlocalizedName(Block block) {
        return STARTLOC + block.getTranslationKey() + ".name" + ENDLOC;
    }
}
