package mcjty.theoneprobe.apiimpl.providers;

import mcjty.theoneprobe.config.ConfigSetup;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.LayoutStyle;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static mcjty.theoneprobe.api.TextStyleClass.INFO;
import static mcjty.theoneprobe.api.TextStyleClass.LABEL;

public class DebugProbeInfoProvider implements IProbeInfoProvider {

    @Override
    public String getID() {
        return TheOneProbe.MODID + ":debug";
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        if (mode == ProbeMode.DEBUG && ConfigSetup.showDebugInfo) {
            Block block = blockState.getBlock();
            BlockPos pos = data.getPos();
            showDebugInfo(probeInfo, world, blockState, pos, block, data.getSideHit());
        }
    }

    private void showDebugInfo(IProbeInfo probeInfo, World world, IBlockState blockState, BlockPos pos, Block block, EnumFacing side) {

        IProbeInfo vertical = probeInfo.vertical(new LayoutStyle().borderColor(0xffff4444).spacing(2))
                .text(LABEL + I18n.format("top.debug.block.registry_name", "" + INFO + block.getRegistryName()))
                .text(LABEL + I18n.format("top.debug.block.translation_key", INFO + block.getTranslationKey()))
                .text(LABEL + I18n.format("top.debug.block.meta", "" + INFO + block.getMetaFromState(blockState)))
                .text(LABEL + I18n.format("top.debug.block.class", INFO + block.getClass().getSimpleName()))
                .text(LABEL + I18n.format("top.debug.block.hardness", "" + INFO + blockState.getBlockHardness(world, pos)))
                .text(LABEL + I18n.format("top.debug.block.weak_power", "" + INFO + blockState.getWeakPower(world, pos, side.getOpposite())))
                .text(LABEL + I18n.format("top.debug.block.strong_power", "" + INFO + blockState.getStrongPower(world, pos, side.getOpposite())))
                .text(LABEL + I18n.format("top.debug.block.light_level", "" + INFO + blockState.getLightValue(world, pos)));

                /* TODO better handling for stuffs like these
                .text(LABEL + "Power W: " + INFO + block.getWeakPower(blockState, world, pos, side.getOpposite())
                        + LABEL + ", S: " + INFO + block.getStrongPower(blockState, world, pos, side.getOpposite()))*/

        TileEntity te = world.getTileEntity(pos);

        if (te != null)
            vertical.text(LABEL + I18n.format("top.debug.block.tile_entity", INFO + te.getClass().getSimpleName()));
    }

    @Override
    public boolean onlyClientSide() {
        return true;
    }
}
