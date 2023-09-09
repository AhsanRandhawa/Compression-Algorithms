import java.io.*;
import java.nio.file.*;
import java.util.*;
public class Digr512D {
    static final String typeEncoding = "Digr512";
    static final int binarySize = 9;
    public static void main(String[] args) throws Exception{
        if (args.length == 0) {
            System.out.println(
                    "Please input a path to a file to be decompressed.");
            System.exit(1);
        }
        if(Files.notExists(Paths.get(args[0])) || Files.notExists(Paths.get(typeEncoding + ".txt"))){
            System.out.println("Error: Issue with arguments!");
            System.exit(1);
        }
        File digr = new File(typeEncoding + ".txt");

        String inputFilePath = args[0];
        String outputFilePath = args[0].substring(0, args[0].length() - 7);

        Map<String, String> dict = new HashMap<>();

        Scanner reader = new Scanner(digr);
        while (reader.hasNext()) {
            String[] current = reader.nextLine().split("\t");
            if(current.length > 1){
                dict.put(current[0], current[1]);
            }
        }
        dict.put(String.format("%0" + binarySize + "d", 0), "\n"); //Manually Adding newline
        reader.close();

        try {
            File book = new File(inputFilePath);
            FileInputStream fileInputStream = new FileInputStream(book);
            String decodedBinaryString = toBinaryString(fileInputStream.readAllBytes());
            fileInputStream.close();
            long startTime = System.currentTimeMillis();
            writeDecodedBook(decodedBinaryString, dict, outputFilePath);
            double timeTaken = (System.currentTimeMillis() - startTime) / 1000.0;
            System.out.println("The time taken to decompress " + book.getName() + " is " + timeTaken);
        }
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    private static String getNewCode(String book, int currentBit, int binarySize)
    {
        return book.substring(currentBit, currentBit + binarySize);
    }
    private static void writeDecodedBook(String book, Map<String, String> encoding, String outputFilePath) {
        StringBuilder sb = new StringBuilder();
        int currentBit = 0;
        while (currentBit + binarySize <= book.length())
        {
            String nextCode = getNewCode(book, currentBit, binarySize);
            currentBit += binarySize;

            sb.append(encoding.get(nextCode));
        }

        try {
            FileWriter myWriter = new FileWriter(outputFilePath);
            myWriter.write(sb.toString());
            myWriter.close();
            System.out.printf("Successfully wrote the decompressed file to: %s\n", outputFilePath);
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



