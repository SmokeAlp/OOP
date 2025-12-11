package main;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class ReadFile {
    public static void Read_File(String filePath) {
        if (filePath.endsWith(".csv")) {
            System.out.println("Считывание CSV");
            ReadCSV(filePath);
        }
        else if (filePath.endsWith(".xml")) {
            System.out.println("Считывание  XML");
            ReadXML(filePath);
        }
        else {
            System.out.println("Файл не распознан, ожидание xml или csv формата: " + filePath);
        }
    }

    static void ReadCSV(String filePath) {
        Map<String, Integer> buildingCounts = new HashMap<>();
        Map<String, Map<String, Integer>> floorStats = new HashMap<>();

        // Считывание
        try (Scanner scanner = new Scanner(Path.of(filePath))) {
            scanner.nextLine();

            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(";");
                if (parts.length < 4) continue;

                String city = parts[0].trim();
                String street = parts[1].trim();
                String house = parts[2].trim();
                String floor = parts[3].trim();

                String buildingKey = city + "|" + street + "|" + house + "|" + floor;
                buildingCounts.put(buildingKey, buildingCounts.getOrDefault(buildingKey, 0) + 1);

                floorStats.putIfAbsent(city, new HashMap<>());
                Map<String, Integer> cityFloors = floorStats.get(city);
                cityFloors.put(floor, cityFloors.getOrDefault(floor, 0) + 1);
            }

            // Обработка
            System.out.println("Дубликаты записей:");
            boolean hasDuplicates = false;

            for (Map.Entry<String, Integer> entry : buildingCounts.entrySet()) {
                if (entry.getValue() > 1) {
                    hasDuplicates = true;
                    String[] parts = entry.getKey().split("\\|");
                    String city = parts[0];
                    String street = parts[1];
                    String house = parts[2];
                    String floor = parts[3];

                    System.out.println("Город: " + city + " Улица: " + street + " Дом: " + house +
                                    " Этаж: " + floor + " - повторяется " + entry.getValue() + " раз(а)");
                }
            }
            if (!hasDuplicates) {
                System.out.println("Дубликатов не найдено.");
            }
            System.out.println("Статистика этажей по городам:");

            for (Map.Entry<String, Map<String, Integer>> cityEntry : floorStats.entrySet()) {
                String city = cityEntry.getKey();
                Map<String, Integer> floors = cityEntry.getValue();

                System.out.println("\nГород: " + city);
                for (int i = 1; i <= 5; i++) {
                    String floor_counter = String.valueOf(i);
                    int count = floors.getOrDefault(floor_counter, 0);
                    System.out.println(i + "-этажных зданий: " + count);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void ReadXML(String filePath) {
        try {
            Map<String, Integer> buildingCounts = new HashMap<>();
            Map<String, Map<String, Integer>> floorStats = new HashMap<>();
            // Считывание
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(filePath);
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("item");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String city = element.getAttribute("city");
                    String street = element.getAttribute("street");
                    String house = element.getAttribute("house");
                    String floor = element.getAttribute("floor");

                    String buildingKey = city + "|" + street + "|" + house + "|" + floor;
                    buildingCounts.put(buildingKey, buildingCounts.getOrDefault(buildingKey, 0) + 1);

                    floorStats.putIfAbsent(city, new HashMap<>());
                    Map<String, Integer> cityFloors = floorStats.get(city);
                    cityFloors.put(floor, cityFloors.getOrDefault(floor, 0) + 1);
                }
            }

            // Обработка
            System.out.println("Дубликаты записей:");
            boolean hasDuplicates = false;

            for (Map.Entry<String, Integer> entry : buildingCounts.entrySet()) {
                if (entry.getValue() > 1) {
                    hasDuplicates = true;
                    String[] parts = entry.getKey().split("\\|");
                    String city = parts[0];
                    String street = parts[1];
                    String house = parts[2];
                    String floor = parts[3];

                    System.out.println("Город: " + city + " Улица: " + street + " Дом: " + house +
                            " Этаж: " + floor + " - повторяется " + entry.getValue() + " раз(а)");
                }
            }
            if (!hasDuplicates) {
                System.out.println("Дубликатов не найдено.");
            }
            System.out.println("Статистика этажей по городам:");

            for (Map.Entry<String, Map<String, Integer>> cityEntry : floorStats.entrySet()) {
                String city = cityEntry.getKey();
                Map<String, Integer> floors = cityEntry.getValue();
                System.out.println("\nГород: " + city);
                for (int i = 1; i <= 5; i++) {
                    String floor_counter = String.valueOf(i);
                    int count = floors.getOrDefault(floor_counter, 0);
                    System.out.println(i + "-этажных зданий: " + count);
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
