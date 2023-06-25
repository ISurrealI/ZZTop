package mcjty.theoneprobe.core;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class TOPTransformer implements IClassTransformer, Opcodes {

    private Logger LOGGER = LogManager.getLogger("ZZTop");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (name.equals("erebus.blocks.BlockPreservedBlock")) {
            LOGGER.info("Transforming class: " + name);
            return handleBlockPreservedBlock(basicClass);
        }
        return basicClass;
    }

    public byte[] handleBlockPreservedBlock(byte[] basicClass) {

        ClassReader reader = new ClassReader(basicClass);
        ClassNode cls = new ClassNode();
        reader.accept(cls, 0);

        for (MethodNode method : cls.methods) {
            if (method.name.equals("getPickBlock")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();

                boolean check = false;
                int i = 5;

                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();

                    if (node.getOpcode() == ALOAD && ((VarInsnNode) node).var == 9) {
                        check = true;
                    }

                    if (check && i > 0) {
                        i--;
                        LOGGER.info("Removing: " + node);
                        iterator.remove();
                        continue;
                    }

                    if (i == 0) {
                        LOGGER.info(node);
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 9));
                        list.add(new VarInsnNode(ALOAD, 6));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "erebus/tileentity/TileEntityPreservedBlock", "getEntityNBT", "()Lnet/minecraft/nbt/NBTTagCompound;", false));
                        list.add(new MethodInsnNode(INVOKESTATIC, "mcjty/theoneprobe/core/TOPHooks", "setTag", "(Lnet/minecraft/nbt/NBTTagCompound;Lnet/minecraft/nbt/NBTTagCompound;)V", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }

                break;
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cls.accept(writer);

        return writer.toByteArray();
    }

    /*public byte[] handleTilePreservedBlock(byte[] basicClass) {

        ClassReader reader = new ClassReader(basicClass);
        ClassNode cls = new ClassNode();
        reader.accept(cls, 0);

        for (MethodNode method : cls.methods) {
            if (method.name.equals("markForUpdate")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();

                    if (node.getOpcode() == ALOAD) {
                        AbstractInsnNode next = iterator.next();
                        method.instructions.insertBefore(next, new MethodInsnNode(INVOKEVIRTUAL, "erebus/tileentity/TileEntityPreservedBlock", "getWorld" : "func_145831_w", "()Lnet/minecraft/world/World;", false));
                        break;
                    }
                }
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cls.accept(writer);

        return writer.toByteArray();
    }*/


}
