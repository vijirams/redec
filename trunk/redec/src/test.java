import processing.core.PApplet;

public class test extends PApplet {

    public float MAX = 10;

    public void setup() {
        for (float a = 0; a < MAX; a++) {
            for (float b = 0; b < MAX; b++) {
                System.out.printf("%f-%f=%f\n", a, b, difference(a, b, MAX));
            }
        }
        exit();
    }

    /**
     * Returns the Absolute Difference between two angles. Useful for computing
     * the difference between two hues. In the HSB colorspace, one of the
     * coordinates is an angle, thus standard arithmetic fails. The difference
     * between 359° and 0° isn't 359 as normal arithmetic would indicate, but 1.
     * This method fixes that. <code>max</code> should be set to the number of
     * discrete values*smallest value or the value that is overlapping with 0.
     * ex: For a system where 0==360, you would set max to 360.
     * 
     * @param a
     *            first angle
     * @param b
     *            second angle
     * @param max
     *            overlap value, see above.
     * @return the difference of the angles (shortest angular difference)
     */
    private float difference(float a, float b, float max) {
        float result = 0f;
        if (a > b)
            result = max - ((a + max - b) % max);
        else if (b > a)
            result = max - ((b + max - a) % max);
        if (result > max / 2)
            result = (max) - result;
        return (result);
    }
}
