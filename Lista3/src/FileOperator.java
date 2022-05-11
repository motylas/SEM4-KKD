import java.io.File;
import java.io.FileOutputStream;

public class FileOperator {
    public static void out(String output, String path) {
        File file = new File(path);
        try {
            FileOutputStream fout = new FileOutputStream(file);
            while (!output.isEmpty()) {
                if (output.length() < 8) {
                    while (output.length() < 8){
                        output += '1';
                    }
                }
                String temp = output.substring(0, 8);
                output = output.substring(8);
                char charToWrite = 0;
                for (int i = 0; i < temp.length(); i++) {
                    if (temp.charAt(i) == '1') {
                        charToWrite += Math.pow(2, temp.length() - 1 - i);
                    }
                }
                fout.write(charToWrite);
            }
        } catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
