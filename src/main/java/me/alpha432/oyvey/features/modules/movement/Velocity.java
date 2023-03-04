package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.world.Explosion;

public class Velocity extends Module {
    public static final Velocity INSTANCE = new Velocity();

    public final Setting<Float> horizontal = this.register(new Setting("Horizontal", 0, 0, 1));
    public final Setting<Float> vertical = this.register(new Setting("Vertical", 0, 0, 1));

    public Velocity() {
        super("Velocity", "velocity", Category.MOVEMENT, true,false,false);
    }

    public void onReceivePacket(PacketEvent event) {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.player == null) {
            return;
        }

        if (event.getPacket() instanceof SPacketEntityVelocity) {
            SPacketEntityVelocity velPacket = (SPacketEntityVelocity) event.getPacket();

            if (velPacket.getEntityID() == mc.player.getEntityId()) {

                if (horizontal.getValue() != 0) {
                    mc.player.motionX = (double)velPacket.getMotionX() * horizontal.getValue() / 8000.0D;
                    mc.player.motionZ = (double)velPacket.getMotionZ() * horizontal.getValue() / 8000.0D;
                }

                if (vertical.getValue() != 0) {
                    mc.player.motionY = (double)velPacket.getMotionY() * vertical.getValue() / 8000.0D;
                }

                event.setCanceled(true);
            }
        }

        if (event.getPacket() instanceof SPacketExplosion) {
            SPacketExplosion expPacket = (SPacketExplosion) event.getPacket();

            Explosion explosion = new Explosion(mc.world, null, expPacket.getX(), expPacket.getY(), expPacket.getZ(), expPacket.getStrength(), expPacket.getAffectedBlockPositions());
            explosion.doExplosionB(true);

            if (horizontal.getValue() != 0) {
                mc.player.motionX += expPacket.getMotionX() * horizontal.getValue();
                mc.player.motionZ += expPacket.getMotionZ() * horizontal.getValue();
            }

            if (vertical.getValue() != 0) {
                mc.player.motionY += expPacket.getMotionY() * vertical.getValue();
            }

            event.setCanceled(true);
        }
    }
}