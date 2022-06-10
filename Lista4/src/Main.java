import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Blad: niepoprawna liczba argumentow");
            System.out.println("Schemat argumentow: [plik_wejsciowy] [plik_wyjsciowy] [liczba_kolorow]");
        } else {
            var input = args[0];
            var output = args[1];
            int colors = 0;
            try {
                colors = Integer.parseInt(args[2]);
                if (colors < 0 || colors > 24) {
                    throw new IndexOutOfBoundsException("liczba kolorow musi wynosic miedzy 0 a 24");
                }
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                System.out.println("Blad: " + e.getMessage());
            }
            try {
                byte[] content = Files.readAllBytes(Paths.get(input));
                Quantizer quantizer = new Quantizer(content, (int) Math.pow(2, colors));
                byte[] result = quantizer.encode();
                Files.write(Paths.get(output), result);
                System.out.println("\n========= Zakodowano plik: " + input + " =========");
                double MSError = quantizer.mse();
                System.out.println("Blad sredniokwadratowy: " + MSError);
                System.out.println("Stosunek sygnalu do szumu: " + quantizer.snr(MSError));
            } catch (IOException ioe) {
                System.out.println("Blad: " + ioe.getMessage());
            }
        }
    }
}