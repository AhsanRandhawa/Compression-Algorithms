import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Queue;

class lzwC {
    static final int bmax = 16;
    static final double minCompRatio = 2.0;
    static final int maxDictSize = (int) Math.pow(2, bmax);
    static final int queueMaxLength = 50;

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 0) {
            System.out.println(
                    "Please input a path to a file to be compressed.");
            System.exit(1);
        }
        String inputFilePath = args[0];
        String outputFilePath = args[0] + ".lzw";
        if (args.length > 1) {
            //Second Argument might be used to specify output file path
            outputFilePath = args[1];
        }
        try {
            File book = new File(inputFilePath);
            Scanner scanner = new Scanner(book);
            StringBuilder sb = new StringBuilder();
            String bookString = "";
            while (scanner.hasNext()) {
                String current = scanner.nextLine();
                sb.append(current);
                if (scanner.hasNext()) {
                    sb.append("\n");
                }
            }
            bookString = sb.toString();
            scanner.close();
            writelzwCompressedBook(bookString.toCharArray(), outputFilePath);
        }
        catch (FileNotFoundException e)
        {
            System.out.println(
                    "The file you passed to be compressed hasn't been found. Please make sure the file exists and that the path to the file is correct");
            System.exit(1);
        }


    }

    private static void writelzwCompressedBook(char[] book, String outputFilePath) throws FileNotFoundException{
        String encodedBook = getlzwEncoding(book);
        String[] encodedJoined = encodedBook.split("(?<=\\G.{" + 8 + "})");
        byte[] binaryData = toByteArray(String.join(" ", encodedJoined));

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath);
            fileOutputStream.write(binaryData);
            fileOutputStream.close();
            System.out.printf("Successfully wrote the compressed file at: %s\n", outputFilePath);
        }
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }


    private static byte[] toByteArray(String binaryString) {
        String[] tokens = binaryString.split("\\s+");
        byte[] data = new byte[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            data[i] = (byte) Integer.parseInt(tokens[i], 2);
        }
        return data;
    }
    private static String getlzwEncoding(char[] book)
    {
        HashMap<String, Integer> table = initializeTable();
        int totalEncodedCharacters = 0;

        int binarySize = 9;
        int dictSize = 256;
        StringBuilder sb = new StringBuilder();
        String currentString = "";

        Queue<Integer> queue = new LinkedList<>();

        for(char c: book)
        {
            if(c > 255 || c < 9) // Look at relevant characters
                continue;
            if(table.containsKey(currentString + c)) // Should be true for first loop
            {
                currentString += c;
                continue;
            }

            if (dictSize < maxDictSize)
            {
                if(dictSize + 1 > Math.pow(2, binarySize)) // We need more digits
                {
                    addCodeWord(sb, 1, binarySize); //1 is the code for increasing binary digits size
                    binarySize++;
                }
                table.put(currentString + c, dictSize);
                dictSize++;
            }
            addCodeWord(sb, table.get(currentString), binarySize);

            if(dictSize == maxDictSize)
            {
                totalEncodedCharacters = getTotalEncodedCharacters(queue, currentString.length(), totalEncodedCharacters);
                if (queue.size() >= queueMaxLength && getCompressionRate(totalEncodedCharacters) < minCompRatio)
                {
                    //Flush the Dictionary
                    addCodeWord(sb, 0, binarySize);//0 is the code word for flushing the dictionary.

                    //Re-initialize all values
                    table = initializeTable();
                    binarySize = 9;
                    dictSize = 256;
                    totalEncodedCharacters = 0;
                    queue = new LinkedList<>();
                }
            }

            currentString = c + "";

        }
        addCodeWord(sb, table.get(currentString), binarySize);
        addCodeWord(sb, 0, binarySize); //For Flushing Purposes
        return sb.toString();
    }

    private static double getCompressionRate(int totalEncodedCharacters)
    {
        return totalEncodedCharacters / (2.0 * queueMaxLength);
    }

    private static int getTotalEncodedCharacters(Queue<Integer> queue, int charsRepresentedByCode, int totalEncodedCharacters)
    {
        totalEncodedCharacters += charsRepresentedByCode;
        queue.add(charsRepresentedByCode);
        if (queue.size() > queueMaxLength)
        {
            totalEncodedCharacters -= queue.remove();
        }
        return totalEncodedCharacters;
    }


    private static void addCodeWord(StringBuilder sb, int code, int binarySize)
    {
        sb.append(binaryWithSpecifiedLength(code, binarySize));
    }

    private static String binaryWithSpecifiedLength(int code, int binarySize)
    {
        return String.format("%" + binarySize + "s",
                Integer.toBinaryString(code)).replaceAll(" ", "0");
    }

    private static HashMap<String, Integer> initializeTable()
    {
        HashMap<String, Integer> table = new HashMap<String, Integer>();
        for (int i = 0; i < 256; i++) {
            table.put(Character.toString((char) i), i);
        }
        return table;
    }

}
