import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Encoding {
    static ArrayList<Double> lowR = new ArrayList<>();
    static ArrayList<Double> lowG = new ArrayList<>();
    static ArrayList<Double> lowB = new ArrayList<>();
    static ArrayList<Double> highR = new ArrayList<>();
    static ArrayList<Double> highG = new ArrayList<>();
    static ArrayList<Double> highB = new ArrayList<>();
    static ArrayList<Double> codebookRY;
    static ArrayList<Double> codebookGY;
    static ArrayList<Double> codebookBY;
    static ArrayList<Double> codebookRZ;
    static ArrayList<Double> codebookGZ;
    static ArrayList<Double> codebookBZ;

    public static void main(String[] args) {
        //if (args.length < 3) return;
//        String input = args[0];
//        String output = args[1];
        String input = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\Lista6\\example0.tga";
        String output = "C:\\Users\\hgrud\\Documents\\GitHub\\SEM4-KKD\\Lista6\\out.tga";
        int colors = 7;
//        try {
//            colors = Integer.parseInt(args[2]);
//            if (colors < 0 || colors > 7) {
//                System.out.println("Wrong number");
//                return;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }
        try {
            Quantifier quantizer = new Quantifier(input, (int) Math.pow(2, colors));
            codeYZ(quantizer);
            ArrayList<ArrayList<Integer>> indexes = quantYZ(quantizer);
            ArrayList<ArrayList<Double>> codebooks = new ArrayList<>();
            codebooks.add(codebookRY);
            codebooks.add(codebookGY);
            codebooks.add(codebookBY);
            codebooks.add(codebookRZ);
            codebooks.add(codebookGZ);
            codebooks.add(codebookBZ);
            Decode.setData(indexes, colors, codebooks, quantizer.height, quantizer.width);
            Pixel[][] pixels = Decode.getPixels();
            TGAParser tga = quantizer.getTga();
            byte[] bytes = tga.getTGABytes(tga.getBytesFromBitmap(pixels));
            Files.write(Paths.get(output), bytes);
        } catch (Exception ioe) {
            ioe.printStackTrace();
            return;
        }
    }

    static void codeYZ(Quantifier quantifier){
        Pixel[][] bitmap = quantifier.getInitialBitmap();

        Pixel[] pixelsSequence = convertBitmap(bitmap);
        int pixelsLength = pixelsSequence.length % 2 == 0 ? pixelsSequence.length : pixelsSequence.length - 1;
        for (int i = 0; i < pixelsLength; i+=2){
            int red1 = pixelsSequence[i].getRed();
            int green1 = pixelsSequence[i].getGreen();
            int blue1 = pixelsSequence[i].getBlue();
            int red2 = pixelsSequence[i+1].getRed();
            int green2 = pixelsSequence[i+1].getGreen();
            int blue2 = pixelsSequence[i+1].getBlue();
            lowR.add((double) (red1 + red2) / 2);
            lowG.add((double) (green1 + green2) / 2);
            lowB.add((double) (blue1 + blue2) / 2);
            highR.add((double) (red1 - red2) / 2);
            highG.add((double) (green1 - green2) / 2);
            highB.add((double) (blue1 - blue2) / 2);
        }

        if (pixelsSequence.length % 2 != 0){
            int red = pixelsSequence[pixelsSequence.length-1].getRed();
            int green = pixelsSequence[pixelsSequence.length-1].getGreen();
            int blue = pixelsSequence[pixelsSequence.length-1].getBlue();

            lowR.add((double) (red) / 2);
            lowG.add((double) (green) / 2);
            lowB.add((double) (blue) / 2);
            highR.add((double) (red) / 2);
            highG.add((double) (green) / 2);
            highB.add((double) (blue) / 2);
        }
    }

    static private ArrayList<ArrayList<Integer>> quantYZ(Quantifier quantifier){
        ArrayList<Integer> redIndexesY = new ArrayList<>();
        ArrayList<Integer> greenIndexesY = new ArrayList<>();
        ArrayList<Integer> blueIndexesY = new ArrayList<>();
        ArrayList<Integer> redIndexesZ = new ArrayList<>();
        ArrayList<Integer> greenIndexesZ = new ArrayList<>();
        ArrayList<Integer> blueIndexesZ = new ArrayList<>();
        codebookRY = quantifier.generateCodeBook(getDiffs(lowR));
        codebookGY = quantifier.generateCodeBook(getDiffs(lowG));
        codebookBY = quantifier.generateCodeBook(getDiffs(lowB));
        codebookRZ = quantifier.generateCodeBook(highR);
        codebookGZ = quantifier.generateCodeBook(highG);
        codebookBZ = quantifier.generateCodeBook(highB);

        // First Number Y
        int redInd = findClosestIndex(codebookRY, lowR.get(0));
        int greenInd = findClosestIndex(codebookGY, lowG.get(0));
        int blueInd = findClosestIndex(codebookBY, lowB.get(0));
        Double redRSeqY = codebookRY.get(redInd);
        Double greenRSeqY = codebookGY.get(greenInd);
        Double blueRSeqY = codebookBY.get(blueInd);
        redIndexesY.add(redInd);
        greenIndexesY.add(greenInd);
        blueIndexesY.add(blueInd);

        // First Number Z
        redInd = findClosestIndex(codebookRZ, highR.get(0));
        greenInd = findClosestIndex(codebookGZ, highG.get(0));
        blueInd = findClosestIndex(codebookBZ, highB.get(0));
        redIndexesZ.add(redInd);
        greenIndexesZ.add(greenInd);
        blueIndexesZ.add(blueInd);


        for (int i = 1; i < lowR.size(); i++){
            //Y
            double redDiff = lowR.get(i) - redRSeqY;
            double greenDiff = lowG.get(i) - greenRSeqY;
            double blueDiff = lowB.get(i) - blueRSeqY;
            redInd = findClosestIndex(codebookRY, redDiff);
            greenInd = findClosestIndex(codebookGY, greenDiff);
            blueInd = findClosestIndex(codebookBY, blueDiff);
            redIndexesY.add(redInd);
            greenIndexesY.add(greenInd);
            blueIndexesY.add(blueInd);
            redRSeqY += codebookRY.get(redInd);
            greenRSeqY += codebookGY.get(greenInd);
            blueRSeqY += codebookBY.get(blueInd);

            //Z
            redInd = findClosestIndex(codebookRZ, highR.get(i));
            greenInd = findClosestIndex(codebookGZ, highG.get(i));
            blueInd = findClosestIndex(codebookBZ, highB.get(i));
            redIndexesZ.add(redInd);
            greenIndexesZ.add(greenInd);
            blueIndexesZ.add(blueInd);
        }
        ArrayList<ArrayList<Integer>> indexes = new ArrayList<>();
        indexes.add(redIndexesY);
        indexes.add(greenIndexesY);
        indexes.add(blueIndexesY);
        indexes.add(redIndexesZ);
        indexes.add(greenIndexesZ);
        indexes.add(blueIndexesZ);
        return indexes;
    }

    static private ArrayList<Double> getDiffs(ArrayList<Double> sequence){
        ArrayList<Double> diffs = new ArrayList<>();
        diffs.add(sequence.get(0));
        double last = sequence.get(0);
        double current;
        for (int i = 1; i < sequence.size(); i++){
            current = sequence.get(0);
            diffs.add(current - last);
            last = current;
        }
        return diffs;
    }


    static Pixel[] convertBitmap(Pixel[][] bitmap){
        int height = bitmap.length;
        int width = bitmap[0].length;
        Pixel[] pixelArray = new Pixel[width*height];
        int k =0;
        for (Pixel[] pixels : bitmap) {
            for (int j = 0; j < width; j++) {
                pixelArray[k] = pixels[j];
                k++;
            }
        }
        return pixelArray;
    }

    static private double getDistance(Double num1, Double num2){
        return Math.abs(num1 - num2);
    }


    static private int findClosestIndex(ArrayList<Double> codebook, Double value){
        double minDistance = Double.MAX_VALUE;
        int index = -1;
        double dist;
        for (int i = 0; i < codebook.size(); i++){
            if ((dist = getDistance(codebook.get(i), value)) < minDistance){
                minDistance = dist;
                index = i;
            }
        }
        return index;
    }
}
