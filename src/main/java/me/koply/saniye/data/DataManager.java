package me.koply.saniye.data;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import me.koply.kcommando.internal.CronService;
import me.koply.saniye.util.FileUtil;

import java.io.*;
import java.util.Arrays;

public class DataManager {

    public static class MissingRequiredKeyException extends RuntimeException {
        public MissingRequiredKeyException(String str) { super(str); }
    }

    public final String[] requiredKeys;
    public final String[] defaultKeys;

    private final File dataFile;
    public DataManager(File dataFile, boolean autoSave) {
        this(dataFile, null, null, autoSave);
    }

    public DataManager(File dataFile, String[] requiredKeys, String[] defaultKeys) {
        this(dataFile, requiredKeys, defaultKeys, false);
    }

    public DataManager(File dataFile, String[] requiredKeys, String[] defaultKeys, boolean autoSave) {
        this.dataFile = dataFile;
        this.requiredKeys = requiredKeys;
        this.defaultKeys = defaultKeys;
        if (autoSave) {
            // windows and linux shutdown hook
            CronService.getInstance().addRunnable(this::saveData, 5);
            Runtime.getRuntime().addShutdownHook(new Thread(this::saveData, "TerminateProcess"));
            Runtime.getRuntime().addShutdownHook(new Thread(this::saveData, "Shutdown-thread"));
        }
    }

    private JsonObject dataJson;
    public JsonObject getDataJson() {
        return dataJson;
    }

    public JsonValue get(String key) {
        if (dataJson == null) throw new RuntimeException("ConfigData null. Call readConfig() before getProperty()");
        return dataJson.get(key);
    }

    public JsonValue putAndGet(String key) {
        var data = dataJson.get(key);
        if (data != null) return data;
        var obj = new JsonObject();
        dataJson.add(key, obj);
        return obj;
    }

    public void saveData() {
        if (dataJson != null) FileUtil.writeFile(dataFile, dataJson.toString());
    }

    /**
     * @return 0 -> success, 1 -> config created
     */
    public int readData() {
        if (dataFile == null || dataFile.isDirectory()) {
            throw new IllegalArgumentException("Invalid configFile.");
        } if (!dataFile.exists()) {
            try { if (dataFile.createNewFile()) {
                fillDefaultConfig();
            }} catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        var dataString = FileUtil.readFile(dataFile);
        if (dataString.isBlank()) {
            fillDefaultConfig();
        }

        var tempJson = Json.parse(dataString).asObject();
        if (requiredKeys != null && !tempJson.names().containsAll(Arrays.asList(requiredKeys))) {
            throw new MissingRequiredKeyException("Missing required key in config. Required keys: " + Arrays.toString(requiredKeys));
        }

        dataJson = tempJson;
        return 0;
    }

    private void fillDefaultConfig() {
        var tempJson = new JsonObject();
        if (defaultKeys != null) for (var key : defaultKeys) {
            tempJson.add(key, "");
        }

        try  {
            /*var writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(configFile), StandardCharsets.UTF_8));
            tempJson.writeTo(writer, WriterConfig.PRETTY_PRINT);*/
            // TODO: create fake writer stream for json pretty printer
            FileUtil.writeFile(dataFile, tempJson.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}