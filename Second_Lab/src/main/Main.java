package main;

import java.util.Scanner;

// CSV - Ярошенко Кирилл; XML - Рагимов Павел

public class Main {
    void main()
    {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Введите адрес xml или csv файла (или exit для выхода): ");
            String filePath = scanner.nextLine();

            if (filePath.equals("exit")) {
                break;
            }

            long start = System.currentTimeMillis();
            ReadFile.Read_File(filePath);
            long end = System.currentTimeMillis();

            System.out.println("Время обработки: " + (end - start) + " миллисекунд\n");
        }
    }
}
