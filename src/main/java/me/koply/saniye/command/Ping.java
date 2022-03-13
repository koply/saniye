package me.koply.saniye.command;

import me.koply.kcommando.internal.annotations.HandleCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Ping {

    @HandleCommand(name = "ping", aliases = "ping")
    public void handle(MessageReceivedEvent e) {
        e.getChannel().sendMessage("eved kardesim helikopter").queue();
    }

}