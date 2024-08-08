
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileReader {
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
}
