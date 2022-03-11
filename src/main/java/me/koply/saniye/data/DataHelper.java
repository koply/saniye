package me.koply.saniye.data;

import com.eclipsesource.json.JsonObject;
import me.koply.saniye.Main;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import static me.koply.saniye.util.JsonUtil.*;

public class DataHelper {

    public static JsonObject getUserVCData(Guild guild, Member member) {
        return getUserVCData(Main.DATA.getDataJson(), guild.getId(), member.getId());
    }

    public static JsonObject getUserVCData(JsonObject data, Guild guild, Member member) {
        return getUserVCData(data, guild.getId(), member.getId());
    }

    public static JsonObject getUserVCData(JsonObject data, String guildID, String userID) {
        var guildJson = data.get(guildID);
        if (isNull(guildJson)) return null;

        var vcDataJson = guildJson.asObject().get("vcdata");
        if (isNull(vcDataJson)) return null;

        var userDataJson = vcDataJson.asObject().get(userID);
        if (isNull(userDataJson)) return null;
        return userDataJson.asObject();
    }

}