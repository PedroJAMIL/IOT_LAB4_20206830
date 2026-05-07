package com.example.iotlab4.network;

import com.example.iotlab4.models.Category;
import com.example.iotlab4.models.Meal;
import com.example.iotlab4.models.MealDetail;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MealApi {

    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1";

    private static String httpGet(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        try {
            int code = conn.getResponseCode();
            if (code != 200) throw new Exception("HTTP " + code);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            return sb.toString();
        } finally {
            conn.disconnect();
        }
    }

    public static List<Category> getCategories() throws Exception {
        String json = httpGet(BASE_URL + "/categories.php");
        JSONArray arr = new JSONObject(json).getJSONArray("categories");
        List<Category> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.getJSONObject(i);
            list.add(new Category(
                    o.optString("idCategory"),
                    o.optString("strCategory"),
                    o.optString("strCategoryThumb"),
                    o.optString("strCategoryDescription")
            ));
        }
        return list;
    }

    public static List<Meal> getMealsByCategory(String category) throws Exception {
        String json = httpGet(BASE_URL + "/filter.php?c=" + category);
        return parseMealList(json);
    }

    public static List<Meal> getMealsByIngredient(String ingredient) throws Exception {
        String json = httpGet(BASE_URL + "/filter.php?i=" + ingredient);
        return parseMealList(json);
    }

    public static MealDetail getMealDetail(String idMeal) throws Exception {
        String json = httpGet(BASE_URL + "/lookup.php?i=" + idMeal);
        return parseMealDetail(json);
    }

    public static MealDetail getRandomMeal() throws Exception {
        String json = httpGet(BASE_URL + "/random.php");
        return parseMealDetail(json);
    }

    private static List<Meal> parseMealList(String json) throws Exception {
        JSONObject obj = new JSONObject(json);
        List<Meal> list = new ArrayList<>();
        if (obj.isNull("meals")) return list;
        JSONArray arr = obj.getJSONArray("meals");
        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.getJSONObject(i);
            list.add(new Meal(
                    o.optString("idMeal"),
                    o.optString("strMeal"),
                    o.optString("strMealThumb")
            ));
        }
        return list;
    }

    private static MealDetail parseMealDetail(String json) throws Exception {
        JSONObject obj = new JSONObject(json);
        if (obj.isNull("meals")) return null;
        JSONArray arr = obj.getJSONArray("meals");
        if (arr.length() == 0) return null;
        JSONObject o = arr.getJSONObject(0);

        List<String> ingredients = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            String ing = o.optString("strIngredient" + i, "").trim();
            String mea = o.optString("strMeasure" + i, "").trim();
            if (!ing.isEmpty() && !ing.equals("null")) {
                String item = (!mea.isEmpty() && !mea.equals("null")) ? (ing + " - " + mea) : ing;
                ingredients.add(item);
            }
        }

        return new MealDetail(
                o.optString("idMeal"),
                o.optString("strMeal"),
                o.optString("strCategory"),
                o.optString("strArea"),
                o.optString("strInstructions"),
                o.optString("strMealThumb"),
                ingredients
        );
    }
}