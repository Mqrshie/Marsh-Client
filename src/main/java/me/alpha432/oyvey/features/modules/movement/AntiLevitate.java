package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.potion.Potion;

import java.util.Objects;

public class AntiLevitate
        extends Module {
    public AntiLevitate() {
        super("AntiLevitate", "Removes shulker levitation", Module.Category.MOVEMENT, false, false, false);
    }

    @Override
    public int onUpdate() {
        if (AntiLevitate.mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionFromResourceLocation("levitation")))) {
            AntiLevitate.mc.player.removeActivePotionEffect(Potion.getPotionFromResourceLocation("levitation"));
        }
        return 0;
    }
}