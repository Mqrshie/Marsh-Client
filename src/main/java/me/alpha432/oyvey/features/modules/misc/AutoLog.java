package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.MathUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoLog
        extends Module {
    private final Setting<Float> health = this.register(new Setting <>("Health", 16.0f, 0.1f, 36.0f));
    private final Setting<Boolean> bed = this.register(new Setting <>("Beds", true));
    private final Setting<Float> range = this.register(new Setting<Object>("BedRange", 6.0f, 0.1f, 36.0f, v -> this.bed.getValue()));
    private final Setting<Boolean> logout = this.register(new Setting <>("LogoutOff", true));
    private static AutoLog INSTANCE = new AutoLog();

    public AutoLog() {
        super("AutoLog", "Logs when in danger.", Module.Category.MISC, false, false, false);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static AutoLog getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AutoLog();
        }
        return INSTANCE;
    }

    @Override
    public int onTick() {
        if (!AutoLog.nullCheck() && AutoLog.mc.player.getHealth() <= this.health.getValue()) {
            OyVey.moduleManager.disableModule("AutoReconnect");
            AutoLog.mc.player.connection.sendPacket(new SPacketDisconnect(new TextComponentString("AutoLogged")));
            if (this.logout.getValue()) {
                this.disable();
            }
        }
        return 0;
    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvent.Receive event) {
        SPacketBlockChange packet;
        if (event.getPacket() instanceof SPacketBlockChange && this.bed.getValue() && (packet = event.getPacket()).getBlockState().getBlock() == Blocks.BED && AutoLog.mc.player.getDistanceSqToCenter(packet.getBlockPosition()) <= MathUtil.square(this.range.getValue())) {
            OyVey.moduleManager.disableModule("AutoReconnect");
            AutoLog.mc.player.connection.sendPacket(new SPacketDisconnect(new TextComponentString("AutoLogged")));
            if (this.logout.getValue()) {
                this.disable();
            }
        }
    }
}