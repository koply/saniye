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
        var guildval = data.get(guildID);
        if (isNull(guildval)) return null;

        var vcdataval = guildval.asObject().get("vcdata");
        if (isNull(vcdataval)) return null;

        var userdataval = vcdataval.asObject().get(userID);
        if (isNull(userdataval)) return null;
        return userdataval.asObject();
    }

}