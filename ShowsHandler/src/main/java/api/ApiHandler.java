package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import model.Category;
import model.Event;
import mongodb.MongoEventHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import utilities.DateHandler;

public class ApiHandler {
    
    private final String apiUrl = "https://sala-negra.com/actua_public_api_v1/get_events";
    
    /**
     * Realiza un post a la api publica para recuperar un string que representa la respuesta del servidor
     * @param params del metodo post
     * @return respuesta
     */
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
    
    // almacena los objetos recuperados en la respuesta a la pai si estos no existen ya en la base de datos.
    private void storeApiResponse(){
        JSONObject object = new JSONObject(apiPost(""));
        JSONArray events = object.getJSONArray("events");
        for(int i =0; i<events.length();i++){
            JSONObject event = events.getJSONObject(i);
            Event e = parseJsonToModel(event);
            if(MongoEventHandler.getInstance().getEventByTitle(e.getTitle())==null){
                MongoEventHandler.getInstance().insertEvent(e);
            }
        }
    }
    
    //convierte los objetos json de la respuesta de la api al modelo evento de este programa.
    private Event parseJsonToModel(JSONObject event){
        Event e = new Event();
        e.setStartDateTime(DateHandler.convertToLocalDateTime(event.getString("startDateTime")));
        e.setFinishDateTime(DateHandler.convertToLocalDateTime(event.getString("finishDateTime")));
        e.setTitle(event.getString("title"));
        e.setDescription(event.getString("excerpt"));
        e.setUrl(event.getString("url"));
        Object cats = event.opt("cats");
        if(cats instanceof JSONArray){
            JSONArray jsonCats = new JSONArray(cats.toString());
            List<Category> categories = new ArrayList();
            for(int o = 0; o<jsonCats.length();o++){
                JSONObject cat = jsonCats.getJSONObject(o);
                Category c = new Category();
                String key = cat.keys().next();
                c.setCatId(Integer.parseInt(key));
                c.setCatName(cat.getString(key));
                categories.add(c);
            }
            e.setCats(categories);
        } 
        return e;
    }
}