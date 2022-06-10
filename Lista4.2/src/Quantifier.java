import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Quantifier {
    private final TGAParser tga = new TGAParser();
    private Pixel[] codebook;
    private Pixel[][] bitmap;
    private Pixel[][] initialBitmap;
    int height;
    int width;

    public Quantifier(String tgaImage, int colors) {
        try {
            tga.getImage(tgaImage);
            initialBitmap = tga.getPixels();
            height = initialBitmap.length;
            width = initialBitmap[0].length;
            codebook = generateCodeBook(colors);
            bitmap = new Pixel[height][width];
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public byte[] encode() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Double[] diffs = new Double[codebook.length];
                for (int k = 0; k < codebook.length; k++) {
                    diffs[k] = initialBitmap[i][j].getDistanceD(codebook[k]);
                }
                bitmap[i][j] = codebook[Arrays.asList(diffs).indexOf(Collections.min(Arrays.asList(diffs)))];
            }
        }
        return tga.getTGABytes(tga.getBytesFromBitmap(bitmap));
    }

    public double mse() {
        double sum = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                sum += initialBitmap[i][j].getDistanceD(bitmap[i][j]);
            }
        }
        return sum / (width * height);
    }

    public double snr(double MSE) {
        double sum = 0;
        for (Pixel[] row : initialBitmap) {
            for (Pixel pixel : row) {
                sum += Math.pow(pixel.red, 2) + Math.pow(pixel.green, 2) + Math.pow(pixel.blue, 2);
            }
        }
        return (sum * (1.0 / (width * height))) / MSE;
    }

    private Pixel[] generateCodeBook(int codebookSize) {
        double epsilon = 0.00001;
        ArrayList<Pixel> codebook = new ArrayList<>();
        ArrayList<Pixel> allPixels = getListOfPixels(initialBitmap);
        codebook.add(averageOfPixels(allPixels));
        int size = allPixels.size();
        double initialAvgDist = averageDistortion(codebook.get(0), allPixels);


        while (codebook.size() < codebookSize) {
            ArrayList<Pixel> newCodebook = new ArrayList<>();
            for (Pixel p : codebook) {
                newCodebook.add(p.newPixelD(epsilon));
                newCodebook.add(p.newPixelD(-epsilon));
            }
            codebook = newCodebook;

            double averageDistortion = 0.0;
            double error = Double.MAX_VALUE;
            while (error > epsilon) {
                ArrayList<Pixel> closest = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    closest.add(null);
                } // ?
                HashMap<Integer, ArrayList<Pixel>> nearestPixels = new HashMap<>();
                HashMap<Integer, ArrayList<Integer>> nearestPixelsIndexes = new HashMap<>();
                for (int i = 0; i < size; i++) {
                    Pixel currentPixel = allPixels.get(i);
                    double minDist = Double.MAX_VALUE;
                    int closestIndex = -1;
                    for (int j = 0; j < codebook.size(); j++) {
                        Pixel codeBookPixel = codebook.get(j);
                        double d = currentPixel.getDistanceD(codeBookPixel);
                        if (d < minDist) {
                            minDist = d;
                            closest.set(i, codeBookPixel);
                            closestIndex = j;
                        }
                    }
                    nearestPixels.putIfAbsent(closestIndex, new ArrayList<>());
                    nearestPixelsIndexes.putIfAbsent(closestIndex, new ArrayList<>());
                    nearestPixels.get(closestIndex).add(currentPixel);
                    nearestPixelsIndexes.get(closestIndex).add(i);
                }

                for (int i = 0; i < codebook.size(); i++) {
                    ArrayList<Pixel> nearestPixelsToCodeBookPixelI = nearestPixels.get(i);
                    if (nearestPixelsToCodeBookPixelI != null && nearestPixelsToCodeBookPixelI.size() > 0) {
                        Pixel averagePixel = averageOfPixels(nearestPixelsToCodeBookPixelI);
                        codebook.set(i, averagePixel);
                        nearestPixelsIndexes.get(i).forEach(integer -> closest.set(integer, averagePixel));
                    }
                }

                double prevAvgDist = averageDistortion > 0.0 ? averageDistortion : initialAvgDist;
                averageDistortion = averageDistortion(closest, allPixels);

                error = (averageDistortion - prevAvgDist) / averageDistortion;
            }
            initialAvgDist = averageDistortion;
        }
        return castCodebook(codebook);
    }

    private ArrayList<Pixel> getListOfPixels(Pixel[][] bitmap) {
        ArrayList<Pixel> allPixels = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                allPixels.add(bitmap[i][j]);
            }
        }
        return allPixels;
    }

    private Pixel[] castCodebook(ArrayList<Pixel> vectors) {
        Pixel[] codebook = new Pixel[vectors.size()];
        for (int i = 0; i < vectors.size(); i++) {
            codebook[i] = vectors.get(i);
            codebook[i].castDouble();
        }
        return codebook;
    }

    private Pixel averageOfPixels(ArrayList<Pixel> pixels) {
        int size = pixels.size();
        Pixel averagePixel = new Pixel();
        for (Pixel pixel : pixels) {
                averagePixel.plusD(pixel);
        }
        averagePixel.divD(size);
        return averagePixel;
    }

    private double averageDistortion(Pixel averagePixel, ArrayList<Pixel> allPixels) {
        int size = allPixels.size();
        ArrayList<Double> distances = new ArrayList<>();
        distances.add(0.0);
        for (Pixel pixel : allPixels)
            distances.add(pixel.getDistanceD(averagePixel));

        double averageDistortion = 0;
        for (Double distance: distances)
            averageDistortion += distance;

        return averageDistortion / size;
    }

    private double averageDistortion(ArrayList<Pixel> vectorsA, ArrayList<Pixel> vectorsB) {
        int size = vectorsB.size();
        ArrayList<Double> vectorsEuclid = new ArrayList<>();
        vectorsEuclid.add(0.0);
        for (int i = 0; i < vectorsA.size(); i++) {
            vectorsEuclid.add(vectorsA.get(i).getDistanceD(vectorsB.get(i)));
        }
        return vectorsEuclid.stream().reduce((s, d) -> s + d / size).get();
    }
}