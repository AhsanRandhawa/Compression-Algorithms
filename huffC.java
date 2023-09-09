import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class huffC {
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 0) {
            System.out.println(
                    "Please input a path to a file or directory to be compressed.");
            System.exit(1);
        }
        String pathToFile = args[0];
        String pathToOutput = "";
        if (args.length > 1) {
            pathToOutput = args[1];
        }

        File encodingTxt = new File("encoding.txt");
        Map<String, String> encoding = new HashMap<>();

        Scanner scanner = new Scanner(encodingTxt);
        while (scanner.hasNext()) {
            String[] current = scanner.nextLine().split(" ");
            encoding.put(current[0], current[1]);
        }
        scanner.close();

        try {
            File book = new File(pathToFile);
            scanner = new Scanner(book);
            StringBuilder bookString = new StringBuilder();
            while (scanner.hasNext()) {
                String current = scanner.nextLine();
                bookString.append(current);
                if (scanner.hasNext()) {
                    bookString.append("\n");
                }
            }
            scanner.close();
            long startTime = System.currentTimeMillis();
            writeEncodedBook(bookString.toString().toCharArray(), encoding, book.getName().split("\\.")[0],
                    pathToOutput);
            double timeTaken = (System.currentTimeMillis() - startTime) / 1000.0;
            System.out.println("The time taken to compress " + book.getName() + " is " + timeTaken);
        } catch (FileNotFoundException e) {
            try {
                File dir = new File(pathToFile);
                File[] directoryListing = dir.listFiles();
                if (directoryListing != null) {
                    for (File child : directoryListing) {
                        scanner = new Scanner(child);
                        StringBuilder bookString = new StringBuilder();
                        while (scanner.hasNext()) {
                            String current = scanner.nextLine();
                            bookString.append(current);
                            if (scanner.hasNext()) {
                                bookString.append("\n");
                            }
                        }
                        long startTime = System.currentTimeMillis();
                        writeEncodedBook(bookString.toString().toCharArray(), encoding, child.getName().split("\\.")[0],
                                pathToOutput);
                        double timeTaken = (System.currentTimeMillis() - startTime) / 1000.0;
                        System.out.println("The time taken to compress " + child.getName() + " is " + timeTaken);
                    }
                }
            } catch (FileNotFoundException f) {
                System.out.println(
                        "The file or directory you passed to be compressed hasn't been found. Please make sure the file exists and that the path to the file is correct");
                System.exit(1);
            }
        }
    }

    private static void writeEncodedBook(char[] book, Map<String, String> encoding, String name, String pathToOutput) {
        String[] encoded = new String[book.length];
        int index = 0;
        for (char c : book) {
            String current = c + "";
            if (!(c > 32 && c < 128)) {
                current = String.valueOf(Integer.valueOf(c));
            }
            encoded[index] = encoding.get(current);
            index += 1;
        }
        String[] encodedJoined = String.join("", encoded).split("(?<=\\G.{" + 8 + "})");
        byte[] binaryData = toByteArray(String.join(" ", encodedJoined));

        try {
            if (!pathToOutput.isEmpty()) {
                File theDir = new File(pathToOutput);
                if (!theDir.exists()) {
                    theDir.mkdirs();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(pathToOutput + "\\" + name + ".huff");
                fileOutputStream.write(binaryData);
                fileOutputStream.close();
            } else {
                FileOutputStream fileOutputStream = new FileOutputStream(name + ".huff");
                fileOutputStream.write(binaryData);
                fileOutputStream.close();
            }
            System.out.printf("Successfully wrote the compressed %s file.\n", name);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static byte[] toByteArray(String binaryString) {
        String[] tokens = binaryString.split("\\s+");
        byte[] data = new byte[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            data[i] = (byte) Integer.parseInt(tokens[i], 2);
        }
        return data;
    }
}
