package org.titanic.util;

import com.google.gson.*;
import org.titanic.cryptosx.dto.message.answer.Level2DataEvent;
import org.titanic.cryptosx.dto.message.answer.TradeDataUpdateAnswer;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;

/**
 * @author Hanno Skowronek, Chung Ho Chiu
 */
public final class JSONHelper {

    private static Gson gson;
    public static String toJsonString(Object obj){
        return getGson().toJson(obj);
    }
    public static <T> T toObject(String json, Class<T> clazz){
        return getGson().fromJson(json, clazz);
    }

    public static <T> T toObject(String json, Type type){
        return getGson().fromJson(json, type);
    }
    public static ArrayList<Level2DataEvent> toLevel2Data(String json){
        double[][] array = getGson().fromJson(json, double[][].class);
        ArrayList<Level2DataEvent> updates = new ArrayList<>();
        for (double[] line: array) {
            updates.add(new Level2DataEvent((int)line[0],(int)line[1],Instant.ofEpochMilli((long)line[2]), (int)line[3],line[4],(int)line[5],line[6],(int)line[7],line[8],(int)line[9]));
        }
        return updates;
    }

    public static ArrayList<TradeDataUpdateAnswer> toTradeDataUpdate(String json){
        double[][] array = getGson().fromJson(json, double[][].class);
        ArrayList<TradeDataUpdateAnswer> updates = new ArrayList<>();
        for (double[] line: array) {
            updates.add(new TradeDataUpdateAnswer((int)line[0],(int)line[1],line[2], line[3], (int) line[4],(int) line[5], Instant.ofEpochMilli((long) line[6]),(int) line[7],(int) line[8], line[9] == 1.0, (int) line[9]));
        }
        return updates;
    }
    public static JsonObject fromJson(String json) {
        return getGson().fromJson(json, JsonObject.class);
    }

    private static Gson getGson() {
        // Creates the json object which will manage the information received
        if (gson == null) {
            GsonBuilder builder = new GsonBuilder();

            // Register an adapter to manage the date types as long values
            builder.registerTypeAdapter(Instant.class, (JsonDeserializer<Instant>) (json, typeOfT, context) -> Instant.ofEpochMilli(json.getAsJsonPrimitive().getAsLong()));
            gson = builder.create();
        }
        return gson;
    }
}

