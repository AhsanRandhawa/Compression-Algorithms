import java.io.*;
import java.nio.file.*;
import java.util.*;
public class Digr1024C {
    static final String typeEncoding = "Digr1024";
    static final int binaryLength = 10;
    public static void main(String[] args) throws Exception{
        if(args.length < 1)
        {
            System.out.println(
                    "Please input a path to a file to be compressed.");
            System.exit(1);
        }
        if(Files.notExists(Paths.get(args[0])) || Files.notExists(Paths.get(typeEncoding + ".txt"))){
            System.out.println("Error: Issue with arguments!");
            System.exit(1);
        }
        File digr = new File(typeEncoding + ".txt");


        String inputFilePath = args[0];
        String outputFilePath = args[0] + "." + typeEncoding;

        Map<String, String> dict = new HashMap<>();

        Scanner reader = new Scanner(digr);
        while (reader.hasNext()) {
            String[] current = reader.nextLine().split("\t");
            if(current.length > 1){
                dict.put(current[1], current[0]);
            }
        }
        dict.put("\n", String.format("%0" + binaryLength + "d", 0)); //Manually Adding newLine Character
        reader.close();

        File book = new File(args[0]);
        Scanner scanner = new Scanner(book);
        StringBuilder sb = new StringBuilder();
        String bookString = "";
        while (scanner.hasNext()) {
            sb.append(scanner.nextLine());
            if (scanner.hasNext()) {
                sb.append("\n");
            }
        }
        bookString = sb.toString();
        scanner.close();

        long startTime = System.currentTimeMillis();
        writeEncodedBook(bookString.toCharArray(), dict, outputFilePath);
        double timeTaken = (System.currentTimeMillis() - startTime) / 1000.0;
        System.out.println("The time taken to compress " + book.getName() + " is " + timeTaken);
    }

    private static boolean notInRange(char c)
    {
        return c != 9 && c != 10 && !(c >= 32 && c <= 126);
    }


    private static void writeEncodedBook(char[] book, Map<String, String> encoding, String outputFilePath) {
        StringBuilder sb = new StringBuilder();
        for(int i =0;i< book.length;i++){
            char curr = book[i];
            if (notInRange(curr))
            {
                continue;
            }
            int j = i + 1;
            while(j<book.length &&notInRange(book[j]))
                j++;

            if (j<book.length)
            {
                char next = book[j];
                String posDi = curr + "" + next;
                if(encoding.containsKey(posDi))
                {
                    sb.append(encoding.get(posDi));
                    i = j;
                    continue;
                }
            }

            String a = "" + curr;
            sb.append(encoding.get(a));
        }
        String[] encodedJoined = sb.toString().split("(?<=\\G.{" + 8 + "})");
        byte[] binaryData = toByteArray(String.join(" ", encodedJoined));
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath);
            fileOutputStream.write(binaryData);
            fileOutputStream.close();
            System.out.printf("Successfully wrote the compressed file to: %s\n", outputFilePath);
        }
        catch (IOException e) {
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

