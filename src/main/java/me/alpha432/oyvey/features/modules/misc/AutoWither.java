package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.features.modules.*;
import me.alpha432.oyvey.features.setting.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.*;
import net.minecraft.network.*;
import net.minecraft.util.*;
import me.alpha432.oyvey.util.*;
import net.minecraft.network.play.client.*;
import com.mojang.realmsclient.gui.*;
import me.alpha432.oyvey.features.command.*;
import java.util.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.item.*;

public class AutoWither extends Module
{
    private static boolean isSneaking;
    private Setting<UseMode> useMode;
    private Setting<Float> placeRange;
    private Setting<Integer> delay;
    private Setting<Boolean> rotate;
    private Setting<Boolean> debug;
    private BlockPos placeTarget;
    private boolean rotationPlaceableX;
    private boolean rotationPlaceableZ;
    private int bodySlot;
    private int headSlot;
    private int buildStage;
    private int delayStep;

    public AutoWither() {
        super("AutoWither", "Automatically places a wither", Category.MISC, false, false, false);
        this.useMode = (Setting<UseMode>)this.register(new Setting("UseMode", UseMode.SPAM));
        this.placeRange = (Setting<Float>)this.register(new Setting("PlaceRange", 3.5f, 2.0f, 10.0f));
        this.delay = (Setting<Integer>)this.register(new Setting("Delay", 20, 12, 100, v -> this.useMode.getValue().equals(UseMode.SPAM)));
        this.rotate = (Setting<Boolean>)this.register(new Setting("Rotate", true));
        this.debug = (Setting<Boolean>)this.register(new Setting("Debug", false));
    }

    @Override
    public void onEnable() {
        if (AutoWither.mc.player == null) {
            this.disable();
            return;
        }
        this.buildStage = 1;
        this.delayStep = 1;
    }

