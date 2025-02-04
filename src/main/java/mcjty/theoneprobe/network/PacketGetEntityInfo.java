package mcjty.theoneprobe.network;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.ProbeHitEntityData;
import mcjty.theoneprobe.apiimpl.ProbeInfo;
import mcjty.theoneprobe.config.ConfigSetup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;
import java.util.UUID;

import static mcjty.theoneprobe.api.TextStyleClass.ERROR;
import static mcjty.theoneprobe.api.TextStyleClass.LABEL;
import static mcjty.theoneprobe.config.ConfigSetup.PROBE_NEEDEDFOREXTENDED;
import static mcjty.theoneprobe.config.ConfigSetup.PROBE_NEEDEDHARD;

public class PacketGetEntityInfo implements IMessage {

    private int dim;
    private UUID uuid;
    private ProbeMode mode;
    private Vec3d hitVec;
    private ProbeInfo info;

    @Override
    public void fromBytes(ByteBuf buf) {
        dim = buf.readInt();
        uuid = new UUID(buf.readLong(), buf.readLong());
        mode = ProbeMode.values()[buf.readByte()];
        if (buf.readBoolean()) {
            hitVec = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        }
        this.info = new ProbeInfo();
        this.info.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dim);
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
        buf.writeByte(mode.ordinal());
        if (hitVec == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeDouble(hitVec.x);
            buf.writeDouble(hitVec.y);
            buf.writeDouble(hitVec.z);
        }
        this.info.toBytes(buf);
    }

    public PacketGetEntityInfo() {
    }

    public PacketGetEntityInfo(int dim, ProbeMode mode, RayTraceResult mouseOver, Entity entity, ProbeInfo info) {
        this.dim = dim;
        this.uuid = entity.getPersistentID();
        this.mode = mode;
        this.hitVec = mouseOver.hitVec;
        this.info = info;
    }

    public static class Handler implements IMessageHandler<PacketGetEntityInfo, IMessage> {
        @Override
        public IMessage onMessage(PacketGetEntityInfo message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketGetEntityInfo message, MessageContext ctx) {
            WorldServer world = DimensionManager.getWorld(message.dim);
            if (world != null) {
                Entity entity = world.getEntityFromUuid(message.uuid);
                if (entity != null) {
                    TheOneProbe.theOneProbeImp.getEntityProviders().forEach(p -> p.addProbeEntityInfo(message.mode, message.info, ctx.getServerHandler().player, world, entity, new ProbeHitEntityData(message.hitVec)));
                    PacketHandler.INSTANCE.sendTo(new PacketReturnEntityInfo(message.uuid, message.info), ctx.getServerHandler().player);
                }
            }
        }
    }

    private static ProbeInfo getProbeInfo(EntityPlayer player, ProbeMode mode, World world, Entity entity, Vec3d hitVec) {

        ProbeInfo probeInfo = TheOneProbe.theOneProbeImp.create();
        IProbeHitEntityData data = new ProbeHitEntityData(hitVec);

        IProbeConfig probeConfig = TheOneProbe.theOneProbeImp.createProbeConfig();
        List<IProbeConfigProvider> configProviders = TheOneProbe.theOneProbeImp.getConfigProviders();
        for (IProbeConfigProvider configProvider : configProviders) {
            configProvider.getProbeConfig(probeConfig, player, world, entity, data);
        }
        ConfigSetup.setRealConfig(probeConfig);

        List<IProbeInfoEntityProvider> entityProviders = TheOneProbe.theOneProbeImp.getEntityProviders();
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
