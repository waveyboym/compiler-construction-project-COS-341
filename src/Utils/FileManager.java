package Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class FileManager {
    public static String readFileAndReturnContents(String path) {
        try {
            File file = new File(path);
            String contents;
            try (Scanner scanner = new Scanner(file)) {
                contents = scanner.useDelimiter("\\A").next();
            }
            return contents;
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            return null;
        }
    }

    public static void createAndWriteFile(String path, String contents) {
        try {
            File file = new File(path);
            // create file if it does not exist else overwrite
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists. Overwriting...");
            }
            try (java.io.FileWriter writer = new java.io.FileWriter(path)) {
                writer.write(contents);
            }
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public static void writeFile(String path, String contents) {
        try {
            try (java.io.FileWriter writer = new java.io.FileWriter(path)) {
                writer.write(contents);
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
        }
    }
}
