import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class huffD {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println(
                    "Please input a path to a file or directory to be decompressed.");
            System.exit(1);
        }
        File encodingTxt = new File("encoding.txt");
        Map<String, String> encoding = new HashMap<>();

        Scanner scanner = new Scanner(encodingTxt);
        while (scanner.hasNext()) {
            String[] current = scanner.nextLine().split(" ");
            encoding.put(current[1], current[0]);
        }
        scanner.close();

        String pathToFile = args[0];
        String pathToOutput = "";
        if (args.length > 1) {
            pathToOutput = args[1];
        }

        try {
            File book = new File(pathToFile);
            FileInputStream fileInputStream = new FileInputStream(book);
            String decodedBinaryString = toBinaryString(fileInputStream.readAllBytes());
            fileInputStream.close();
            long startTime = System.currentTimeMillis();
            writeDecodedBook(decodedBinaryString, encoding, book.getName().split("\\.")[0], pathToOutput);
            double timeTaken = (System.currentTimeMillis() - startTime) / 1000.0;
            System.out.println("The time taken to compress " + book.getName() + " is " + timeTaken);
        } catch (IOException e) {
            try {
                File dir = new File(pathToFile);
                File[] directoryListing = dir.listFiles();
                if (directoryListing != null) {
                    for (File child : directoryListing) {
                        FileInputStream fileInputStream = new FileInputStream(child);
                        String decodedBinaryString = toBinaryString(fileInputStream.readAllBytes());
                        fileInputStream.close();
                        long startTime = System.currentTimeMillis();
                        writeDecodedBook(decodedBinaryString, encoding, child.getName().split("\\.")[0], pathToOutput);
                        double timeTaken = (System.currentTimeMillis() - startTime) / 1000.0;
                        System.out.println("The time taken to decompress " + child.getName() + " is " + timeTaken);
                    }
                }
            } catch (FileNotFoundException f) {
                System.out.println(
                        "The file or directory you passed to be decompressed hasn't been found. Please make sure the file exists and that the path to the file is correct");
                System.exit(1);
            }
        }

    }

    private static void writeDecodedBook(String book, Map<String, String> encoding, String name, String pathToOutput) {
        String[] decoded = new String[book.length()];
        int i = 0;
        int start = 0;
        int end = 1;
        while (start < book.length() && end <= book.length()) {
            String curr = book.substring(start, end);
            if (encoding.containsKey(curr)) {
                String res = encoding.get(curr);
                if (res.length() > 1) {
                    res = "" + (char) Integer.parseInt(res);
                }
                decoded[i] = res;
                start = end;
                end = start + 1;
                i += 1;
            } else {
                end += 1;
            }
        }
        String[] toWriteFinal = Arrays.copyOfRange(decoded, 0, i);
        try {
            if (!pathToOutput.isEmpty()) {
                File theDir = new File(pathToOutput);
                if (!theDir.exists()) {
                    theDir.mkdirs();
                }
            }
            FileWriter myWriter = new FileWriter(pathToOutput + "\\" + name + ".txt");
            myWriter.write(String.join("", toWriteFinal));
            myWriter.close();
            System.out.printf("Successfully wrote the decompressed %s file.\n", name);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static String toBinaryString(byte[] data) {
        StringBuilder builder = new StringBuilder();
        for (byte b : data) {
            builder.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
        return builder.toString();
    }
}
