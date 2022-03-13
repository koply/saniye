package me.koply.saniye;

import me.koply.saniye.data.DataManager;

import java.io.File;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Logger;

public class Main {

    public static final Logger log = Logger.getLogger("Bot");

    private static final File CONFIG_FILE = new File("./config.json");
    public static final DataManager CONFIG = new DataManager(CONFIG_FILE, null, new String[]{"token", "activity"}, false);

    private static final File DATA_FILE = new File("./data.dat");
    public static final DataManager DATA = new DataManager(DATA_FILE, true);

    private static final Bot bot = new Bot();

    public static void main(String[] args) {
        if (CONFIG.readData() != 0) {
            System.out.println("Config file created. Fill the blank quotes...");
            System.exit(30);
            return;
        }

        var tokenJson = CONFIG.get("token");
        if (tokenJson == null || tokenJson.isNull()) {
            System.out.println("Token not found...");
            System.exit(30);
            return;
        }

        DATA.readData();

        var botThread = new Thread(() -> bot.startBot(tokenJson.asString()));
        botThread.start();

        var sc = new Scanner(System.in);
        String entry = "";
        while (true) {
            System.out.print("> ");
            entry = sc.nextLine().toLowerCase(Locale.ROOT);

            switch (entry) {
                case "quit", "exit" -> {
                    bot.stop();
                    System.exit(31);
                }
                default -> System.out.println(":)");
            }
        }
    }

}
