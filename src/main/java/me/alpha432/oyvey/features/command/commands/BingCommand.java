package me.alpha432.oyvey.features.command.commands;

import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.util.Wrapper;

import java.awt.*;
import java.net.URI;


public class BingCommand
        extends Command {

    //this module was made because when Mqrshie kept testing they would type .bing clickgui z instead of .bind lmao
    public BingCommand() {
        super("bing", new String[]{"<module>", "<bing>"});
    }

    @Override
    public void execute(String[] var1) {
        if(Wrapper.mc.world != null && Wrapper.getPlayer() != null) {
            try {
                Desktop.getDesktop().browse(URI.create("https://www.bing.com"));
            } catch (Exception ignored) {
            }

            BindCommand.sendMessage("bing!");

        }

    }
    }



