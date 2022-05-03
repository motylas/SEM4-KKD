import java.util.*;

public class LZW {
    public static List<Integer> lzwE(String text) {
        int dictSize = 256;
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
        int dictSize = 256;
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

    public static List<Integer> eliasOD(List<String> numbersToDecode) {
        List<Integer> decodedNumbers = new ArrayList<>();
        while (!numbersToDecode.isEmpty()) {
            String number = numbersToDecode.remove(0);
            int n = 1;
            while (number.charAt(0) == '1') {
                int len = n;
                n = 0;
                String temp = number.substring(0, len + 1);
                n = Integer.parseInt(temp, 2);
                number = number.substring(len + 1);
            }
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

    public static List<Integer> eliasGD(List<String> numbersToDecode){
        List<Integer> decodedNumbers = new ArrayList<>();
        while (!numbersToDecode.isEmpty()) {
            String number = numbersToDecode.remove(0);
            int n = 1;
            while(number.charAt(0) != '1'){
                number = number.substring(1);
                n++;
            }
            String temp = number.substring(0,n);
            int output = Integer.parseInt(temp, 2);
            decodedNumbers.add(output);
        }
        return decodedNumbers;
    }

    public static List<String> eliasDE(List<Integer> numbersToEncode){
        List<String> encodedNumbers = new ArrayList<>();
        while (!numbersToEncode.isEmpty()) {
            StringBuilder output = new StringBuilder();
            int number = numbersToEncode.remove(0);
            int len = 0;
            int lengthOfLen = 0;


            len = 1 + (int) Math.floor(log2(number));
            lengthOfLen = (int) Math.floor(log2(len));

            output.append("0".repeat(Math.max(0, lengthOfLen)));
            for (int i = lengthOfLen; i>=0; --i){
                if (((len >> i) & 1) == 1) {
                    output.append( "1");
                } else output.append("0");
            }
            for (int i = len-2; i>=0;i--){
                if (((number >> i) & 1) == 1) {
                    output.append( "1");
                } else output.append("0");
            }
            encodedNumbers.add(output.toString());
        }
        return encodedNumbers;
    }

    public static List<Integer> eliasDD(List<String> numbersToDecode){
        List<Integer> decodedNumbers = new ArrayList<>();
        while (!numbersToDecode.isEmpty()){
            String number = numbersToDecode.remove(0);
            int l = 1;
            while(number.charAt(0) == '0'){
                number = number.substring(1);
                l++;
            }
            String temp = number.substring(0,l);
            number = number.substring(l);
            int n = Integer.parseInt(temp, 2) - 1;
            int output = Integer.parseInt(number.substring(0,n),2);
            output += Math.pow(2, n);
            decodedNumbers.add(output);
        }
        return decodedNumbers;
    }

    private static double log2(int number) {
        return Math.log(number) / Math.log(2);
    }

    public static void main(String[] args) {
        List<Integer> compressed = lzwE("abababab");
        System.out.println(compressed);
        var code = eliasDE(compressed);
        System.out.println(code);
        var decode = eliasDD(code);
        System.out.println(decode);
        String decompressed = lzwD(decode);
        System.out.println(decompressed);
//        List<Integer> test = new ArrayList<>();
//        test.add(5);
//        test.add(3);
//        test.add(15);
//        test.add(12);
//        test.add(17);
//        var x = eliasDE(test);
//        System.out.println(x);
//        System.out.println(eliasDD(x));
    }
}
