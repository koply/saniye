package me.koply.saniye.util;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class JsonUtil {

    public static JsonObject createObjectIfAbsent(JsonObject first, String key) {
        var value = first.get(key);
        if (value == null) {
            var obj = new JsonObject();
            first.add(key, obj);
            return obj;
        } else return value.asObject();
    }

    public static boolean isNull(JsonValue val) {
        return val == null || val.isNull();
    }

}