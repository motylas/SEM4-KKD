public class Pixel {
    int red;
    int green;
    int blue;

    public Pixel(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Pixel() {
        this.red = 0;
        this.green = 0;
        this.blue = 0;
    }

    @Override
    public String toString() {
        return "{" + red + ", " + green + ", " + blue + "}";
    }
}