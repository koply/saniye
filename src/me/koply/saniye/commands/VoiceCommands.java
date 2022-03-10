package me.koply.saniye.commands;

import com.eclipsesource.json.JsonObject;
import me.koply.kcommando.internal.annotations.HandleCommand;
import me.koply.saniye.Main;
import me.koply.saniye.data.DataHelper;
import me.koply.saniye.util.Util;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.stream.Collectors;

public class VoiceCommands {

    private static final List<Permission> CONNECT = List.of(Permission.VOICE_CONNECT);

    @HandleCommand(name = "ac", aliases = "ac", guildOnly = true)
    public void ac(MessageReceivedEvent e, String[] args, String prefix) {
        if (args.length == 1 || e.getMember() == null) {
            e.getChannel().sendMessage("geçersiz kullanım").queue();
            return;
        }

        var userData = DataHelper.getUserVCData(e.getGuild(), e.getMember());
        var memberVoiceState = e.getMember().getVoiceState();

        String vcid;

        if (userData == null || memberVoiceState == null ||
                memberVoiceState.getChannel() == null ||
                !memberVoiceState.getChannel().getId().equals((vcid = userData.get("vcid").asString()))) {
            e.getChannel().sendMessage("Bu komutu kullanmak için odanızın olması gerekiyor.").queue();
            return;
        }

        var guild = e.getGuild();
        var channel = guild.getVoiceChannelById(vcid);
        if (channel == null) return;

        var content = e.getMessage().getContentDisplay();
        var cmd = args[1];
        var realContent = content.substring(prefix.length() + cmd.length() + args[0].length() + 1);
        System.out.println(realContent);
        switch (cmd) {
            case "name" -> channel.getManager().setName(realContent).queue();
            case "close", "kapat", "kapa" -> channel.getManager().putRolePermissionOverride(guild.getPublicRole().getIdLong(), null, CONNECT).queue();
            case "open", "aç", "ac" -> channel.getManager().putRolePermissionOverride(guild.getPublicRole().getIdLong(), CONNECT, null).queue();
            case "userlimit", "limit", "sınır", "sinir" -> {
                var value = Util.parseInt(realContent);
                if (value == null || value > 99 || value < 0) {
                    e.getChannel().sendMessage("0-99 aralığında bir sayı girin. 0, limiti kaldıracaktır.").queue();
                    return;
                } else channel.getManager().setUserLimit(value).queue();
            }
            case "kick", "at" -> {
                var founds = findInVoiceChannel(e, channel, realContent);
                if (founds.isEmpty()) e.getChannel().sendMessage("adam odada yok").queue();
                else guild.kickVoiceMember(founds.get(0)).queue();
            }
            case "ban", "yasakla" -> {
                var founds = findInVoiceChannel(e, channel, realContent);
                if (founds.isEmpty()) e.getChannel().sendMessage("adam odada yok").queue();
                else {
                    guild.kickVoiceMember(founds.get(0)).queue();
                    channel.getManager().putMemberPermissionOverride(founds.get(0).getIdLong(), null, CONNECT).queue();
                }
            }
            case "whitelist" -> {

            }
            case "tc", "text", "yazı", "textchannel" -> {

            }
        }
        e.getChannel().sendMessage("okay").queue();
    }

    private List<Member> findInVoiceChannel(MessageReceivedEvent e, VoiceChannel channel, String realContent) {
        var members = channel.getMembers();
        var mentionedMembers = e.getMessage().getMentionedMembers();
        var member = members.stream();
        return mentionedMembers.isEmpty()
                ? member.filter(mem -> mem.getId().equals(realContent)).collect(Collectors.toList())
                : member.filter(mem -> mem.getId().equals(mentionedMembers.get(0).getId())).collect(Collectors.toList());
    }

    @HandleCommand(name = "customvc", aliases = "customvc")
    public void customVCSelection(MessageReceivedEvent e, String[] args) {
        if (e.getMember() == null) return;
        if (!e.getMember().hasPermission(Permission.ADMINISTRATOR)) return;

        if (args.length == 1) {
            e.getChannel().sendMessage("Otomatik ses kanalı lobisinin idsini girin.").queue();
            return;
        }

        var id = args[1];
        var vc = e.getGuild().getVoiceChannelById(id);
        if (vc == null) {
            e.getChannel().sendMessage("Girilen idye sahip ses kanalı bulunamadı.").queue();
            return;
        }

        var value = Main.DATA.putAndGet(e.getGuild().getId()).asObject();
        value.add("customvcid", vc.getId());
        value.add("vcdata", new JsonObject());
        e.getChannel().sendMessage("halletim").queue();
    }

}

/*
ac name (channel name)
ac close/open (locks for @everyone)
ac userlimit <int> [1,99]
ac kick/ban @mention
ac whitelist add/remove @mention
ac tc -> creates text channel
spam detector

data
{
    12313guildid12: {
        "customvcid": 123134213,
        "vcdata": {
            1234567userid89123456: {
                "vcid": 12313123,
                "tcid": 123131,
                "whitelist": boolean,
                "status": true-open / false-close ; boolean

            }
        }
    }
}
 */