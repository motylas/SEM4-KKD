import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        if (args.length < 3) return;
        String input = args[0];
        String output = args[1];
        int colors = 0;
        try {
            colors = Integer.parseInt(args[2]);
            if (colors < 0 || colors > 24) {
                System.out.println("Wrong color");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        try {
            Quantifier quantizer = new Quantifier(input, (int) Math.pow(2, colors));
            byte[] result = quantizer.encode();
            Files.write(Paths.get(output), result);
            double MSError = quantizer.mse();
            System.out.println("Blad sredniokwadratowy: " + MSError);
            System.out.println("Stosunek sygnalu do szumu: " + quantizer.snr(MSError));
        } catch (IOException ioe) {
            return;
        }
    }
}