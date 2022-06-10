import java.io.IOException;
import java.util.*;

public class Quantifier {
    private final TGAParser tga = new TGAParser();
    private final Random rand = new Random();
    private Pixel[] codebook;
    private Pixel[][] bitmap;
    private Pixel[][] initialBitmap;
    int height;
    int width;
    int colors;

    public TGAParser getTga() {
        return tga;
    }

    public Pixel[][] getInitialBitmap() {
        return initialBitmap;
    }

    public Quantifier(String tgaImage, int colors) {
        try {
            tga.getImage(tgaImage);
            initialBitmap = tga.getPixels();
            height = initialBitmap.length;
            width = initialBitmap[0].length;
            bitmap = new Pixel[height][width];
            this.colors = colors;
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

    public ArrayList<Double> generateCodeBook(ArrayList<Double> sequence) {
        double epsilon = 0.00001;
        ArrayList<Double> codebook = new ArrayList<>();
        codebook.add(averageOfArray(sequence));
        int size = sequence.size();
        double initialAvgDist = averageDistortion(codebook.get(0), sequence);


        while (codebook.size() < colors) {
            ArrayList<Double> newCodebook = new ArrayList<>();
            double[] randValue = {-3.0, 0, 3.0};
            for (Double num : codebook) {
                int rand1 = rand.nextInt(3);
                int rand2 = rand.nextInt(3);
                newCodebook.add(num * (epsilon + 1) + randValue[rand1]);
                newCodebook.add(num * (1 - epsilon) + randValue[rand2]);
            }
            codebook = newCodebook;

            double averageDistortion = 0.0;
            double error = Double.MAX_VALUE;
            while (error > epsilon) {
                ArrayList<Double> closest = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    closest.add(null);
                } // ?
                HashMap<Integer, ArrayList<Double>> nearestValues = new HashMap<>();
                HashMap<Integer, ArrayList<Integer>> nearestValuesIndexes = new HashMap<>();
                for (int i = 0; i < size; i++) {
                    Double currentValue = sequence.get(i);
                    double minDist = Double.MAX_VALUE;
                    int closestIndex = -1;
                    for (int j = 0; j < codebook.size(); j++) {
                        Double codeBookValue = codebook.get(j);
                        double d = getDistance(currentValue, codeBookValue);
                        if (d < minDist) {
                            minDist = d;
                            closest.set(i, codeBookValue);
                            closestIndex = j;
                        }
                    }
                    nearestValues.putIfAbsent(closestIndex, new ArrayList<>());
                    nearestValuesIndexes.putIfAbsent(closestIndex, new ArrayList<>());
                    nearestValues.get(closestIndex).add(currentValue);
                    nearestValuesIndexes.get(closestIndex).add(i);
                }

                for (int i = 0; i < codebook.size(); i++) {
                    ArrayList<Double> nearestValueToCodeBookValueI = nearestValues.get(i);
                    if (nearestValueToCodeBookValueI != null && nearestValueToCodeBookValueI.size() > 0) {
                        Double averageValue = averageOfArray(nearestValueToCodeBookValueI);
                        codebook.set(i, averageValue);
                        nearestValuesIndexes.get(i).forEach(integer -> closest.set(integer, averageValue));
                    }
                }

                double prevAvgDist = averageDistortion > 0.0 ? averageDistortion : initialAvgDist;
                averageDistortion = averageDistortion(closest, sequence);

                error = (averageDistortion - prevAvgDist) / averageDistortion;
            }
            initialAvgDist = averageDistortion;
        }
        return codebook;
    }

    private Double averageOfArray(ArrayList<Double> sequence) {
        int size = sequence.size();
        double average = 0;
        for (Double number : sequence) {
            average += number;
        }
        average /= size;
        return average;
    }

    private double averageDistortion(Double averageNumber, ArrayList<Double> sequence) {
        int size = sequence.size();
        ArrayList<Double> distances = new ArrayList<>();
        distances.add(0.0);
        for (Double number: sequence)
            distances.add(getDistance(number, averageNumber));
        Optional<Double> result = sequence.stream().map(number -> getDistance(number, averageNumber)).reduce((s, d) -> s + d / size);

        if (result.isEmpty()){
            return 0;
        }
        return result.get();
//        double averageDistortion = 0;
//        for (Double distance: distances)
//            averageDistortion += distance;

//        return averageDistortion / size;
    }

    private double averageDistortion(ArrayList<Double> vectorsA, ArrayList<Double> vectorsB) {
        int size = vectorsB.size();
        ArrayList<Double> vectorsEuclid = new ArrayList<>();
        vectorsEuclid.add(0.0);
        for (int i = 0; i < vectorsA.size(); i++) {
            vectorsEuclid.add(getDistance(vectorsA.get(i), vectorsB.get(i)));
        }
        return vectorsEuclid.stream().reduce((s, d) -> s + d / size).get();
    }

    private double getDistance(Double num1, Double num2){
        return Math.abs(num1 - num2);
    }
}