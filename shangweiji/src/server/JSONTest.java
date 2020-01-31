package server;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.HashMap;

public class JSONTest {
    //    {"file":"http://localhost:8080/root/rabbit.mp3","currentTime":0,"paused":false,"ended":false,"loop":true,"volume":1}
    public static void main(String[] args) {
        JsonObject object = getJsonObject("{\"file\":\"http://localhost:8080/root/rabbit.mp3\",\"currentTime\":0,\"paused\":false,\"ended\":false,\"loop\":true,\"volume\":1}");
        HashMap<String,String> map =new HashMap<>();
        for(String s:object.keySet()) map.put(s,  object.get(s).toString());
        System.out.println(map);
    }

    /**
     *      * Read JSON object from String input
     *      * @param input String to be parsed
     *      * @return JsonObject
     *      
     */
    public static JsonObject getJsonObject(String input) {
        try (JsonReader jsonReader = Json.createReader(new StringReader(input))) {
            return jsonReader.readObject();
        }
    }

}
