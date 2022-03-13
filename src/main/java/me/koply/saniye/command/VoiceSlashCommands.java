package me.koply.saniye.command;

import me.koply.kcommando.internal.OptionType;
import me.koply.kcommando.internal.annotations.HandleSlash;
import me.koply.kcommando.internal.annotations.Option;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class VoiceSlashCommands {

    @HandleSlash(name = "test", desc = "testing slash", guildId = 674334330444709904L,
            options = @Option(type = OptionType.STRING, name = "value", required = true))
    public void ac(SlashCommandInteractionEvent e) {

    }

}