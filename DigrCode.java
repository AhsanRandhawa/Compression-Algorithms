import java.io.*;
import java.nio.file.*;
import java.util.*;
public class DigrCode {
    public static void main(String[] args) throws Exception{
        if(args.length < 2 || Integer.parseInt(args[0]) < 105){
            System.out.println("Error: Issue with arguments! The first argument indicates the " +
            "size of the dictionary(must be >=96) and all arguments after must " +
            "be paths to txt files to be encoded.");
            System.exit(1);
        }
        try{
            Integer.parseInt(args[0]);
        }
        catch(IllegalArgumentException e){
            System.out.println(e + "\n" + "Issue with arguments! The first argument indicates the " +
            "size of the dictionary(must be >=96) and all arguments after must " +
            "be paths to txt files to be encoded.");
            System.exit(1);
        }
        int dictSize = Integer.parseInt(args[0]);
        String[] books = new String[args.length-1];
        for(int j=1;j<args.length;j++){
            if(Files.notExists(Paths.get(args[j])) == true){
                    System.out.println("Error: Issue with arguments! The first argument indicates the " +
                    "size of the dictionary(must be >=96) and all arguments after must " +
                    "be paths to txt files to be encoded.");
                    System.exit(1);
            }
            books[j-1] = args[j];
        }
        Map<String, Integer>[] freq = empiricalFreq(books);
        List<String> orderedDigr = new ArrayList<>();
        for(String uni:freq[0].keySet()){
            orderedDigr.add(uni);
        }
        List<String> filt = filter(freq[1], dictSize - freq[0].keySet().size());
        if(filt != null){
            for(String bi:filt){
                orderedDigr.add(bi);
            }
        }
        List<String[]> digr = (encoding(orderedDigr,dictSize));
        String outText = "";
        for (String[] bin:digr) {
            outText += bin[1] + "\t" + bin[0] + "\n";
            System.out.println(bin[1] + "\t" + bin[0]);
        }
        try {
            FileWriter dXXX = new FileWriter("Digr" + dictSize + ".txt");
            dXXX.write(outText);
            dXXX.close();
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
    }

    public static String readBook(String[] books) throws Exception{
        String data = "";
        for(int k = 0;k < books.length;k++){
            data += new String(Files.readAllBytes(Paths.get(books[k])));
        }
        return data;
    }
    public static Map[] empiricalFreq(String[] books) throws Exception{
        String text = readBook(books);
        Map<String, Integer> uniFreq = new HashMap<String, Integer>();
        Map<String, Integer> biFreq = new HashMap<String, Integer>();
        int i = 0;
        int j = 1;
        while(i < text.length()){
            int c = (int)text.charAt(i);
            if(j == text.length()-1){
                i = j;
                if((c >= 32 && c<=126) || c == 9 || c == 10){
                    String curr = "" + text.charAt(i);
                    Integer val = uniFreq.get(curr);
                    if(val != null){
                        uniFreq.put(curr, val + 1);
                    }
                    else{
                        uniFreq.put(curr, 1);
                    }
                }
                else{
                    String curr = "@";
                    Integer val = uniFreq.get(curr);
                    if(val != null){
                        uniFreq.put(curr, val + 1);
                    }
                    else{
                        uniFreq.put(curr, 1);
                    }
                }
                break;   
            }
            int d = (int)text.charAt(j);
            if((c >= 32 && c<=126) || c == 9 || c == 10){
                String curr = "" + text.charAt(i);
                Integer val = uniFreq.get(curr);
                if(val != null){
                    uniFreq.put(curr, val + 1);
                }
                else{
                    uniFreq.put(curr, 1);
                }
                if((d >= 32 && d <=126) || d == 9 || d == 10){
                    curr = "" + text.charAt(i) + text.charAt(j);
                    if(val != null){
                        biFreq.put(curr, val + 1);
                    }
                    else{
                        biFreq.put(curr, 1);
                    }
                    i++;
                    j = i+1;
                }
                else{
                    j++;
                    continue;
                }
            }
            else{
                String curr = "@";
                Integer val = uniFreq.get(curr);
                if(val != null){
                    uniFreq.put(curr, val + 1);
                }
                else{
                    uniFreq.put(curr, 1);
                }
                i++;
                j = i+1;
                continue;
            }
        }
            Map<String, Integer>[] freq = new Map[2];
            freq[0] = uniFreq;
            freq[1] = biFreq;

        return freq;
    }

    public static List<String> filter(Map<String, Integer> biFreq, int size){
        if(size <= 0){
            return null;
        }
        PriorityQueue<String> topN = new PriorityQueue<String>(size, new Comparator<String>(){
            public int compare(String s1, String s2) {
                return Integer.compare(biFreq.get(s1), biFreq.get(s2));
            }
        });
    
        for(String key:biFreq.keySet()){
            if (topN.size() < size)
                topN.add(key);
            else if (biFreq.get(topN.peek()) < biFreq.get(key)) {
                topN.poll();
                topN.add(key);
            }
        }
        List<String> sortList = (List) Arrays.asList(topN.toArray());
        Collections.sort(sortList);
        return sortList;
    }

    public static List encoding(List<String> source, int size){
        List<String[]> encoded = new ArrayList<String[]>();
        if(size <= 256){
            for(int i = 0;i < size;i++){
                String[] bin = {source.get(i), String.format("%08d", Integer.parseInt(Integer.toString(i, 2), 10))};
                encoded.add(bin);
            }
            return encoded;
        }
        else if(size > 256 && size <= 512){
            for(int i = 0;i < size;i++){
                String[] bin = {source.get(i), String.format("%09d", Integer.parseInt(Integer.toString(i, 2), 10))};
                encoded.add(bin);
            }
            return encoded;
        }
        else if(size > 512 && size <= 1024){
            for(int i = 0;i < size;i++){
                String[] bin = {source.get(i), String.format("%010d", Integer.parseInt(Integer.toString(i, 2), 10))};
                encoded.add(bin);
            }
            return encoded;
        }
        else{
            System.out.println("Error: Too big of a requested dictionary size! It must be " +
            "smaller than 1024");
            System.exit(1);
            return null;
        }
    }

}
