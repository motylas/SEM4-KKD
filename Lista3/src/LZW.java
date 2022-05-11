import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class LZW {
    final static int dictUTF = 1112064;
    final static int dictASCII = 256;
    public static List<Integer> lzwE(String text) {
        int dictSize  = dictASCII;
        Map<String, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < dictSize; i++) {
            dictionary.put(String.valueOf((char) i), i);
        }

        String foundChars = "";
        List<Integer> result = new ArrayList<>();
        for (char character : text.toCharArray()) {
            String charsToAdd = foundChars + character;
            if (dictionary.containsKey(charsToAdd)) {
                foundChars = charsToAdd;
            } else {
                result.add(dictionary.get(foundChars) + 1);
                dictionary.put(charsToAdd, dictSize++);
                foundChars = String.valueOf(character);
            }
        }
        if (!foundChars.isEmpty()) {
            result.add(dictionary.get(foundChars) + 1);
        }
        return result;
    }

    public static String lzwD(List<Integer> encodedText) {
        int dictSize = dictASCII;
        Map<Integer, String> dictionary = new HashMap<>();
        for (int i = 0; i < dictSize; i++) {
            dictionary.put(i, String.valueOf((char) i));
        }

        String characters = String.valueOf((char) (encodedText.remove(0) - 1));
        StringBuilder result = new StringBuilder(characters);
        for (int code : encodedText) {
            code--;
            String entry = dictionary.containsKey(code)
                    ? dictionary.get(code)
                    : characters + characters.charAt(0);
            result.append(entry);
            dictionary.put(dictSize++, characters + entry.charAt(0));
            characters = entry;
        }
        return result.toString();
    }

    public static List<String> eliasOE(List<Integer> numbersToEncode) {
        List<String> encodedNumbers = new ArrayList<>();
        while (!numbersToEncode.isEmpty()) {
            int number = numbersToEncode.remove(0);
            StringBuilder output = new StringBuilder();
            while (number > 1) {
                int len = 0;
                for (int temp = number; temp > 0; temp >>= 1) {
                    len++;
                }
                for (int i = 0; i < len; i++) {
                    if (((number >> i) & 1) == 1) {
                        output.insert(0, "1");
                    } else output.insert(0, "0");
                }
                number = len - 1;
            }
            output.append("0");
            encodedNumbers.add(output.toString());
        }
        return encodedNumbers;
    }

    public static List<Integer> eliasOD(String input) {
        List<Integer> decodedNumbers = new ArrayList<>();
        while (!input.isEmpty()) {
            int n = 1;
            while (input.charAt(0) == '1') {
                int len = n;
                n = 0;
                String temp = input.substring(0, len + 1);
                n = Integer.parseInt(temp, 2);
                input = input.substring(len + 1);
            }
            input = input.substring(1);
            decodedNumbers.add(n);
        }
        return decodedNumbers;
    }

    public static List<String> eliasGE(List<Integer> numbersToEncode) {
        List<String> encodedNumbers = new ArrayList<>();
        while (!numbersToEncode.isEmpty()) {
            StringBuilder output = new StringBuilder();
            int number = numbersToEncode.remove(0);
            int n = (int) Math.floor(log2(number));
            output.append("0".repeat(Math.max(0, n)));
            output.append(Integer.toBinaryString(number));
            encodedNumbers.add(output.toString());
        }
        return encodedNumbers;
    }

    public static List<Integer> eliasGD(String input) {
        List<Integer> decodedNumbers = new ArrayList<>();
        while (!input.isEmpty()) {
            int n = 1;
            while (input.charAt(0) != '1') {
                input = input.substring(1);
                n++;
            }
            String temp = input.substring(0, n);
            int output = Integer.parseInt(temp, 2);
            decodedNumbers.add(output);
            input = input.substring(n);
        }
        return decodedNumbers;
    }

