import java.util.Random;

public class Pixel {
    Random rand = new Random();
    int red;
    int green;
    int blue;

    double redD;
    double greenD;
    double blueD;

    public Pixel(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        redD = red;
        greenD = green;
        blueD = blue;
    }

    public Pixel(int red, int green, int blue, double redD, double greenD, double blueD) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.redD = redD;
        this.greenD = greenD;
        this.blueD = blueD;
    }

    public Pixel() {
        red = 0;
        green = 0;
        blue = 0;
        redD = 0;
        greenD = 0;
        blueD = 0;
    }

    public void plusD(Pixel p1) {
        redD += p1.redD;
        greenD += p1.greenD;
        blueD += p1.blueD;
    }

    public void divD(int num) {
        redD /= num;
        greenD /= num;
        blueD /= num;
    }

    // metryka taksowkowa
    public double getDistanceD(Pixel other) {
        return Math.abs(redD - other.redD) + Math.abs(greenD - other.greenD) + Math.abs(blueD - other.blueD);
    }

    public Pixel newPixelD(double eps) {
        double[] randValue = {-5.0, 0, 5.0};
        int randR = rand.nextInt(3);
        int randB = rand.nextInt(3);
        int randG = rand.nextInt(3);
        double val = eps + 1;

        return new Pixel(red, blue, green, redD * val + randR, greenD * val + randG, blueD * val + randB);
    }

    public void castDouble(){
        red = (int) redD;
        green = (int) greenD;
        blue = (int) blueD;
    }

    @Override
    public String toString() {
        return "{" + red + ", " + green + ", " + blue + "}";
    }
}