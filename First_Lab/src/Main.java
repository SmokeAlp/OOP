import java.awt.Desktop;
import java.util.Scanner;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

private static String makeHttpRequest(String urlString) throws IOException {
    URL url = new URL(urlString);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    connection.setRequestMethod("GET");
    connection.setRequestProperty("User-Agent", "WikipediaSearch");

    try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        return response.toString();
    } finally {
        connection.disconnect();
    }
}

private static java.util.ArrayList<Integer> parseAndDisplayResults(String jsonResponse) {
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

void main() {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Введите поисковый запрос: ");
    String searchQuery = scanner.nextLine();

    if (searchQuery == null || searchQuery.trim().isEmpty()) {
        System.out.println("Запрос не может быть пустым!");
        return;
    }

    try {
        String encodedQuery = URLEncoder.encode(searchQuery.trim(), StandardCharsets.UTF_8);

        String apiUrl = "https://ru.wikipedia.org/w/api.php?" + "action=query&" + "list=search&" + "utf8=&" +
                "format=json&" + "srsearch=" + encodedQuery;

        System.out.println("\nЗапрос к Wikipedia:");
        System.out.println(apiUrl);

        String json = makeHttpRequest(apiUrl);

        java.util.ArrayList<Integer> articleTitles = parseAndDisplayResults(json);

        if (articleTitles.isEmpty()) {
            System.out.println("Статьи не найдены.");
            scanner.close();
            return;
        }

        while (true) {
            System.out.print("\nВведите номер статьи для открытия 1-" + articleTitles.size() + " или 0 для выхода: ");

            int i = Integer.parseInt(scanner.nextLine());
            String articleURL;
            try {
                if (i == 0) {
                    System.out.println("Выход");
                    break;
                }

                if (i < 1 || i > articleTitles.size()) {
                    System.out.println("Ошибка: пожалуйста, введите число от 1 до " + articleTitles.size() + " или 0 для выхода: ");
                    continue;
                }

                articleURL = "https://ru.wikipedia.org/w/index.php?curid=" + articleTitles.get(i-1);

                System.out.println("Открываю статью");

                Desktop.getDesktop().browse(new URI(articleURL));

            } catch (Exception e) {
                System.out.println("Не удалось открыть браузер: " + e.getMessage());
            }

        }
        scanner.close();

    } catch (Exception e) {
        System.out.println("Произошла ошибка: " + e.getMessage());
    }
}