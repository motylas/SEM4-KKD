import java.util.ArrayList;

public class Decode {
    static ArrayList<Integer> redIndexesY = new ArrayList<>();
    static ArrayList<Integer> greenIndexesY = new ArrayList<>();
    static ArrayList<Integer> blueIndexesY = new ArrayList<>();
    static ArrayList<Integer> redIndexesZ = new ArrayList<>();
    static ArrayList<Integer> greenIndexesZ = new ArrayList<>();
    static ArrayList<Integer> blueIndexesZ = new ArrayList<>();
    static ArrayList<Double> codebookRY;
    static ArrayList<Double> codebookGY;
    static ArrayList<Double> codebookBY;
    static ArrayList<Double> codebookRZ;
    static ArrayList<Double> codebookGZ;
    static ArrayList<Double> codebookBZ;
    static int k;
    static int height;
    static int width;

    static void setData(ArrayList<ArrayList<Integer>> indexes, int color, ArrayList<ArrayList<Double>> codebooks, int h, int w) {
        redIndexesY = indexes.get(0);
        greenIndexesY = indexes.get(1);
        blueIndexesY = indexes.get(2);
        redIndexesZ = indexes.get(3);
        greenIndexesZ = indexes.get(4);
        blueIndexesZ = indexes.get(5);

        codebookRY = codebooks.get(0);
        codebookGY = codebooks.get(1);
        codebookBY = codebooks.get(2);
        codebookRZ = codebooks.get(3);
        codebookGZ = codebooks.get(4);
        codebookBZ = codebooks.get(5);

        k = color;
        height = h;
        width = w;
    }

    static Pixel[][] getPixels() {
        ArrayList<Double> lowR = new ArrayList<>();
        ArrayList<Double> lowG = new ArrayList<>();
        ArrayList<Double> lowB = new ArrayList<>();
        ArrayList<Double> highR = new ArrayList<>();
        ArrayList<Double> highG = new ArrayList<>();
        ArrayList<Double> highB = new ArrayList<>();

        double redRSeqY = 0;
        double greenRSeqY = 0;
        double blueRSeqY = 0;
        for (int i = 0; i < redIndexesY.size(); i++) {
            // Y
            redRSeqY += codebookRY.get(redIndexesY.get(i));
            greenRSeqY += codebookGY.get(greenIndexesY.get(i));
            blueRSeqY += codebookBY.get(blueIndexesY.get(i));

            lowR.add(redRSeqY);
            lowG.add(greenRSeqY);
            lowB.add(blueRSeqY);

            // Z
            highR.add(codebookRZ.get(redIndexesZ.get(i)));
            highG.add(codebookGZ.get(greenIndexesZ.get(i)));
            highB.add(codebookBZ.get(blueIndexesZ.get(i)));
        }

        // Make Pixels
        Pixel[] pixels = new Pixel[height * width];
        int index = 0;
        for (int i = 0; i < lowR.size(); i++) {
            int red1 = (int) Math.round(lowR.get(i) + highR.get(i));
//            red1 = red1 % 256;
            if (red1 < 0) red1 = 0;
            if (red1 > 255) red1 = 255;
            int green1 = (int) Math.round(lowG.get(i) + highG.get(i));
            if (green1 < 0) green1 = 0;
            if (green1 > 255) green1 = 255;
//            green1 = green1 % 256;
            int blue1 = (int) Math.round(lowB.get(i) + highB.get(i));
            if (blue1 < 0) blue1 = 0;
            if (blue1 > 255) blue1 = 255;
//            blue1 = blue1 % 256;
            int red2 = (int) Math.round(lowR.get(i) - highR.get(i));
            if (red2 < 0) red2 = 0;
            if (red2 > 255) red2 = 255;
//            red2 = red2 % 256;
            int green2 = (int) Math.round(lowG.get(i) - highG.get(i));
            if (green2 < 0) green2 = 0;
            if (green2 > 255) green2 = 255;
//            green2 = green2 % 256;
            int blue2 = (int) Math.round(lowB.get(i) - highB.get(i));
            if (blue2 < 0) blue2 = 0;
            if (blue2 > 255) blue2 = 255;
//            blue2 = blue2 % 256;

            pixels[index] = new Pixel(red1, green1, blue1);
            pixels[index + 1] = new Pixel(red2, green2, blue2);
            index += 2;
        }

        int k = 0;
        Pixel[][] finalPixels = new Pixel[height][width];
        for(int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                finalPixels[i][j] = pixels[k];
                k++;
            }
        }
        return finalPixels;
    }
}