    private boolean checkBlocksInHotbar() {
        this.headSlot = -1;
        this.bodySlot = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = AutoWither.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() == Items.SKULL && stack.getItemDamage() == 1) {
                    if (AutoWither.mc.player.inventory.getStackInSlot(i).stackSize >= 3) {
                        this.headSlot = i;
                    }
                }
                else if (stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock)stack.getItem()).getBlock();
                    if (block instanceof BlockSoulSand && AutoWither.mc.player.inventory.getStackInSlot(i).stackSize >= 4) {
                        this.bodySlot = i;
                    }
                }
            }
        }
        return this.bodySlot != -1 && this.headSlot != -1;
    }

    private boolean testStructure() {
        return this.testWitherStructure();
    }

    private static EnumFacing getPlaceableSide(final BlockPos pos) {
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbour = pos.offset(side);
            if (AutoWither.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(AutoWither.mc.world.getBlockState(neighbour), false)) {
                final IBlockState blockState = AutoWither.mc.world.getBlockState(neighbour);
                if (!blockState.getMaterial().isReplaceable() && !(blockState.getBlock() instanceof BlockTallGrass) && !(blockState.getBlock() instanceof BlockDeadBush)) {
                    return side;
                }
            }
        }
        return null;
    }

    private boolean testWitherStructure() {
        boolean noRotationPlaceable = true;
        this.rotationPlaceableX = true;
        this.rotationPlaceableZ = true;
        boolean isShitGrass = false;
        if (AutoWither.mc.world.getBlockState(this.placeTarget) == null) {
            return false;
        }
        final Block block = AutoWither.mc.world.getBlockState(this.placeTarget).getBlock();
        if (block instanceof BlockTallGrass || block instanceof BlockDeadBush) {
            isShitGrass = true;
        }
        if (getPlaceableSide(this.placeTarget.up()) == null) {
            return false;
        }
        for (final BlockPos pos : BodyParts.bodyBase) {
            if (this.placingIsBlocked(this.placeTarget.add((Vec3i)pos))) {
                noRotationPlaceable = false;
            }
        }
        for (final BlockPos pos : BodyParts.ArmsX) {
            if (this.placingIsBlocked(this.placeTarget.add((Vec3i)pos)) || this.placingIsBlocked(this.placeTarget.add((Vec3i)pos.down()))) {
                this.rotationPlaceableX = false;
            }
        }
        for (final BlockPos pos : BodyParts.ArmsZ) {
            if (this.placingIsBlocked(this.placeTarget.add((Vec3i)pos)) || this.placingIsBlocked(this.placeTarget.add((Vec3i)pos.down()))) {
                this.rotationPlaceableZ = false;
            }
        }
        for (final BlockPos pos : BodyParts.headsX) {
            if (this.placingIsBlocked(this.placeTarget.add((Vec3i)pos))) {
                this.rotationPlaceableX = false;
            }
        }
        for (final BlockPos pos : BodyParts.headsZ) {
            if (this.placingIsBlocked(this.placeTarget.add((Vec3i)pos))) {
                this.rotationPlaceableZ = false;
            }
        }
        return !isShitGrass && noRotationPlaceable && (this.rotationPlaceableX || this.rotationPlaceableZ);
    }

    private boolean testIronGolemStructure() {
        boolean noRotationPlaceable = true;
        this.rotationPlaceableX = true;
        this.rotationPlaceableZ = true;
        boolean isShitGrass = false;
        if (AutoWither.mc.world.getBlockState(this.placeTarget) == null) {
            return false;
        }
        final Block block = AutoWither.mc.world.getBlockState(this.placeTarget).getBlock();
        if (block instanceof BlockTallGrass || block instanceof BlockDeadBush) {
            isShitGrass = true;
        }
        if (getPlaceableSide(this.placeTarget.up()) == null) {
            return false;
        }
        for (final BlockPos pos : BodyParts.bodyBase) {
            if (this.placingIsBlocked(this.placeTarget.add((Vec3i)pos))) {
                noRotationPlaceable = false;
            }
        }
        for (final BlockPos pos : BodyParts.ArmsX) {
            if (this.placingIsBlocked(this.placeTarget.add((Vec3i)pos)) || this.placingIsBlocked(this.placeTarget.add((Vec3i)pos.down()))) {
                this.rotationPlaceableX = false;
            }
        }
        for (final BlockPos pos : BodyParts.ArmsZ) {
            if (this.placingIsBlocked(this.placeTarget.add((Vec3i)pos)) || this.placingIsBlocked(this.placeTarget.add((Vec3i)pos.down()))) {
                this.rotationPlaceableZ = false;
            }
        }
        for (final BlockPos pos : BodyParts.head) {
            if (this.placingIsBlocked(this.placeTarget.add((Vec3i)pos))) {
                noRotationPlaceable = false;
            }
        }
        return !isShitGrass && noRotationPlaceable && (this.rotationPlaceableX || this.rotationPlaceableZ);
    }

    private boolean testSnowGolemStructure() {
        boolean noRotationPlaceable = true;
        boolean isShitGrass = false;
        if (AutoWither.mc.world.getBlockState(this.placeTarget) == null) {
            return false;
        }
        final Block block = AutoWither.mc.world.getBlockState(this.placeTarget).getBlock();
        if (block instanceof BlockTallGrass || block instanceof BlockDeadBush) {
            isShitGrass = true;
        }
        if (getPlaceableSide(this.placeTarget.up()) == null) {
            return false;
        }
        for (final BlockPos pos : BodyParts.bodyBase) {
            if (this.placingIsBlocked(this.placeTarget.add((Vec3i)pos))) {
                noRotationPlaceable = false;
            }
        }
        for (final BlockPos pos : BodyParts.head) {
            if (this.placingIsBlocked(this.placeTarget.add((Vec3i)pos))) {
                noRotationPlaceable = false;
            }
        }
        return !isShitGrass && noRotationPlaceable;
    }

    private static void placeBlock(final BlockPos pos, final boolean rotate) {
        final EnumFacing side = getPlaceableSide(pos);
        if (side == null) {
            return;
        }
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        final Vec3d hitVec = new Vec3d((Vec3i)neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final Block neighbourBlock = AutoWither.mc.world.getBlockState(neighbour).getBlock();
        if (!AutoWither.isSneaking && (BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock))) {
            AutoWither.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoWither.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            AutoWither.isSneaking = true;
        }
        if (rotate) {
            faceVectorPacketInstant(hitVec);
        }
        AutoWither.mc.playerController.processRightClickBlock(AutoWither.mc.player, AutoWither.mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        AutoWither.mc.player.swingArm(EnumHand.MAIN_HAND);
        AutoWither.mc.rightClickDelayTimer = 4;
    }

    public static void faceVectorPacketInstant(final Vec3d vec) {
        final float[] rotations = RotationUtil.getLegitRotations(vec);
        AutoWither.mc.getConnection().sendPacket((Packet)new CPacketPlayer.Rotation(rotations[0], rotations[1], AutoWither.mc.player.onGround));
    }

    @Override
    public int onUpdate() {
        if (AutoWither.mc.player == null) {
            return 0;
        }
        if (this.buildStage == 1) {
            AutoWither.isSneaking = false;
            this.rotationPlaceableX = false;
            this.rotationPlaceableZ = false;
            if (!this.checkBlocksInHotbar()) {
                if (this.debug.getValue()) {
                    Command.sendMessage("[AutoSpawner] " + ChatFormatting.RED.toString() + "Blocks missing for: " + ChatFormatting.RESET.toString() + "Wither" + ChatFormatting.RED.toString() + ", disabling.");
                }
                this.disable();
                return 0;
            }
            final List<BlockPos> blockPosList = getSphere(AutoWither.mc.player.getPosition().down(), this.placeRange.getValue(), this.placeRange.getValue().intValue(), false, true, 0);
            boolean noPositionInArea = true;
            for (final BlockPos pos : blockPosList) {
                this.placeTarget = pos.down();
                if (this.testStructure()) {
                    noPositionInArea = false;
                    break;
                }
            }
            if (noPositionInArea) {
                if (this.useMode.getValue().equals(UseMode.SINGLE)) {
                    if (this.debug.getValue()) {
                        Command.sendMessage("[AutoSpawner] " + ChatFormatting.RED.toString() + "Position not valid, disabling.");
                    }
                    this.disable();
                }
                return 0;
            }
            AutoWither.mc.player.inventory.currentItem = this.bodySlot;
            for (final BlockPos pos2 : BodyParts.bodyBase) {
                placeBlock(this.placeTarget.add((Vec3i)pos2), this.rotate.getValue());
            }
            if (this.rotationPlaceableX) {
                for (final BlockPos pos2 : BodyParts.ArmsX) {
                    placeBlock(this.placeTarget.add((Vec3i)pos2), this.rotate.getValue());
                }
            }
            else if (this.rotationPlaceableZ) {
                for (final BlockPos pos2 : BodyParts.ArmsZ) {
                    placeBlock(this.placeTarget.add((Vec3i)pos2), this.rotate.getValue());
                }
            }
            this.buildStage = 2;
        }
        else if (this.buildStage == 2) {
            AutoWither.mc.player.inventory.currentItem = this.headSlot;
            if (this.rotationPlaceableX) {
                for (final BlockPos pos : BodyParts.headsX) {
                    placeBlock(this.placeTarget.add((Vec3i)pos), this.rotate.getValue());
                }
            }
            else if (this.rotationPlaceableZ) {
                for (final BlockPos pos : BodyParts.headsZ) {
                    placeBlock(this.placeTarget.add((Vec3i)pos), this.rotate.getValue());
                }
            }
            if (AutoWither.isSneaking) {
                AutoWither.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoWither.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                AutoWither.isSneaking = false;
            }
            if (this.useMode.getValue().equals(UseMode.SINGLE)) {
                this.disable();
            }
            this.buildStage = 3;
        }
        else if (this.buildStage == 3) {
            if (this.delayStep < this.delay.getValue()) {
                ++this.delayStep;
            }
            else {
                this.delayStep = 1;
                this.buildStage = 1;
            }
        }
        return 0;
    }

    public static List<BlockPos> getSphere(final BlockPos loc, final float r, final int h, final boolean hollow, final boolean sphere, final int plus_y) {
        final List<BlockPos> circleblocks = new ArrayList<BlockPos>();
        final int cx = loc.getX();
        final int cy = loc.getY();
        final int cz = loc.getZ();
        for (int x = cx - (int)r; x <= cx + r; ++x) {
            for (int z = cz - (int)r; z <= cz + r; ++z) {
                for (int y = sphere ? (cy - (int)r) : cy; y < (sphere ? (cy + r) : ((float)(cy + h))); ++y) {
                    final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0);
                    if (dist < r * r && (!hollow || dist >= (r - 1.0f) * (r - 1.0f))) {
                        final BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    private boolean placingIsBlocked(final BlockPos pos) {
        final Block block = AutoWither.mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir)) {
            return true;
        }
        for (final Entity entity : AutoWither.mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                return true;
            }
        }
        return false;
    }

    private enum UseMode
    {
        SINGLE,
        SPAM;
    }

    private static class BodyParts
    {
        private static final BlockPos[] bodyBase;
        private static final BlockPos[] ArmsX;
        private static final BlockPos[] ArmsZ;
        private static final BlockPos[] headsX;
        private static final BlockPos[] headsZ;
        private static final BlockPos[] head;

        static {
            bodyBase = new BlockPos[] { new BlockPos(0, 1, 0), new BlockPos(0, 2, 0) };
            ArmsX = new BlockPos[] { new BlockPos(-1, 2, 0), new BlockPos(1, 2, 0) };
            ArmsZ = new BlockPos[] { new BlockPos(0, 2, -1), new BlockPos(0, 2, 1) };
            headsX = new BlockPos[] { new BlockPos(0, 3, 0), new BlockPos(-1, 3, 0), new BlockPos(1, 3, 0) };
            headsZ = new BlockPos[] { new BlockPos(0, 3, 0), new BlockPos(0, 3, -1), new BlockPos(0, 3, 1) };
            head = new BlockPos[] { new BlockPos(0, 3, 0) };
        }
    }
}