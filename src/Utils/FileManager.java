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
            // Create directories if they do not exist
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();  // Create parent directories if necessary
            }
            // Create the file if it does not exist; overwrite if it does
            if (file.createNewFile() || file.exists()) {
                try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                    writer.write(contents);
                }
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

    public static void writeIMCode(String path, String contents) {
        try {
            try (java.io.FileWriter writer = new java.io.FileWriter(path)) {
                writer.write(contents);
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
        }
    }

    public static void writeBasicCode(String path, String contents) {
        try {
            try (java.io.FileWriter writer = new java.io.FileWriter(path)) {
                writer.write(contents);
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
        }
    }
}
