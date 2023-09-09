import java.io.*;
import java.util.HashMap;

public class lzwD {
    static final int bmax = 16;
    static final int maxDictSize = (int) Math.pow(2, bmax);

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 0) {
            System.out.println(
                    "Please input a path to a file to be decompressed.");
            System.exit(1);
        }
        String inputFilePath = args[0];
        String outputFilePath = args[0].substring(0, args[0].length() - 4);
        if (args.length > 1) {
            //Second Argument might be used to specify output file path
            outputFilePath = args[1];
        }
        try {
            File book = new File(inputFilePath);
            FileInputStream fileInputStream = new FileInputStream(book);
            String encodedBinaryString = toBinaryString(fileInputStream.readAllBytes());
            fileInputStream.close();
            writeDecodedtxtFile(encodedBinaryString, outputFilePath);
        } catch (FileNotFoundException f) {
            System.out.println(
                    "The file you passed to be decompressed hasn't been found. Please make sure the file exists and that the path to the file is correct");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred.");
        }
    }

    private static void writeDecodedtxtFile(String encodedBinaryString, String outputFilePath) {
        String decodedTxt = getDecodedtxtFile(encodedBinaryString);
        try {
            FileWriter myWriter = new FileWriter(outputFilePath);
            myWriter.write(decodedTxt);
            myWriter.close();
            System.out.printf("Successfully wrote the decompressed file to: %s \n", outputFilePath);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static String getDecodedtxtFile(String encodedBinaryString) {
        HashMap<Integer, String> table = initializeTable();

        int binarySize = 9;
        int dictSize = 256;
        StringBuilder sb = new StringBuilder();

        int currentBit = 0;
        int newCode = getNextCode(encodedBinaryString, currentBit, binarySize);
        currentBit += binarySize;
        sb.append(table.get(newCode));
        int oldCode = newCode;
        String oldTranslation = table.get(oldCode);


        while (currentBit + binarySize <= encodedBinaryString.length()) {
            newCode = getNextCode(encodedBinaryString, currentBit, binarySize);
            currentBit += binarySize;

            if (newCode == 0) //Flushing the table
            {
                //System.out.println("Flushing HAPPENED");
                table = initializeTable();
                binarySize = 9;
                dictSize = 256;
                oldTranslation = "";
                continue;
            }
            if (newCode == 1) //Increasing Length Of Code
            {
                binarySize++;
                continue;
            }
            String s;

            if (!table.containsKey(newCode))
                s = oldTranslation + oldTranslation.charAt(0);
            else
                s = table.get(newCode);

            sb.append(s);
            if (dictSize < maxDictSize && oldTranslation.length() != 0) {
                table.put(dictSize, oldTranslation + s.charAt(0));
                dictSize++;
            }

            oldCode = newCode;
            oldTranslation = table.get(oldCode);

        }

        return sb.toString();
    }

    private static int getNextCode(String encodedBinaryString, int currentBit, int binarySize) {
        return Integer.parseUnsignedInt(encodedBinaryString.substring(currentBit, currentBit + binarySize), 2);
    }


    private static String toBinaryString(byte[] data) {
        StringBuilder builder = new StringBuilder();
        for (byte b : data) {
            builder.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
        return builder.toString();
    }

    private static HashMap<Integer, String>initializeTable()
    {
        HashMap<Integer, String> table = new HashMap<Integer, String>();
        for (int i = 0; i < 256; i++) {
            table.put(i, Character.toString((char) i));
        }
        return table;
    }
}