//    public static List<Integer> eliasGD(List<String> numbersToDecode) {
//        List<Integer> decodedNumbers = new ArrayList<>();
//        while (!numbersToDecode.isEmpty()) {
//            String number = numbersToDecode.remove(0);
//            int n = 1;
//            while (number.charAt(0) != '1') {
//                number = number.substring(1);
//                n++;
//            }
//            String temp = number.substring(0, n);
//            System.out.println(number.substring(n));
//            int output = Integer.parseInt(temp, 2);
//            decodedNumbers.add(output);
//        }
//        return decodedNumbers;
//    }

    public static List<String> eliasDE(List<Integer> numbersToEncode) {
        List<String> encodedNumbers = new ArrayList<>();
        while (!numbersToEncode.isEmpty()) {
            StringBuilder output = new StringBuilder();
            int number = numbersToEncode.remove(0);
            int len = 0;
            int lengthOfLen = 0;


            len = 1 + (int) Math.floor(log2(number));
            lengthOfLen = (int) Math.floor(log2(len));

            output.append("0".repeat(Math.max(0, lengthOfLen)));
            for (int i = lengthOfLen; i >= 0; --i) {
                if (((len >> i) & 1) == 1) {
                    output.append("1");
                } else output.append("0");
            }
            for (int i = len - 2; i >= 0; i--) {
                if (((number >> i) & 1) == 1) {
                    output.append("1");
                } else output.append("0");
            }
            encodedNumbers.add(output.toString());
        }
        return encodedNumbers;
    }

    public static List<Integer> eliasDD(List<String> numbersToDecode) {
        List<Integer> decodedNumbers = new ArrayList<>();
        while (!numbersToDecode.isEmpty()) {
            String number = numbersToDecode.remove(0);
            int l = 1;
            while (number.charAt(0) == '0') {
                number = number.substring(1);
                l++;
            }
            String temp = number.substring(0, l);
            number = number.substring(l);
            int n = Integer.parseInt(temp, 2) - 1;
            int output = Integer.parseInt(number.substring(0, n), 2);
            output += Math.pow(2, n);
            decodedNumbers.add(output);
        }
        return decodedNumbers;
    }

    public static List<String> fibbE(List<Integer> numbersToEncode) {
        List<String> encodedNumbers = new ArrayList<>();
        while (!numbersToEncode.isEmpty()) {
            StringBuilder output = new StringBuilder();
            int number = numbersToEncode.remove(0);
            int n = 2;
            int fib = 1;
            while (true) {
                int temp;
                if ((temp = fibbNumber(n + 1)) > number) {
                    break;
                }
                n++;
                fib = temp;
            }
            output.append("0".repeat(Math.max(0, n - 1)));
            output.setCharAt(n - 2, '1');
            output.append('1');
            number -= fib;
            while (true) {
                if (number == 0) break;
                n = 2;
                fib = 1;
                while (true) {
                    int temp;
                    if ((temp = fibbNumber(n + 1)) > number) {
                        break;
                    }
                    n++;
                    fib = temp;
                }
                output.setCharAt(n - 2, '1');
                number -= fib;
            }
            encodedNumbers.add(output.toString());
        }
        return encodedNumbers;
    }

    public static List<Integer> fibbD(List<String> numbersToDecode) {
        List<Integer> decodedNumbers = new ArrayList<>();
        while (!numbersToDecode.isEmpty()) {
            String number = numbersToDecode.remove(0);
            number = number.substring(0, number.length() - 1);
            int output = 0;
            int index = 0;
            for (char character : number.toCharArray()) {
                if (character == '1') {
                    output += fibbNumber(index + 2);
                }
                index++;
            }
            decodedNumbers.add(output);
        }
        return decodedNumbers;
    }

    private static double log2(int number) {
        return Math.log(number) / Math.log(2);
    }

    private static int fibbNumber(int n) {
        if (n == 0) return 0;
        if (n == 1) return 1;
        if (n == 2) return 1;
        return fibbNumber(n - 1) + fibbNumber(n - 2);
    }

//    public static void main(String[] args) throws IOException {
//        Path path = Paths.get("C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\testy1\\pan-tadeusz.txt");
//        byte[] data = Files.readAllBytes(path);
//        String test = new String(data, StandardCharsets.US_ASCII);
//        List<Integer> compressed = lzwE(test);
////        List<String> coded = eliasDE(compressed);
////        for (String word: coded){
////
////        }
//    }

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        String fileName = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\testy1\\pan-tadeusz.txt";
        DataInputStream inputStream = new DataInputStream(new FileInputStream(fileName));
        int byteRead = -1;
        int codeLength = 0;
        Integer count = null;
        StringBuilder input = new StringBuilder();
        while ((byteRead = inputStream.read()) != -1) {
            codeLength++;
            input.append((char) byteRead);
        }
        List<Integer> x = lzwE(input.toString());
        var y = eliasGE(x);
        System.out.println("Ys");
        StringBuilder all = new StringBuilder();
        for (String st: y){
            all.append(st);
//            System.out.println(st);
        }
        System.out.println("YS2");
        var z = eliasGD(all.toString());
        System.out.println("YS3");
        System.out.println(lzwD(z));
        long end = System.currentTimeMillis();
        System.out.println((end-start));
    }
}
