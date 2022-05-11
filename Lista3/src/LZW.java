import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class LZW {
    final static int dictUTF = 1112064;
    final static int dictASCII = 256;

    public static List<Integer> lzwE(String text) {
        int dictSize = dictASCII;
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
//        System.out.println(input);
        List<Integer> decodedNumbers = new ArrayList<>();
        while (!input.isEmpty()) {
            int n = 1;
            try {
                while (input.charAt(0) == '1') {
                    int len = n;
                    n = 0;
                    String temp = input.substring(0, len + 1);
                    n = Integer.parseInt(temp, 2);
                    input = input.substring(len + 1);
                }
                input = input.substring(1);
                if (n > -1)
                    decodedNumbers.add(n);
            } catch (Exception e) {
                return decodedNumbers;
            }
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
            try {
                while (input.charAt(0) != '1') {
                    input = input.substring(1);
                    n++;
                }
                String temp = input.substring(0, n);
                int output = Integer.parseInt(temp, 2);
                decodedNumbers.add(output);
                input = input.substring(n);
            } catch (Exception e) {
                return decodedNumbers;
            }
        }
        return decodedNumbers;
    }

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

    public static List<Integer> eliasDD(String input) {
        List<Integer> decodedNumbers = new ArrayList<>();
        while (!input.isEmpty()) {
            try {
                int l = 1;
                while (input.charAt(0) == '0') {
                    input = input.substring(1);
                    l++;
                }
                String temp = input.substring(0, l);
                input = input.substring(l);
                int n = Integer.parseInt(temp, 2) - 1;
                int output = Integer.parseInt(input.substring(0, n), 2);
                input = input.substring(n);
                output += Math.pow(2, n);
                decodedNumbers.add(output);
            } catch (Exception e) {
                return decodedNumbers;
            }
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

    public static List<Integer> fibbD(String input) {
        List<Integer> decodedNumbers = new ArrayList<>();
        while (!input.isEmpty()) {
            try {
                char lastChar = '2';
                String number = "";
                int counter = 0;
                for (char character : input.toCharArray()) {
                    if (character == '1' && lastChar == '1') {
                        number = input.substring(0, counter);
                        try {
                            input = input.substring(counter + 1);
                        } catch (Exception e) {
                            input = "";
                        }
                        break;
                    }
                    lastChar = character;
                    counter++;
                }
                int output = 0;
                int index = 0;
                for (char character : number.toCharArray()) {
                    if (character == '1') {
                        output += fibbNumber(index + 2);
                    }
                    index++;
                }
                decodedNumbers.add(output);
            } catch (Exception e){
                return decodedNumbers;
            }
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

    static double countEntropy(HashMap<Integer, Integer> map, int codeLength){
        double entropy = 0;
        for(Map.Entry<Integer, Integer> entry: map.entrySet()){
            int value = entry.getValue();
            entropy += value*(-1)*Math.log10(value);
        }
        entropy = entropy/(Math.log10(2)*codeLength)+Math.log10(codeLength)/Math.log10(2);
        return entropy;
    }


    public static void main(String[] args) throws IOException {
        System.out.println("PAN TADEUSZ");
        HashMap<Integer, Integer> pt1 = new HashMap<>();
        HashMap<Integer, Integer> pteO = new HashMap<>();
        HashMap<Integer, Integer> pteG = new HashMap<>();
        HashMap<Integer, Integer> pteD = new HashMap<>();
        HashMap<Integer, Integer> ptfi = new HashMap<>();
        String fileName = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\testy1\\pan-tadeusz.txt";
        DataInputStream inputStream = new DataInputStream(new FileInputStream(fileName));
        int byteRead = -1;
        int codeLength1 = 0;
        Integer count = null;
        StringBuilder input = new StringBuilder();
        while ((byteRead = inputStream.read()) != -1) {
            codeLength1++;
            if ((count = pt1.putIfAbsent(byteRead,1)) != null){
                pt1.put(byteRead,count + 1);
            }
            input.append((char) byteRead);
        }
        System.out.println("Długość pliku wejściowego: " + codeLength1);
        System.out.println("Entropia pliku wejściowego: " + countEntropy(pt1, codeLength1));
        long start = System.currentTimeMillis();
        List<Integer> eD = lzwE(input.toString());
        List<Integer> eG = new ArrayList<>();
        List<Integer> eO = new ArrayList<>();
        List<Integer> f = new ArrayList<>();
        long end = System.currentTimeMillis();
        System.out.println("Czas kodowania LZW w ms: " + (end - start));
        for (int n: eD){
            eG.add(n);
            eO.add(n);
            f.add(n);
        }
        // Elias Delta
        start = System.currentTimeMillis();
        List<String> eDout = eliasDE(eD);
        StringBuilder sb = new StringBuilder();
        for (String st: eDout)  sb.append(st);
        FileOperator.out(sb.toString(), "pteliasD.txt");
        end = System.currentTimeMillis();
        System.out.println("Czas kodowania Eliasa Delta z zapisem do pliku w s" + (end - start)/1000);

        // Elias Gamma
        start = System.currentTimeMillis();
        List<String> eGout = eliasGE(eG);
        sb = new StringBuilder();
        for (String st: eGout)  sb.append(st);
        FileOperator.out(sb.toString(), "pteliasG.txt");
        end = System.currentTimeMillis();
        System.out.println("Czas kodowania Eliasa Gamma z zapisem do pliku w s" + (end - start)/1000);

        // Elias Omega
        start = System.currentTimeMillis();
        List<String> eOout = eliasOE(eO);
        sb = new StringBuilder();
        for (String st: eOout)  sb.append(st);
        FileOperator.out(sb.toString(), "pteliasO.txt");
        end = System.currentTimeMillis();
        System.out.println("Czas kodowania Eliasa Omega z zapisem do pliku w s" + (end - start)/1000);

        // Fibbonacci
        start = System.currentTimeMillis();
        List<String> fout = fibbE(f);
        sb = new StringBuilder();
        for (String st: fout)  sb.append(st);
        FileOperator.out(sb.toString(), "ptfibb.txt");
        end = System.currentTimeMillis();
        System.out.println("Czas kodowania Fibbonacciego z zapisem do pliku w s" + (end - start)/1000);

        // Elias Delta Decode
        start = System.currentTimeMillis();
        fileName = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\Lista3\\pteliasD.txt";
        inputStream = new DataInputStream(new FileInputStream(fileName));
        input = new StringBuilder();
        int codeLengthED = 0;
        while ((byteRead = inputStream.read()) != -1) {
            codeLengthED++;
            if ((count = pteD.putIfAbsent(byteRead,1)) != null){
                pteD.put(byteRead,count + 1);
            }
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                if (byteRead % 2 == 1) {
                    temp.insert(0, "1");
                } else {
                    temp.insert(0, "0");
                }
                byteRead /= 2;
            }
            input.append(temp);
        }
        List<Integer> lzwDecode = eliasDD(input.toString());
        end = System.currentTimeMillis();
        System.out.println("Czas dekodowania Eliasa Delta z odczytem z pliku w s: " + (end-start)/1000);
        System.out.println("Długość pliku zakodowanego: " + codeLengthED);
        System.out.println("Entropia pliku zakodowanego: " + countEntropy(pteD, codeLengthED));
        System.out.println("Procent kompresji: " + (codeLengthED * 100.0 /codeLength1) + "%");

        // Elias Gamma Decode
        start = System.currentTimeMillis();
        fileName = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\Lista3\\pteliasG.txt";
        inputStream = new DataInputStream(new FileInputStream(fileName));
        input = new StringBuilder();
        int codeLengthEG = 0;
        while ((byteRead = inputStream.read()) != -1) {
            codeLengthEG++;
            if ((count = pteG.putIfAbsent(byteRead,1)) != null){
                pteG.put(byteRead,count + 1);
            }
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                if (byteRead % 2 == 1) {
                    temp.insert(0, "1");
                } else {
                    temp.insert(0, "0");
                }
                byteRead /= 2;
            }
            input.append(temp);
        }
        eliasGD(input.toString());
        end = System.currentTimeMillis();
        System.out.println("Czas dekodowania Eliasa Gamma z odczytem z pliku w s: " + (end-start)/1000);
        System.out.println("Długość pliku zakodowanego: " + codeLengthEG);
        System.out.println("Entropia pliku zakodowanego: " + countEntropy(pteG, codeLengthEG));
        System.out.println("Procent kompresji: " + (codeLengthEG * 100.0 /codeLength1) + "%");

        // Elias Omega Decode
        start = System.currentTimeMillis();
        fileName = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\Lista3\\pteliasO.txt";
        inputStream = new DataInputStream(new FileInputStream(fileName));
        input = new StringBuilder();
        int codeLengthEO = 0;
        while ((byteRead = inputStream.read()) != -1) {
            codeLengthEO++;
            if ((count = pteO.putIfAbsent(byteRead,1)) != null){
                pteO.put(byteRead,count + 1);
            }
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                if (byteRead % 2 == 1) {
                    temp.insert(0, "1");
                } else {
                    temp.insert(0, "0");
                }
                byteRead /= 2;
            }
            input.append(temp);
        }
        eliasOD(input.toString());
        end = System.currentTimeMillis();
        System.out.println("Czas dekodowania Eliasa Omega z odczytem z pliku w s: " + (end-start)/1000);
        System.out.println("Długość pliku zakodowanego: " + codeLengthEO);
        System.out.println("Entropia pliku zakodowanego: " + countEntropy(pteO, codeLengthEO));
        System.out.println("Procent kompresji: " + (codeLengthEO * 100.0 /codeLength1) + "%");

        // Fibbonacci Decode
        start = System.currentTimeMillis();
        fileName = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\Lista3\\ptfibb.txt";
        inputStream = new DataInputStream(new FileInputStream(fileName));
        input = new StringBuilder();
        int codeLengthF = 0;
        while ((byteRead = inputStream.read()) != -1) {
            codeLengthF++;
            if ((count = ptfi.putIfAbsent(byteRead,1)) != null){
                ptfi.put(byteRead,count + 1);
            }
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                if (byteRead % 2 == 1) {
                    temp.insert(0, "1");
                } else {
                    temp.insert(0, "0");
                }
                byteRead /= 2;
            }
            input.append(temp);
        }
        fibbD(input.toString());
        end = System.currentTimeMillis();
        System.out.println("Czas dekodowania fibbonacciego z odczytem z pliku w s: " + (end-start)/1000);
        System.out.println("Długość pliku zakodowanego: " + codeLengthF);
        System.out.println("Entropia pliku zakodowanego: " + countEntropy(ptfi, codeLengthF));
        System.out.println("Procent kompresji: " + (codeLengthF * 100.0 /codeLength1) + "%");

        // LZW Decode
        start = System.currentTimeMillis();
        lzwD(lzwDecode);
        end = System.currentTimeMillis();
        System.out.println("Czas dekodowania LZW w ms: " + (end-start));


        System.out.println("Test1");
        HashMap<Integer, Integer> t1 = new HashMap<>();
        HashMap<Integer, Integer> t1eO = new HashMap<>();
        HashMap<Integer, Integer> t1eG = new HashMap<>();
        HashMap<Integer, Integer> t1eD = new HashMap<>();
        HashMap<Integer, Integer> t1fi = new HashMap<>();
        fileName = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\testy1\\test1.bin";
        inputStream = new DataInputStream(new FileInputStream(fileName));
        byteRead = -1;
        codeLength1 = 0;
        input = new StringBuilder();
        while ((byteRead = inputStream.read()) != -1) {
            codeLength1++;
            if ((count = t1.putIfAbsent(byteRead,1)) != null){
                t1.put(byteRead,count + 1);
            }
            input.append((char) byteRead);
        }
        System.out.println("Długość pliku wejściowego: " + codeLength1);
        System.out.println("Entropia pliku wejściowego: " + countEntropy(pt1, codeLength1));
        start = System.currentTimeMillis();
        eD = lzwE(input.toString());
        eG = new ArrayList<>();
        eO = new ArrayList<>();
        f = new ArrayList<>();
        end = System.currentTimeMillis();
        System.out.println("Czas kodowania LZW w ms: " + (end - start));
        for (int n: eD){
            eG.add(n);
            eO.add(n);
            f.add(n);
        }
        // Elias Delta
        start = System.currentTimeMillis();;
        eDout = eliasDE(eD);
        sb = new StringBuilder();
        for (String st: eDout)  sb.append(st);
        FileOperator.out(sb.toString(), "t1eliasD.txt");
        end = System.currentTimeMillis();
        System.out.println("Czas kodowania Eliasa Delta z zapisem do pliku w s" + (end - start)/1000);

        // Elias Gamma
        start = System.currentTimeMillis();
        eGout = eliasGE(eG);
        sb = new StringBuilder();
        for (String st: eGout)  sb.append(st);
        FileOperator.out(sb.toString(), "t1eliasG.txt");
        end = System.currentTimeMillis();
        System.out.println("Czas kodowania Eliasa Gamma z zapisem do pliku w s" + (end - start)/1000);

        // Elias Omega
        start = System.currentTimeMillis();
        eOout = eliasOE(eO);
        sb = new StringBuilder();
        for (String st: eOout)  sb.append(st);
        FileOperator.out(sb.toString(), "t1eliasO.txt");
        end = System.currentTimeMillis();
        System.out.println("Czas kodowania Eliasa Omega z zapisem do pliku w s" + (end - start)/1000);

        // Fibbonacci
        start = System.currentTimeMillis();
        fout = fibbE(f);
        sb = new StringBuilder();
        for (String st: fout)  sb.append(st);
        FileOperator.out(sb.toString(), "t1fibb.txt");
        end = System.currentTimeMillis();
        System.out.println("Czas kodowania Fibbonacciego z zapisem do pliku w s" + (end - start)/1000);

        // Elias Delta Decode
        start = System.currentTimeMillis();
        fileName = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\Lista3\\t1eliasD.txt";
        inputStream = new DataInputStream(new FileInputStream(fileName));
        input = new StringBuilder();
        codeLengthED = 0;
        while ((byteRead = inputStream.read()) != -1) {
            codeLengthED++;
            if ((count = t1eD.putIfAbsent(byteRead,1)) != null){
                t1eD.put(byteRead,count + 1);
            }
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                if (byteRead % 2 == 1) {
                    temp.insert(0, "1");
                } else {
                    temp.insert(0, "0");
                }
                byteRead /= 2;
            }
            input.append(temp);
        }
        lzwDecode = eliasDD(input.toString());
        end = System.currentTimeMillis();
        System.out.println("Czas dekodowania Eliasa Delta z odczytem z pliku w s: " + (end-start)/1000);
        System.out.println("Długość pliku zakodowanego: " + codeLengthED);
        System.out.println("Entropia pliku zakodowanego: " + countEntropy(pteD, codeLengthED));
        System.out.println("Procent kompresji: " + (codeLengthED * 100.0 /codeLength1) + "%");

        // Elias Gamma Decode
        start = System.currentTimeMillis();
        fileName = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\Lista3\\t1eliasG.txt";
        inputStream = new DataInputStream(new FileInputStream(fileName));
        input = new StringBuilder();
        codeLengthEG = 0;
        while ((byteRead = inputStream.read()) != -1) {
            codeLengthEG++;
            if ((count = t1eG.putIfAbsent(byteRead,1)) != null){
                t1eG.put(byteRead,count + 1);
            }
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                if (byteRead % 2 == 1) {
                    temp.insert(0, "1");
                } else {
                    temp.insert(0, "0");
                }
                byteRead /= 2;
            }
            input.append(temp);
        }
        eliasGD(input.toString());
        end = System.currentTimeMillis();
        System.out.println("Czas dekodowania Eliasa Gamma z odczytem z pliku w s: " + (end-start)/1000);
        System.out.println("Długość pliku zakodowanego: " + codeLengthEG);
        System.out.println("Entropia pliku zakodowanego: " + countEntropy(pteG, codeLengthEG));
        System.out.println("Procent kompresji: " + (codeLengthEG * 100.0 /codeLength1) + "%");

        // Elias Omega Decode
        start = System.currentTimeMillis();
        fileName = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\Lista3\\t1eliasO.txt";
        inputStream = new DataInputStream(new FileInputStream(fileName));
        input = new StringBuilder();
        codeLengthEO = 0;
        while ((byteRead = inputStream.read()) != -1) {
            codeLengthEO++;
            if ((count = t1eO.putIfAbsent(byteRead,1)) != null){
                t1eO.put(byteRead,count + 1);
            }
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                if (byteRead % 2 == 1) {
                    temp.insert(0, "1");
                } else {
                    temp.insert(0, "0");
                }
                byteRead /= 2;
            }
            input.append(temp);
        }
        eliasOD(input.toString());
        end = System.currentTimeMillis();
        System.out.println("Czas dekodowania Eliasa Omega z odczytem z pliku w s: " + (end-start)/1000);
        System.out.println("Długość pliku zakodowanego: " + codeLengthEO);
        System.out.println("Entropia pliku zakodowanego: " + countEntropy(pteO, codeLengthEO));
        System.out.println("Procent kompresji: " + (codeLengthEO * 100.0 /codeLength1) + "%");

        // Fibbonacci Decode
        start = System.currentTimeMillis();
        fileName = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\Lista3\\t1fibb.txt";
        inputStream = new DataInputStream(new FileInputStream(fileName));
        input = new StringBuilder();
        codeLengthF = 0;
        while ((byteRead = inputStream.read()) != -1) {
            codeLengthF++;
            if ((count = t1fi.putIfAbsent(byteRead,1)) != null){
                t1fi.put(byteRead,count + 1);
            }
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                if (byteRead % 2 == 1) {
                    temp.insert(0, "1");
                } else {
                    temp.insert(0, "0");
                }
                byteRead /= 2;
            }
            input.append(temp);
        }
        fibbD(input.toString());
        end = System.currentTimeMillis();
        System.out.println("Czas dekodowania fibbonacciego z odczytem z pliku w s: " + (end-start)/1000);
        System.out.println("Długość pliku zakodowanego: " + codeLengthF);
        System.out.println("Entropia pliku zakodowanego: " + countEntropy(ptfi, codeLengthF));
        System.out.println("Procent kompresji: " + (codeLengthF * 100.0 /codeLength1) + "%");

        // LZW Decode
        start = System.currentTimeMillis();
        lzwD(lzwDecode);
        end = System.currentTimeMillis();
        System.out.println("Czas dekodowania LZW w ms: " + (end-start));



        System.out.println("Test2");
        HashMap<Integer, Integer> t2 = new HashMap<>();
        HashMap<Integer, Integer> t2eO = new HashMap<>();
        HashMap<Integer, Integer> t2eG = new HashMap<>();
        HashMap<Integer, Integer> t2eD = new HashMap<>();
        HashMap<Integer, Integer> t2fi = new HashMap<>();
        fileName = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\testy1\\test2.bin";
        inputStream = new DataInputStream(new FileInputStream(fileName));
        byteRead = -1;
        codeLength1 = 0;
        input = new StringBuilder();
        while ((byteRead = inputStream.read()) != -1) {
            codeLength1++;
            if ((count = t2.putIfAbsent(byteRead,1)) != null){
                t2.put(byteRead,count + 1);
            }
            input.append((char) byteRead);
        }
        System.out.println("Długość pliku wejściowego: " + codeLength1);
        System.out.println("Entropia pliku wejściowego: " + countEntropy(pt1, codeLength1));
        start = System.currentTimeMillis();
        eD = lzwE(input.toString());
        eG = new ArrayList<>();
        eO = new ArrayList<>();
        f = new ArrayList<>();
        end = System.currentTimeMillis();
        System.out.println("Czas kodowania LZW w ms: " + (end - start));
        for (int n: eD){
            eG.add(n);
            eO.add(n);
            f.add(n);
        }
        // Elias Delta
        start = System.currentTimeMillis();;
        eDout = eliasDE(eD);
        sb = new StringBuilder();
        for (String st: eDout)  sb.append(st);
        FileOperator.out(sb.toString(), "t2eliasD.txt");
        end = System.currentTimeMillis();
        System.out.println("Czas kodowania Eliasa Delta z zapisem do pliku w s" + (end - start)/1000);

        // Elias Gamma
        start = System.currentTimeMillis();
        eGout = eliasGE(eG);
        sb = new StringBuilder();
        for (String st: eGout)  sb.append(st);
        FileOperator.out(sb.toString(), "t2eliasG.txt");
        end = System.currentTimeMillis();
        System.out.println("Czas kodowania Eliasa Gamma z zapisem do pliku w s" + (end - start)/1000);

        // Elias Omega
        start = System.currentTimeMillis();
        eOout = eliasOE(eO);
        sb = new StringBuilder();
        for (String st: eOout)  sb.append(st);
        FileOperator.out(sb.toString(), "t2eliasO.txt");
        end = System.currentTimeMillis();
        System.out.println("Czas kodowania Eliasa Omega z zapisem do pliku w s" + (end - start)/1000);

        // Fibbonacci
        start = System.currentTimeMillis();
        fout = fibbE(f);
        sb = new StringBuilder();
        for (String st: fout)  sb.append(st);
        FileOperator.out(sb.toString(), "t2fibb.txt");
        end = System.currentTimeMillis();
        System.out.println("Czas kodowania Fibbonacciego z zapisem do pliku w s" + (end - start)/1000);

        // Elias Delta Decode
        start = System.currentTimeMillis();
        fileName = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\Lista3\\t2eliasD.txt";
        inputStream = new DataInputStream(new FileInputStream(fileName));
        input = new StringBuilder();
        codeLengthED = 0;
        while ((byteRead = inputStream.read()) != -1) {
            codeLengthED++;
            if ((count = t2eD.putIfAbsent(byteRead,1)) != null){
                t2eD.put(byteRead,count + 1);
            }
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                if (byteRead % 2 == 1) {
                    temp.insert(0, "1");
                } else {
                    temp.insert(0, "0");
                }
                byteRead /= 2;
            }
            input.append(temp);
        }
        lzwDecode = eliasDD(input.toString());
        end = System.currentTimeMillis();
        System.out.println("Czas dekodowania Eliasa Delta z odczytem z pliku w s: " + (end-start)/1000);
        System.out.println("Długość pliku zakodowanego: " + codeLengthED);
        System.out.println("Entropia pliku zakodowanego: " + countEntropy(pteD, codeLengthED));
        System.out.println("Procent kompresji: " + (codeLengthED * 100.0 /codeLength1) + "%");

        // Elias Gamma Decode
        start = System.currentTimeMillis();
        fileName = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\Lista3\\t2eliasG.txt";
        inputStream = new DataInputStream(new FileInputStream(fileName));
        input = new StringBuilder();
        codeLengthEG = 0;
        while ((byteRead = inputStream.read()) != -1) {
            codeLengthEG++;
            if ((count = t2eG.putIfAbsent(byteRead,1)) != null){
                t2eG.put(byteRead,count + 1);
            }
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                if (byteRead % 2 == 1) {
                    temp.insert(0, "1");
                } else {
                    temp.insert(0, "0");
                }
                byteRead /= 2;
            }
            input.append(temp);
        }
        eliasGD(input.toString());
        end = System.currentTimeMillis();
        System.out.println("Czas dekodowania Eliasa Gamma z odczytem z pliku w s: " + (end-start)/1000);
        System.out.println("Długość pliku zakodowanego: " + codeLengthEG);
        System.out.println("Entropia pliku zakodowanego: " + countEntropy(pteG, codeLengthEG));
        System.out.println("Procent kompresji: " + (codeLengthEG * 100.0 /codeLength1) + "%");

        // Elias Omega Decode
        start = System.currentTimeMillis();
        fileName = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\Lista3\\t2eliasO.txt";
        inputStream = new DataInputStream(new FileInputStream(fileName));
        input = new StringBuilder();
        codeLengthEO = 0;
        while ((byteRead = inputStream.read()) != -1) {
            codeLengthEO++;
            if ((count = t2eO.putIfAbsent(byteRead,1)) != null){
                t2eO.put(byteRead,count + 1);
            }
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                if (byteRead % 2 == 1) {
                    temp.insert(0, "1");
                } else {
                    temp.insert(0, "0");
                }
                byteRead /= 2;
            }
            input.append(temp);
        }
        eliasOD(input.toString());
        end = System.currentTimeMillis();
        System.out.println("Czas dekodowania Eliasa Omega z odczytem z pliku w s: " + (end-start)/1000);
        System.out.println("Długość pliku zakodowanego: " + codeLengthEO);
        System.out.println("Entropia pliku zakodowanego: " + countEntropy(pteO, codeLengthEO));
        System.out.println("Procent kompresji: " + (codeLengthEO * 100.0 /codeLength1) + "%");

        // Fibbonacci Decode
        start = System.currentTimeMillis();
        fileName = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\Lista3\\t2fibb.txt";
        inputStream = new DataInputStream(new FileInputStream(fileName));
        input = new StringBuilder();
        codeLengthF = 0;
        while ((byteRead = inputStream.read()) != -1) {
            codeLengthF++;
            if ((count = t2fi.putIfAbsent(byteRead,1)) != null){
                t2fi.put(byteRead,count + 1);
            }
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                if (byteRead % 2 == 1) {
                    temp.insert(0, "1");
                } else {
                    temp.insert(0, "0");
                }
                byteRead /= 2;
            }
            input.append(temp);
        }
        fibbD(input.toString());
        end = System.currentTimeMillis();
        System.out.println("Czas dekodowania fibbonacciego z odczytem z pliku w s: " + (end-start)/1000);
        System.out.println("Długość pliku zakodowanego: " + codeLengthF);
        System.out.println("Entropia pliku zakodowanego: " + countEntropy(ptfi, codeLengthF));
        System.out.println("Procent kompresji: " + (codeLengthF * 100.0 /codeLength1) + "%");

        // LZW Decode
        start = System.currentTimeMillis();
        lzwD(lzwDecode);
        end = System.currentTimeMillis();
        System.out.println("Czas dekodowania LZW w ms: " + (end-start));




        System.out.println("Test3");
        HashMap<Integer, Integer> t3 = new HashMap<>();
        HashMap<Integer, Integer> t3eO = new HashMap<>();
        HashMap<Integer, Integer> t3eG = new HashMap<>();
        HashMap<Integer, Integer> t3eD = new HashMap<>();
        HashMap<Integer, Integer> t3fi = new HashMap<>();
        fileName = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\testy1\\test3.bin";
        inputStream = new DataInputStream(new FileInputStream(fileName));
        byteRead = -1;
        codeLength1 = 0;
        input = new StringBuilder();
        while ((byteRead = inputStream.read()) != -1) {
            codeLength1++;
            if ((count = t3.putIfAbsent(byteRead,1)) != null){
                t3.put(byteRead,count + 1);
            }
            input.append((char) byteRead);
        }
        System.out.println("Długość pliku wejściowego: " + codeLength1);
        System.out.println("Entropia pliku wejściowego: " + countEntropy(pt1, codeLength1));
        start = System.currentTimeMillis();
        eD = lzwE(input.toString());
        eG = new ArrayList<>();
        eO = new ArrayList<>();
        f = new ArrayList<>();
        end = System.currentTimeMillis();
        System.out.println("Czas kodowania LZW w ms: " + (end - start));
        for (int n: eD){
            eG.add(n);
            eO.add(n);
            f.add(n);
        }
        // Elias Delta
        start = System.currentTimeMillis();;
        eDout = eliasDE(eD);
        sb = new StringBuilder();
        for (String st: eDout)  sb.append(st);
        FileOperator.out(sb.toString(), "t3eliasD.txt");
        end = System.currentTimeMillis();
        System.out.println("Czas kodowania Eliasa Delta z zapisem do pliku w s" + (end - start)/1000);

        // Elias Gamma
        start = System.currentTimeMillis();
        eGout = eliasGE(eG);
        sb = new StringBuilder();
        for (String st: eGout)  sb.append(st);
        FileOperator.out(sb.toString(), "t3eliasG.txt");
        end = System.currentTimeMillis();
        System.out.println("Czas kodowania Eliasa Gamma z zapisem do pliku w s" + (end - start)/1000);

        // Elias Omega
        start = System.currentTimeMillis();
        eOout = eliasOE(eO);
        sb = new StringBuilder();
        for (String st: eOout)  sb.append(st);
        FileOperator.out(sb.toString(), "t3eliasO.txt");
        end = System.currentTimeMillis();
        System.out.println("Czas kodowania Eliasa Omega z zapisem do pliku w s" + (end - start)/1000);

        // Fibbonacci
        start = System.currentTimeMillis();
        fout = fibbE(f);
        sb = new StringBuilder();
        for (String st: fout)  sb.append(st);
        FileOperator.out(sb.toString(), "t3fibb.txt");
        end = System.currentTimeMillis();
        System.out.println("Czas kodowania Fibbonacciego z zapisem do pliku w s" + (end - start)/1000);

        // Elias Delta Decode
        start = System.currentTimeMillis();
        fileName = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\Lista3\\t3eliasD.txt";
        inputStream = new DataInputStream(new FileInputStream(fileName));
        input = new StringBuilder();
        codeLengthED = 0;
        while ((byteRead = inputStream.read()) != -1) {
            codeLengthED++;
            if ((count = t3eD.putIfAbsent(byteRead,1)) != null){
                t3eD.put(byteRead,count + 1);
            }
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                if (byteRead % 2 == 1) {
                    temp.insert(0, "1");
                } else {
                    temp.insert(0, "0");
                }
                byteRead /= 2;
            }
            input.append(temp);
        }
        lzwDecode = eliasDD(input.toString());
        end = System.currentTimeMillis();
        System.out.println("Czas dekodowania Eliasa Delta z odczytem z pliku w s: " + (end-start)/1000);
        System.out.println("Długość pliku zakodowanego: " + codeLengthED);
        System.out.println("Entropia pliku zakodowanego: " + countEntropy(pteD, codeLengthED));
        System.out.println("Procent kompresji: " + (codeLengthED * 100.0 /codeLength1) + "%");

        // Elias Gamma Decode
        start = System.currentTimeMillis();
        fileName = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\Lista3\\t3eliasG.txt";
        inputStream = new DataInputStream(new FileInputStream(fileName));
        input = new StringBuilder();
        codeLengthEG = 0;
        while ((byteRead = inputStream.read()) != -1) {
            codeLengthEG++;
            if ((count = t3eG.putIfAbsent(byteRead,1)) != null){
                t3eG.put(byteRead,count + 1);
            }
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                if (byteRead % 2 == 1) {
                    temp.insert(0, "1");
                } else {
                    temp.insert(0, "0");
                }
                byteRead /= 2;
            }
            input.append(temp);
        }
        eliasGD(input.toString());
        end = System.currentTimeMillis();
        System.out.println("Czas dekodowania Eliasa Gamma z odczytem z pliku w s: " + (end-start)/1000);
        System.out.println("Długość pliku zakodowanego: " + codeLengthEG);
        System.out.println("Entropia pliku zakodowanego: " + countEntropy(pteG, codeLengthEG));
        System.out.println("Procent kompresji: " + (codeLengthEG * 100.0 /codeLength1) + "%");

        // Elias Omega Decode
        start = System.currentTimeMillis();
        fileName = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\Lista3\\t3eliasO.txt";
        inputStream = new DataInputStream(new FileInputStream(fileName));
        input = new StringBuilder();
        codeLengthEO = 0;
        while ((byteRead = inputStream.read()) != -1) {
            codeLengthEO++;
            if ((count = t3eO.putIfAbsent(byteRead,1)) != null){
                t3eO.put(byteRead,count + 1);
            }
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                if (byteRead % 2 == 1) {
                    temp.insert(0, "1");
                } else {
                    temp.insert(0, "0");
                }
                byteRead /= 2;
            }
            input.append(temp);
        }
        eliasOD(input.toString());
        end = System.currentTimeMillis();
        System.out.println("Czas dekodowania Eliasa Omega z odczytem z pliku w s: " + (end-start)/1000);
        System.out.println("Długość pliku zakodowanego: " + codeLengthEO);
        System.out.println("Entropia pliku zakodowanego: " + countEntropy(pteO, codeLengthEO));
        System.out.println("Procent kompresji: " + (codeLengthEO * 100.0 /codeLength1) + "%");

        // Fibbonacci Decode
        start = System.currentTimeMillis();
        fileName = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\Lista3\\t3fibb.txt";
        inputStream = new DataInputStream(new FileInputStream(fileName));
        input = new StringBuilder();
        codeLengthF = 0;
        while ((byteRead = inputStream.read()) != -1) {
            codeLengthF++;
            if ((count = t3fi.putIfAbsent(byteRead,1)) != null){
                t3fi.put(byteRead,count + 1);
            }
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                if (byteRead % 2 == 1) {
                    temp.insert(0, "1");
                } else {
                    temp.insert(0, "0");
                }
                byteRead /= 2;
            }
            input.append(temp);
        }
        fibbD(input.toString());
        end = System.currentTimeMillis();
        System.out.println("Czas dekodowania fibbonacciego z odczytem z pliku w s: " + (end-start)/1000);
        System.out.println("Długość pliku zakodowanego: " + codeLengthF);
        System.out.println("Entropia pliku zakodowanego: " + countEntropy(ptfi, codeLengthF));
        System.out.println("Procent kompresji: " + (codeLengthF * 100.0 /codeLength1) + "%");

        // LZW Decode
        start = System.currentTimeMillis();
        lzwD(lzwDecode);
        end = System.currentTimeMillis();
        System.out.println("Czas dekodowania LZW w ms: " + (end-start));
    }
}