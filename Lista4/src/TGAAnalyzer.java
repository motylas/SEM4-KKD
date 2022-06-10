import java.util.Arrays;

public class TGAAnalyzer {
    private final byte[] header;
    private final byte[] footer;
    protected int width;
    protected int height;
    private Pixel[][] bitmap;

    public TGAAnalyzer(byte[] tgaImage) {
        header = Arrays.copyOfRange(tgaImage, 0, 18);
        footer = Arrays.copyOfRange(tgaImage, tgaImage.length - 26, tgaImage.length);

        width = tgaImage[13] * 256 + (tgaImage[12] & 0xFF);
        height = tgaImage[15] * 256 + (tgaImage[14] & 0xFF);
        parseBitmap(Arrays.copyOfRange(tgaImage, 18, tgaImage.length - 26), width, height);

    }

    private void parseBitmap(byte[] bitmapBytes, int width, int height) {
        bitmap = new Pixel[height][width];
        for (int i = 0, k = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                bitmap[i][j] = new Pixel(bitmapBytes[k * 3 + 2] & 0xff, bitmapBytes[k * 3 + 1] & 0xff, bitmapBytes[k * 3] & 0xff);
                k++;
            }
        }
    }

    public Pixel[][] getBitmap() {
        return bitmap;
    }

    public byte[] getBytesFromBitmap(Pixel[][] bitmap) {
        byte[] bytes = new byte[width * height * 3];
        for (int i = 0, k = 0; i < height; i++) {
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
}