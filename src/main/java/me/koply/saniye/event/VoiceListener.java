package me.koply.saniye.event;

import me.koply.saniye.Main;
import me.koply.saniye.util.JsonUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class VoiceListener extends ListenerAdapter {

    private static final List<Permission> voicePerms = List.of(Permission.VOICE_CONNECT,
            Permission.VOICE_SPEAK,
            Permission.VOICE_START_ACTIVITIES,
            Permission.VOICE_STREAM);

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent e) {
        var guild = e.getGuild();
        var node = Main.DATA.get(guild.getId());
        if (node == null || node.isNull()) return;
        var guildData = node.asObject();
        var vcidvalue = guildData.get("customvcid");
        var vcid = vcidvalue == null || vcidvalue.isNull() ? null : vcidvalue.asString();
        if (vcid == null) return;
        if (!e.getChannelJoined().getId().equals(vcid)) return;

        var detectedCategories = e.getGuild().getCategories().stream()
                .filter(category -> (category.getChannels().stream().anyMatch(ch -> ch.getId().equals(vcid))))
                .collect(Collectors.toList());
        var detectedCategory = detectedCategories.get(0);
        detectedCategory.createVoiceChannel("\uD83D\uDCAC " + e.getMember().getEffectiveName())
                .addMemberPermissionOverride(e.getMember().getIdLong(), voicePerms, null)
                .queue(succ -> {
                    var vcdata = JsonUtil.createObjectIfAbsent(guildData, "vcdata");
                    var vcdatauser = JsonUtil.createObjectIfAbsent(vcdata, e.getMember().getId());
                    vcdatauser.add("vcid", succ.getId());

                    guild.moveVoiceMember(e.getMember(), succ).queue();
                });
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent e) {
        var guild = e.getGuild();
        var node = Main.DATA.get(guild.getId());
        if (node.isNull()) return;
        var guildData = node.asObject();

        var vcdatavalue = guildData.get("vcdata");
        if (vcdatavalue == null || vcdatavalue.isNull()) return;
        var vcdata = vcdatavalue.asObject();

        var userdataval = vcdata.get(e.getMember().getId());
        if (userdataval == null || userdataval.isNull()) return;
        var userdata = userdataval.asObject();

        if (!e.getChannelLeft().getId().equals(userdata.getString("vcid", ""))) return;
        var tcidval = userdata.getString("tcid", null);

        var membersInChannel = e.getChannelLeft().getMembers();
        if (membersInChannel.isEmpty()) {
            e.getChannelLeft().delete().queue(succ -> vcdata.remove(e.getMember().getId()));e.getChannelLeft().delete().queue(succ -> vcdata.remove(e.getMember().getId()));
            if (tcidval != null) {
                var textChannel = e.getGuild().getTextChannelById(tcidval);
                if (textChannel != null) textChannel.delete().queue(); // TODO: archive
            }
        }
    }
}