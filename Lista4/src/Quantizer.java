import java.util.*;

public class Quantizer {
    private final TGAAnalyzer tga;
    private final Pixel[] codebook;
    private final Pixel[][] bitmap;

    public Quantizer(byte[] tgaImage, int colors) {
        tga = new TGAAnalyzer(tgaImage);
        codebook = generateCodeBook(colors);
        bitmap = new Pixel[tga.height][tga.width];
    }

    public byte[] encode() {
        for (int i = 0; i < tga.height; i++) {
            for (int j = 0; j < tga.width; j++) {
                Double[] diffs = new Double[codebook.length];
                for (int k = 0; k < codebook.length; k++) {
                    diffs[k] = euclidSquared(getPixelAsDoubleArray(tga.getBitmap()[i][j]), getPixelAsDoubleArray(codebook[k]));
                }
                bitmap[i][j] = codebook[Arrays.asList(diffs).indexOf(Collections.min(Arrays.asList(diffs)))];
            }
        }
        return tga.getTGABytes(tga.getBytesFromBitmap(bitmap));
    }

    public double mse() {
        double sum = 0;
        for (int i = 0; i < tga.height; i++) {
            for (int j = 0; j < tga.width; j++) {
                sum += euclidSquared(getPixelAsDoubleArray(tga.getBitmap()[i][j]), getPixelAsDoubleArray(bitmap[i][j]));
            }
        }
        return sum / (tga.width * tga.height);
    }

    public double snr(double MSE) {
        double sum = 0;
        for (Pixel[] row :
                tga.getBitmap()) {
            for (Pixel pixel :
                    row) {
                sum += Math.pow(pixel.red, 2) + Math.pow(pixel.green, 2) + Math.pow(pixel.blue, 2);
            }
        }
        return (sum * (1.0 / (tga.width * tga.height))) / MSE;
    }

    private Pixel[] generateCodeBook(int codebookSize) {
        double epsilon = 0.00001;
        ArrayList<Double[]> codebook = new ArrayList<>();
        ArrayList<Double[]> data = castBitmapToVectors(tga.getBitmap());
        Double[] c0 = averageVectorOfVectors(data);
        codebook.add(c0);

        double averageDistortion = averageDistortion(c0, data, data.size());

        while (codebook.size() < codebookSize) {
            Pair<ArrayList<Double[]>, Double> result = splitCodebook(data, codebook, epsilon, averageDistortion);
            codebook = result.first;
            averageDistortion = result.second;
        }
        return castCodebook(codebook);
    }

    private ArrayList<Double[]> castBitmapToVectors(Pixel[][] bitmap) {
        ArrayList<Double[]> vectors = new ArrayList<>();
        for (int i = 0; i < tga.height; i++) {
            for (int j = 0; j < tga.width; j++) {
                Pixel pixel = bitmap[i][j];
                vectors.add(new Double[]{((double) pixel.red), ((double) pixel.green), ((double) pixel.blue)});
            }
        }
        return vectors;
    }

    private Pixel[] castCodebook(ArrayList<Double[]> vectors) {
        Pixel[] codebook = new Pixel[vectors.size()];
        for (int i = 0; i < vectors.size(); i++) {
            codebook[i] = new Pixel(vectors.get(i)[0].intValue(), vectors.get(i)[1].intValue(), vectors.get(i)[2].intValue());
        }
        return codebook;
    }

    private Double[] averageVectorOfVectors(ArrayList<Double[]> vectors) {
        int size = vectors.size();
        Double[] averageVector = new Double[]{0.0, 0.0, 0.0};
        for (Double[] vector : vectors) {
            for (int i = 0; i < 3; i++) {
                averageVector[i] += vector[i] / size;
            }
        }
        return averageVector;
    }

    private double averageDistortion(Double[] vector0, ArrayList<Double[]> vectors, int size) {
        ArrayList<Double> vectorsEuclid = new ArrayList<>();
        vectorsEuclid.add(0.0);
        for (Double[] vector :
                vectors) {
            vectorsEuclid.add(euclidSquared(vector0, vector));
        }
        return vectorsEuclid.stream().reduce((s, d) -> s + d / size).get();
    }

