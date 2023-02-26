package me.alpha432.oyvey.features.modules.player;

import net.minecraft.init.Items;
import me.alpha432.oyvey.features.modules.Module;


public class FastExp extends Module {


        public FastExp() {
        super("FastExp", "Allows You To Throw XP Fast", Category.PLAYER, false, false,false);
        }

        @Override
        public void onUpdate() {
        if (mc.world == null || mc.player == null)
        return;

        if (mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem).getItem() == Items.EXPERIENCE_BOTTLE) {
        mc.rightClickDelayTimer = 0;
        }

        }
        }
