package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import model.Category;
import model.Event;
import org.json.JSONArray;
import org.json.JSONObject;
import utilities.DateHandler;

public class ApiHandler {
    
    private final String apiUrl = "https://sala-negra.com/actua_public_api_v1/get_events";
    
    private String apiPost(String params){
        HttpURLConnection conn = null;
        try{
            URL url = new URL(apiUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(params.getBytes().length));
            try (OutputStream os = conn.getOutputStream()) {
                os.write(params.getBytes());
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        finally{
            conn.disconnect();
        }
    }
    
    private void doThing(){
        JSONObject object = new JSONObject(apiPost(""));
        JSONArray events = object.getJSONArray("events");
        for(int i =0; i<events.length();i++){
            JSONObject event = events.getJSONObject(i);
            Event e = new Event();
            e.setStartDateTime(DateHandler.convertToLocalDateTime(event.getString("startDateTime")));
            e.setFinishDateTime(DateHandler.convertToLocalDateTime(event.getString("finishDateTime")));
            e.setTitle(event.getString("title"));
            e.setDescription(event.getString("excerpt"));
            e.setUrl(event.getString("url"));
            JSONArray cats = event.getJSONArray("cats");
            if(cats!=null){
                for(int o = 0; o<cats.length();o++){
                    JSONObject cat = cats.getJSONObject(o);
                    Category c = new Category();
                }
            }
        }
    }
    
    public static void main(String[] args) {
        ApiHandler handler = new ApiHandler();
        handler.doThing();
    }
}