    private double averageDistortion(ArrayList<Double[]> vectorsA, ArrayList<Double[]> vectorsB, int size) {
        ArrayList<Double> vectorsEuclid = new ArrayList<>();
        vectorsEuclid.add(0.0);
        for (int i = 0; i < vectorsA.size(); i++) {
            vectorsEuclid.add(euclidSquared(vectorsA.get(i), vectorsB.get(i)));
        }
        return vectorsEuclid.stream().reduce((s, d) -> s + d / size).get();
    }


    private double euclidSquared(Double[] a, Double[] b) {
        return Math.abs(a[0] - b[0]) +  Math.abs(a[1] - b[1]) + Math.abs(a[2] - b[2]);
//        double sum = 0;
//        for (int i = 0; i < a.length; i++) {
//            sum += Math.pow((a[i] - b[i]), 2);
//        }
//        return sum;
    }


    private Pair<ArrayList<Double[]>, Double> splitCodebook(ArrayList<Double[]> data, ArrayList<Double[]> codebook, double epsilon, double initialAvgDist) {
        int dataSize = data.size();
        ArrayList<Double[]> newCodebook = new ArrayList<>();
        for (Double[] c : codebook) {
            newCodebook.add(newVector(c, epsilon));
            newCodebook.add(newVector(c, -epsilon));
        }
        codebook = newCodebook;

        double averageDistortion = 0.0;
        double error = epsilon + 1;
        while (error > epsilon) {
            ArrayList<Double[]> closest = new ArrayList<>(dataSize);
            for (int i = 0; i < dataSize; i++) {
                closest.add(null);
            }
            HashMap<Integer, ArrayList<Double[]>> nearestVectors = new HashMap<>();
            HashMap<Integer, ArrayList<Integer>> nearestVectorsIndexes = new HashMap<>();
            for (int i = 0; i < data.size(); i++) {
                double minDist = -1;
                int closestIndex = -1;
                for (int j = 0; j < codebook.size(); j++) {
                    double d = euclidSquared(data.get(i), codebook.get(j));
                    if (j == 0 || d < minDist) {
                        minDist = d;
                        closest.set(i, codebook.get(j));
                        closestIndex = j;
                    }
                }
                nearestVectors.putIfAbsent(closestIndex, new ArrayList<>());
                nearestVectorsIndexes.putIfAbsent(closestIndex, new ArrayList<>());
                nearestVectors.get(closestIndex).add(data.get(i));
                nearestVectorsIndexes.get(closestIndex).add(i);
            }

            for (int i = 0; i < codebook.size(); i++) {
                ArrayList<Double[]> nearestVectorsOfI = nearestVectors.get(i);
                if (nearestVectorsOfI.size() > 0) {
                    Double[] averageVector = averageVectorOfVectors(nearestVectorsOfI);
                    codebook.set(i, averageVector);
                    nearestVectorsIndexes.get(i).forEach(integer -> closest.set(integer, averageVector));
                }
            }

            double prevAvgDist = averageDistortion > 0.0 ? averageDistortion : initialAvgDist;
            averageDistortion = averageDistortion(closest, data, dataSize);

            error = (averageDistortion - prevAvgDist) / averageDistortion;
        }
        return new Pair<>(codebook, averageDistortion);
    }

    private Double[] newVector(Double[] vector, double epsilon) {
        Double[] newVector = new Double[3];
        for (int i = 0; i < vector.length; i++) {
            newVector[i] = vector[i] * (1.0 + epsilon);
        }
        return newVector;
    }

    private Double[] getPixelAsDoubleArray(Pixel a) {
        Double[] x = new Double[3];
        x[0] = (double) a.red;
        x[1] = ((double) a.green);
        x[2] = ((double) a.blue);
        return x;
    }

    static class Pair<U, V> {
        U first;
        V second;

        public Pair(U first, V second) {
            this.first = first;
            this.second = second;
        }
    }

}