package com.anthonyzero.sdk.client.utils;

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : jin.ping
 * @date : 2024/2/26
 */
public class GsonUtil {

    private static Gson gson = null;

    private GsonUtil() {
    }

    public static JsonElement toJsonTree(Object object) {
        return gson.toJsonTree(object);
    }

    public static <T> T fromJson(JsonElement jsonElement, Class<T> clazz) {
        return gson.fromJson(jsonElement, clazz);
    }

    public static <T> T fromJson(JsonElement jsonElement, Type type) {
        return gson.fromJson(jsonElement, type);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }

    public static String toString(Object object) {
        String gsonString = null;
        if (gson != null) {
            gsonString = gson.toJson(object);
        }

        return gsonString;
    }

    public static <T> T toBean(String gsonString, Class<T> cls) {
        T t = null;
        if (gson != null) {
            t = gson.fromJson(gsonString, cls);
        }

        return t;
    }

    public static <T> T toBean(String gsonString, Type typeOfT) {
        T t = null;
        if (gson != null) {
            t = gson.fromJson(gsonString, typeOfT);
        }

        return t;
    }

    public static <T> List<T> toList(String gsonString) {
        Gson gson = (new GsonBuilder()).registerTypeAdapter((new TypeToken<List<T>>() {
        }).getType(), new MapTypeAdapter()).create();
        return gson.fromJson(gsonString, (new TypeToken<List<T>>() {
        }).getType());
    }

    public static <T> List<Map<String, T>> toListMaps(String gsonString) {
        Gson gson = (new GsonBuilder()).registerTypeAdapter((new TypeToken<List<Map<String, T>>>() {
        }).getType(), new MapTypeAdapter()).create();
        return gson.fromJson(gsonString, (new TypeToken<List<Map<String, T>>>() {
        }).getType());
    }

    public static <T> Map<String, T> toMaps(String gsonString) {
        Gson gson = (new GsonBuilder()).registerTypeAdapter((new TypeToken<Map<String, T>>() {
        }).getType(), new MapTypeAdapter()).create();
        return gson.fromJson(gsonString, (new TypeToken<Map<String, T>>() {
        }).getType());
    }

    static {
        gson = (new GsonBuilder()).serializeNulls()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                //.setObjectToNumberStrategy(ToNumberPolicy.LAZILY_PARSED_NUMBER)
                .setNumberToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        private static final String LOCAL_DATE_FORMAT = "yyyy-MM-dd";

        LocalDateAdapter() {
        }

        public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String dateString = json.getAsJsonPrimitive().getAsString();
            if (dateString == null) {
                return null;
            } else {
                return dateString.length() > 10 ? LocalDate.parse(dateString.substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd")) : LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
        }
    }

    static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final String LOCAL_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

        LocalDateTimeAdapter() {
        }

        public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String dateString = json.getAsJsonPrimitive().getAsString();
            if (dateString == null) {
                return null;
            } else {
                return dateString.length() == 10 ? LocalDateTime.parse(dateString + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        }
    }

    public static class MapTypeAdapter extends TypeAdapter<Object> {
        public MapTypeAdapter() {
        }

        public Object read(JsonReader in) throws IOException {
            JsonToken token = in.peek();
            switch (token) {
                case BEGIN_ARRAY:
                    List<Object> list = new ArrayList();
                    in.beginArray();

                    while(in.hasNext()) {
                        list.add(this.read(in));
                    }

                    in.endArray();
                    return list;
                case BEGIN_OBJECT:
                    Map<String, Object> map = new LinkedTreeMap();
                    in.beginObject();

                    while(in.hasNext()) {
                        map.put(in.nextName(), this.read(in));
                    }

                    in.endObject();
                    return map;
                case STRING:
                    return in.nextString();
                case NUMBER:
                    double dbNum = in.nextDouble();
                    if (dbNum > 9.223372036854776E18) {
                        return dbNum;
                    } else if (dbNum > 2.147483647E9) {
                        long lngNum = (long)dbNum;
                        if (dbNum == (double)lngNum) {
                            return lngNum;
                        }

                        return dbNum;
                    } else {
                        int intNum = (int)dbNum;
                        if (dbNum == (double)intNum) {
                            return intNum;
                        }

                        return dbNum;
                    }
                case BOOLEAN:
                    return in.nextBoolean();
                case NULL:
                    in.nextNull();
                    return null;
                default:
                    throw new IllegalStateException();
            }
        }

        public void write(JsonWriter out, Object value) throws IOException {
        }
    }
}
