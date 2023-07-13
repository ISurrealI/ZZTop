package mcjty.theoneprobe.core.mixins;

import erebus.ModBlocks;
import erebus.blocks.BlockPreservedBlock;
import erebus.tileentity.TileEntityPreservedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BlockPreservedBlock.class)
public abstract class BlockPreservedBlockMixin extends Block implements ITileEntityProvider, ModBlocks.IHasCustomItem, ModBlocks.ISubBlocksBlock {

    public BlockPreservedBlockMixin(Material materialIn) {
        super(materialIn);
        throw new AssertionError();
    }

    @Inject(method = "getPickBlock(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/RayTraceResult;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setTag(Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    public void getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player, CallbackInfoReturnable<ItemStack> cir, TileEntityPreservedBlock tile, BlockPreservedBlock.EnumAmberType type, ItemStack stack, NBTTagCompound tag) {

        NBTTagCompound entityNBT = tile.getEntityNBT();

        if (entityNBT != null) {
            tag.setTag("EntityNBT", entityNBT);
        }

        stack.setTagCompound(tag);
        cir.setReturnValue(stack);
        cir.cancel();
    }
}
