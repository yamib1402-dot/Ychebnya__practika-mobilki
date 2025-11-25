package com.example.myapplication;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SupabaseClientHelper {
    private static final String SUPABASE_URL = "https://czgjkwykzwopdpngtfqv.supabase.co";
    private static final String SUPABASE_KEY = "sb_publishable_558pLhqkxi0gYJIcxYkdXQ_ESy2rweV";
    private static final String SUPABASE_REST_URL = SUPABASE_URL + "/rest/v1/";

    public static void addBrickToDatabase(Brick brick, Context context, SupabaseCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(SUPABASE_REST_URL + "bricks");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("apikey", SUPABASE_KEY);
                connection.setRequestProperty("Authorization", "Bearer " + SUPABASE_KEY);
                connection.setRequestProperty("Prefer", "return=minimal");
                connection.setDoOutput(true);

                JSONObject brickJson = new JSONObject();
                brickJson.put("id", UUID.randomUUID().toString());
                brickJson.put("name", brick.getName());
                brickJson.put("type", brick.getType());
                brickJson.put("color", brick.getColor());
                brickJson.put("weight", brick.getWeight());
                brickJson.put("price", brick.getPrice());
                brickJson.put("in_stock", brick.isInStock());

                OutputStream os = connection.getOutputStream();
                os.write(brickJson.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                    callback.onSuccess("Кирпич добавлен в базу данных");
                } else {
                    callback.onError("Ошибка сервера: " + responseCode);
                }

                connection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                callback.onError("Ошибка добавления: " + e.getMessage());
            }
        }).start();
    }

    public static void loadBricksFromDatabase(SupabaseBricksCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(SUPABASE_REST_URL + "bricks?select=*");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("apikey", SUPABASE_KEY);
                connection.setRequestProperty("Authorization", "Bearer " + SUPABASE_KEY);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    JSONArray bricksArray = new JSONArray(response.toString());
                    List<Brick> bricks = new ArrayList<>();

                    for (int i = 0; i < bricksArray.length(); i++) {
                        JSONObject brickJson = bricksArray.getJSONObject(i);
                        Brick brick = new Brick();
                        brick.setId(brickJson.optString("id"));
                        brick.setName(brickJson.optString("name"));
                        brick.setType(brickJson.optString("type"));
                        brick.setColor(brickJson.optString("color"));
                        brick.setWeight(brickJson.optDouble("weight"));
                        brick.setPrice(brickJson.optDouble("price"));
                        brick.setInStock(brickJson.optBoolean("in_stock", true));
                        brick.setCreatedAt(brickJson.optString("created_at"));

                        bricks.add(brick);
                    }

                    callback.onBricksLoaded(bricks);
                } else {
                    callback.onError("Ошибка сервера: " + responseCode);
                }

                connection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                callback.onError("Ошибка загрузки: " + e.getMessage());
            }
        }).start();
    }

    public static void initialize() {
    }

    public interface SupabaseCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public interface SupabaseBricksCallback {
        void onBricksLoaded(List<Brick> bricks);
        void onError(String error);
    }
}