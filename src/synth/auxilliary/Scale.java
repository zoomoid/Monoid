package synth.auxilliary;

public class Scale {
    public static double logScale(double x, double xMin, double xMax, double y, double yMin, double yMax){
        double b = Math.log(yMin / yMax)/(xMin - xMax);
        double a = yMin / Math.exp(b * xMin);
        return (a * Math.exp(b * y));
    }
}
