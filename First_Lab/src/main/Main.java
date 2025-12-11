package main;

import java.awt.Desktop;
import java.util.Scanner;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Main {
    void main() {
        // Рагимов Павел + Request
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

            String json = Request.makeHttpRequest(apiUrl);

            // Ярошенко Кирилл + Parse

            java.util.ArrayList<Integer> articleTitles = Parse.parseAndDisplayResults(json);

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
}