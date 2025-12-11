package main;

import com.google.gson.*;

public class Parse {
    static java.util.ArrayList<Integer> parseAndDisplayResults(String jsonResponse) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

            JsonObject queryObj = jsonObject.getAsJsonObject("query");
            JsonArray searchResults = queryObj.getAsJsonArray("search");

            System.out.println("\n-Результаты поиска");
            System.out.println("-Найдено статей: " + searchResults.size());

            java.util.ArrayList<Integer> titles = new java.util.ArrayList<>();
            for (JsonElement element : searchResults) {
                JsonObject article = element.getAsJsonObject();
                String title = article.get("title").getAsString();
                int pageId = article.get("pageid").getAsInt();

                System.out.println("\nЗаголовок: " + title);
                System.out.println("ID: " + pageId);
                titles.add(pageId);
            }
            return titles;

        } catch (Exception e) {
            System.out.println("Ошибка при обработке результатов: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }
}
