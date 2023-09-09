import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

class Pair<F, S> {
    private F first;
    private S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return this.first;
    }

    public S getSecond() {
        return this.second;
    }

}

class HuffCode {
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 0) {
            System.out.println(
                    "Please input a path to a file or directory to generate an encoding.");
            System.exit(1);
        }
        Map<String, Integer> dictionary = new HashMap<>();

        String pathToFile = args[0];
        try {
            File dir = new File(pathToFile);
            File[] directoryListing = dir.listFiles();
            dictionary.put(String.valueOf(Integer.valueOf('\n')), 0);
            if (directoryListing != null) {
                for (File child : directoryListing) {
                    System.out.println(child.getName());
                    Scanner sc = new Scanner(child);
                    while (sc.hasNextLine()) {
                        for (Character c : sc.nextLine().toCharArray()) {
                            String curr = c + "";
                            if (!(c > 32 && c < 128)) {
                                curr = String.valueOf(Integer.valueOf(c));
                            }
                            if (!dictionary.containsKey(curr)) {
                                dictionary.put(curr, 0);
                            }
                            dictionary.put(curr, dictionary.get(curr) + 1);
                        }
                        if (sc.hasNextLine()) {
                            dictionary.put(String.valueOf(Integer.valueOf('\n')),
                                    dictionary.get(String.valueOf(Integer.valueOf('\n'))) + 1);
                        }
                    }
                    sc.close();
                }
            }
        } catch (FileNotFoundException e) {
            try {
                for (int i = 0; i < args.length; i++) {
                    File file = new File(args[i]);
                    Scanner sc = new Scanner(file);
                    while (sc.hasNextLine()) {
                        for (Character c : sc.nextLine().toCharArray()) {
                            String curr = c + "";
                            if (!(c > 32 && c < 128)) {
                                curr = String.valueOf(Integer.valueOf(c));
                            }
                            if (!dictionary.containsKey(curr)) {
                                dictionary.put(curr, 0);
                            }
                            dictionary.put(curr, dictionary.get(curr) + 1);
                        }
                        if (sc.hasNextLine()) {
                            dictionary.put(String.valueOf(Integer.valueOf('\n')),
                                    dictionary.get(String.valueOf(Integer.valueOf('\n'))) + 1);
                        }
                    }
                    sc.close();
                }
            } catch (FileNotFoundException f) {
                System.out.println(
                        "The file or directory you passed to for dictionary generation hasn't been found. Please make sure the file exists and that the path to the file is correct");
                System.exit(1);
            }
        }

        try {
            FileWriter myWriter = new FileWriter("huffDict.txt");
            for (Map.Entry<String, Integer> entry : dictionary.entrySet()) {
                String key = entry.getKey();
                Integer val = entry.getValue();
                myWriter.write(String.format("%s %d\n", key, val));
            }
            myWriter.close();
            System.out.println("Successfully wrote the huffDict file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        File dictionaryTxt = new File("huffDict.txt");
        PriorityQueue<Pair<String, Integer>> symbolAndFrequency = new PriorityQueue<>(
                (a, b) -> (a.getSecond().compareTo(b.getSecond())));

        Scanner scanner = new Scanner(dictionaryTxt);
        while (scanner.hasNext()) {
            String[] current = scanner.nextLine().split(" ");
            symbolAndFrequency.add(new Pair<String, Integer>(current[0], Integer.parseInt(current[1])));
        }
        scanner.close();
        Map<String, String> encoding = generateEncoding(symbolAndFrequency);
        try {
            FileWriter myWriter = new FileWriter("encoding.txt");
            for (Map.Entry<String, String> entry : encoding.entrySet()) {
                String key = entry.getKey();
                String val = entry.getValue();
                myWriter.write(String.format("%s %s\n", key, val));
            }
            myWriter.close();
            System.out.println("Successfully wrote the encodings file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static Map<String, String> generateEncoding(PriorityQueue<Pair<String, Integer>> symbolAndFrequency) {
        if (symbolAndFrequency.size() <= 2) {
            Map<String, String> encoding = new HashMap<>();
            int i = 0;
            for (Pair<String, Integer> p : symbolAndFrequency) {
                encoding.put(p.getFirst(), String.valueOf(i));
                i += 1;
            }
            return encoding;
        } else {
            int m = 2;
            String superSymbol = "";
            int superSymbolFrequency = 0;
            List<String> mergedSymbols = new ArrayList<>();
            for (int i = 0; i < m; i++) {
                Pair<String, Integer> p = symbolAndFrequency.poll();
                mergedSymbols.add(p.getFirst());
                superSymbol += p.getFirst();
                superSymbolFrequency += p.getSecond();
            }
            symbolAndFrequency.add(new Pair<String, Integer>(superSymbol, superSymbolFrequency));
            Map<String, String> encoding = generateEncoding(new PriorityQueue<>(symbolAndFrequency));
            Map<String, String> result = new HashMap<>();
            for (Pair<String, Integer> p : symbolAndFrequency) {
                if (p.getFirst() != superSymbol) {
                    result.put(p.getFirst(), encoding.get(p.getFirst()));
                }
            }
            for (int i = 0; i < m; i++) {
                result.put(mergedSymbols.get(i), encoding.get(superSymbol) + i);
            }
            return result;
        }
    }
}