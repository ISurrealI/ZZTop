package mcjty.theoneprobe.core.mixins;

import erebus.tileentity.TileEntityPreservedBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityPreservedBlock.class)
public abstract class TilePreservedBlockMixin extends TileEntity {

    @Inject(method = "markForUpdate()V", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void markForUpdate(CallbackInfo info) {

        World world = this.getWorld();

        if (!world.isRemote) {

            BlockPos pos = this.getPos();
            this.markDirty();
            IBlockState state = world.getBlockState(pos);

            world.notifyBlockUpdate(pos, state, state, 8);
        }

        info.cancel();
    }
}
