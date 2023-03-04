package com.mqrshie.marsh.events;

import me.alpha432.oyvey.event.EventStage;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class PacketEvent2
        extends EventStage {
    private final Packet<?> packet2;

    public PacketEvent2(int stage, Packet<?> packet) {
        super(stage);
        this.packet2 = packet;
    }

    public <T extends Packet<?>> T getPacket() {
        return (T) this.packet2;
    }

    @Cancelable
    public static class Send
            extends PacketEvent2 {
        public Send(int stage, Packet<?> packet) {
            super(stage, packet);
        }
    }

    @Cancelable
    public static class Receive
            extends PacketEvent2 {
        public Receive(int stage, Packet<?> packet) {
            super(stage, packet);
        }
    }
}