import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Read TGA file and convert it to two-dimensional array of Pixels
 */
public class TGAParser {

    private Pixel[][] pixelsArray;
    byte[] header;
    byte[] footer;
    int width, height;

    public Pixel[][] getPixels() {
        return pixelsArray;
    }

    public TGAParser() {
        this.pixelsArray = null;
    }

    public BufferedImage getImage(String fileName) throws IOException {
        File file = new File(fileName);
        byte[] buf = new byte[(int) file.length()];
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        bis.read(buf);
        bis.close();
        return decode(buf);
    }

    private static int offset;

    // zakres zmiennej typu byte: od -128 do 127
    private static int byteToInt(byte b) {
        int a = b;
        return (a < 0 ? 256 + a : a);
    }

    private static int read(byte[] buf) {
        return byteToInt(buf[offset++]);
    }

    public BufferedImage decode(byte[] buf) {
        offset = 0;

        // Reading header consisting of 18 bytes
        header = Arrays.copyOfRange(buf, 0, 18);
        footer = Arrays.copyOfRange(buf, buf.length - 26, buf.length);
        for (int i = 0; i < 12; i++)
            read(buf);
        width = read(buf) + (read(buf) << 8); // 12 bajt to szerokosc obrazka
        height = read(buf) + (read(buf) << 8); //
        read(buf);
        read(buf);

        // Reading data
        int n = width * height;
        int[] pixels = new int[n];
        int idx = n - 1;

        Pixel[][] matrixOfPixels = new Pixel[height][width];

        /*
            We write pixels to a two-dimensional array
            Uwaga! Wczytywanie od dolnego lewego wierzcholka
         */
        for (int i = height - 1; i >= 0; i--) {  //rows
            for (int j = 0; j < width; j++) {  //columns
                int b = read(buf);
                int g = read(buf);
                int r = read(buf);
                pixels[idx] = (r << 16) | (g << 8) | b;
                idx--;
                matrixOfPixels[i][j] = new Pixel(r, g, b);

            }
        }

        this.pixelsArray = matrixOfPixels;

        BufferedImage big = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        big.setRGB(0, 0, width, height, pixels, 0, width);
        return big;
    }

    public byte[] getTGABytes(byte[] content) {
        int size = header.length + content.length + footer.length;
        byte[] result = new byte[size];
        int i = 0;
        for (int k = 0; k < header.length; k++, i++) {
            result[i] = header[k];
        }
        for (int k = 0; k < content.length; k++, i++) {
            result[i] = content[k];
        }
        for (int k = 0; k < footer.length; k++, i++) {
            result[i] = footer[k];
        }
        return result;
    }

    public byte[] getBytesFromBitmap(Pixel[][] bitmap) {
        byte[] bytes = new byte[width * height * 3];
        for (int i = height-1, k = 0; i >= 0; i--) {
            for (int j = 0; j < width; j++) {
                Pixel pixel = bitmap[i][j];
                bytes[k * 3 + 2] = ((byte) pixel.red);
                bytes[k * 3 + 1] = ((byte) pixel.green);
                bytes[k * 3] = ((byte) pixel.blue);
                k++;
            }
        }
        return bytes;
    }
}