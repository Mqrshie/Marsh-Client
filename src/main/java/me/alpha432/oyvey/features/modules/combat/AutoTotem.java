package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.*;
import me.alpha432.oyvey.util.*;
import net.minecraft.init.*;
import net.minecraft.item.*;

public class AutoTotem extends Module
{
    public Setting<Boolean> packet;
    public Setting<Boolean> doublE;

    public AutoTotem() {
        super("AutoTotem", "Auto Equips Totem" ,Category.COMBAT, false, false, false);
        this.packet = (Setting<Boolean>)this.register(new Setting("Packet", true));
        this.doublE = (Setting<Boolean>)this.register(new Setting("Double", true));
    }

    @Override
    public void onTick() {
        if (this.nullCheck()) {
            return;
        }
        if (this.shouldTotem()) {
            this.doTotem();
        }
    }

    @Override
    public int onUpdate() {
        if (this.nullCheck()) {
            return 0;
        }
        if (this.shouldTotem() && this.doublE.getValue()) {
            this.doTotem();
        }
        return 0;
    }

    public void doTotem() {
        final int totem = this.findTotemSlot();
        if (totem == -1) {
            return;
        }
        InventoryUtil.moveItemTo(totem, InventoryUtil.offhandSlot);
    }

    public boolean shouldTotem() {
        return AutoTotem.mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING;
    }

    public int findTotemSlot() {
        for (int i = 0; i < AutoTotem.mc.player.inventoryContainer.getInventory().size(); ++i) {
            if (i != InventoryUtil.offhandSlot) {
                final ItemStack stack = (ItemStack)AutoTotem.mc.player.inventoryContainer.getInventory().get(i);
                if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                    return i;
                }
            }
        }
        return -1;
    }
}