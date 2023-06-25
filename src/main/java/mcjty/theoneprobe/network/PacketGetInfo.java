package mcjty.theoneprobe.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.ProbeHitData;
import mcjty.theoneprobe.apiimpl.ProbeInfo;
import mcjty.theoneprobe.config.ConfigSetup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGetInfo implements IMessage {

    private int dim;
    private BlockPos pos;
    private ProbeMode mode;
    private EnumFacing sideHit;
    private Vec3d hitVec;
    private ItemStack pickBlock;

    private ProbeInfo info;

    @Override
    public void fromBytes(ByteBuf buf) {
        dim = buf.readInt();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        mode = ProbeMode.values()[buf.readByte()];
        byte sideByte = buf.readByte();
        if (sideByte == 127) {
            sideHit = null;
        } else {
            sideHit = EnumFacing.values()[sideByte];
        }
        if (buf.readBoolean()) {
            hitVec = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        }
        pickBlock = ByteBufUtils.readItemStack(buf);

        info = new ProbeInfo();
        info.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dim);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeByte(mode.ordinal());
        buf.writeByte(sideHit == null ? 127 : sideHit.ordinal());
        if (hitVec == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeDouble(hitVec.x);
            buf.writeDouble(hitVec.y);
            buf.writeDouble(hitVec.z);
        }

        ByteBuf buffer = Unpooled.buffer();
        ByteBufUtils.writeItemStack(buffer, pickBlock);
        if (buffer.writerIndex() <= ConfigSetup.maxPacketToServer) {
            buf.writeBytes(buffer);
        } else {
            ItemStack copy = new ItemStack(pickBlock.getItem(), pickBlock.getCount(), pickBlock.getMetadata());
            ByteBufUtils.writeItemStack(buf, copy);
        }

        info.toBytes(buf);
    }

    public PacketGetInfo() {
    }

    public PacketGetInfo(int dim, BlockPos pos, ProbeMode mode, RayTraceResult mouseOver, ItemStack pickBlock, ProbeInfo info) {
        this.dim = dim;
        this.pos = pos;
        this.mode = mode;
        this.sideHit = mouseOver.sideHit;
        this.hitVec = mouseOver.hitVec;
        this.pickBlock = pickBlock;
        this.info = info;
    }

    public static class Handler implements IMessageHandler<PacketGetInfo, IMessage> {
        @Override
        public IMessage onMessage(PacketGetInfo message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketGetInfo message, MessageContext ctx) {
            WorldServer world = DimensionManager.getWorld(message.dim);
            if (world != null) {
                for (IProbeInfoProvider provider : TheOneProbe.theOneProbeImp.getProviders()) {
                    provider.addProbeInfo(Tools.getModeForPlayer(), message.info, ctx.getServerHandler().player, world, world.getBlockState(message.pos), new ProbeHitData(message.pos, message.hitVec, message.sideHit, message.pickBlock));
                }

                PacketHandler.INSTANCE.sendTo(new PacketReturnInfo(message.dim, message.pos, message.info), ctx.getServerHandler().player);
            }
        }
    }
}
