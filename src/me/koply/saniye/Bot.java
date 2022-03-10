package me.koply.saniye;

import me.koply.kcommando.KCommando;
import me.koply.kcommando.integration.impl.jda.JDAIntegration;
import me.koply.saniye.commands.Ping;
import me.koply.saniye.event.VoiceListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class Bot {

    private JDA jda;

    public void startBot(String token) {
        var activityNode = Main.CONFIG.get("activity");
        var activity = activityNode.isNull() ? "Ğ" : activityNode.asString();

        try {
            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGES)
                    .enableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                    .disableCache(CacheFlag.EMOTE)
                    .setActivity(Activity.watching(activity))
                    .setAutoReconnect(true)
                    .build();

            jda.addEventListener(new VoiceListener());
            jda.awaitReady();

            var integration = new JDAIntegration(jda);
            var kcm = new KCommando(integration)
                    .setPrefix(".")
                    .setVerbose(true)
                    .addPackagePath(Ping.class.getPackage().getName())
                    .build();

        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        jda.shutdownNow();
    }

}