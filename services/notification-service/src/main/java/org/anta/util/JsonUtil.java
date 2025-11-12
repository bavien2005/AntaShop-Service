package org.anta.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {


    // lop nay chuyen doi du lieu giua 2 doi tuong java object va Json
    private static final ObjectMapper M = new ObjectMapper();


    // chuyen doi tuong san kieu Json {" " , " "}
    public static String toJson(Object o) {
        try {
            return M.writeValueAsString(o);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // nguoc lai chuyen Json thanh doi tuong trong java
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return M.readValue(json, clazz);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // phuc tap hon , dung de chuyen tu map , list sang Json nhu : List<User>, Map<String, Object>
    public static <T> T fromJson(String json,
            com.fasterxml.jackson.core.type.TypeReference<T> typeRef) {
        try {
            return M.readValue(json, typeRef);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
