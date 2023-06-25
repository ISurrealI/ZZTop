package mcjty.theoneprobe.apiimpl;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.elements.ElementVertical;
import mcjty.theoneprobe.config.ConfigSetup;
import mcjty.theoneprobe.network.ThrowableIdentity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static mcjty.theoneprobe.api.TextStyleClass.*;

public class ProbeInfo extends ElementVertical {

    public List<IElement> getElements() {
        return children;
    }

    public void fromBytes(ByteBuf buf) {
        children = createElements(buf);
    }

    public ProbeInfo() {
        super((Integer) null, 2, ElementAlignment.ALIGN_TOPLEFT);
    }

    public static List<IElement> createElements(ByteBuf buf) {
        int size = buf.readShort();
        List<IElement> elements = new ArrayList<>(size);
        for (int i = 0 ; i < size ; i++) {
            int id = buf.readInt();
            IElementFactory factory = TheOneProbe.theOneProbeImp.getElementFactory(id);
            IElement element = factory.createElement(buf);
            elements.add(element);
        }
        return elements;
    }

    public static void writeElements(List<IElement> elements, ByteBuf buf) {
        buf.writeShort(elements.size());
        for (IElement element : elements) {
            buf.writeInt(element.getID());
            element.toBytes(buf);
        }
    }

    public void removeElement(IElement element) {
        this.getElements().remove(element);
    }

    public static ProbeInfo getProbeInfo(EntityPlayer player, ProbeMode mode, World world, BlockPos blockPos, EnumFacing sideHit, Vec3d hitVec, ItemStack pickBlock) {

        IBlockState state = world.getBlockState(blockPos);
        ProbeInfo probeInfo = TheOneProbe.theOneProbeImp.create();
        IProbeHitData data = new ProbeHitData(blockPos, hitVec, sideHit, pickBlock);

        IProbeConfig probeConfig = TheOneProbe.theOneProbeImp.createProbeConfig();
        List<IProbeConfigProvider> configProviders = TheOneProbe.theOneProbeImp.getConfigProviders();
        for (IProbeConfigProvider configProvider : configProviders) {
            configProvider.getProbeConfig(probeConfig, player, world, state, data);
        }
        ConfigSetup.setRealConfig(probeConfig);

        List<IProbeInfoProvider> providers = TheOneProbe.theOneProbeImp.getClientProviders();
        for (IProbeInfoProvider provider : providers) {
            try {
                provider.addProbeInfo(mode, probeInfo, player, world, state, data);
            } catch (Throwable e) {
                ThrowableIdentity.registerThrowable(e);
                probeInfo.text(LABEL + "Error: " + ERROR + provider.getID());
            }
        }
        return probeInfo;
    }

    public static ProbeInfo getProbeInfo(EntityPlayer player, ProbeMode mode, World world, Entity entity, Vec3d hitVec) {

        ProbeInfo probeInfo = TheOneProbe.theOneProbeImp.create();
        IProbeHitEntityData data = new ProbeHitEntityData(hitVec);

        IProbeConfig probeConfig = TheOneProbe.theOneProbeImp.createProbeConfig();
        List<IProbeConfigProvider> configProviders = TheOneProbe.theOneProbeImp.getConfigProviders();
        for (IProbeConfigProvider configProvider : configProviders) {
            configProvider.getProbeConfig(probeConfig, player, world, entity, data);
        }
        ConfigSetup.setRealConfig(probeConfig);

        List<IProbeInfoEntityProvider> entityProviders = TheOneProbe.theOneProbeImp.getEntityClientProviders();
        for (IProbeInfoEntityProvider provider : entityProviders) {
            try {
                provider.addProbeEntityInfo(mode, probeInfo, player, world, entity, data);
            } catch (Throwable e) {
                ThrowableIdentity.registerThrowable(e);
                probeInfo.text(LABEL + "Error: " + ERROR + provider.getID());
            }
        }
        return probeInfo;
    }
}
