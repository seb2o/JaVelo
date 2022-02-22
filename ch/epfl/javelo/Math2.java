package ch.epfl.javelo;

public final class Math2 {
    private Math2() {

    }
    public static int ceilDiv(int x, int y){
        Preconditions.checkArgument (x >= 0 && y> 0);
        return (x+y-1)/y;
    }

    public static double interpolate(double y0, double y1, double x){
        double a = y1 - y0;
        return Math.fma(a,x, y0);
    }

    public static int clamp(int min, int v, int max){
        Preconditions.checkArgument(min <= max);
        if(v < min){
            return min;
        }
        if(v > max){
            return max;
        }
        return v;
    }

    public static double clamp(double min, double v, double max){
        Preconditions.checkArgument(min <= max);
        if(v < min){
            return min;
        }
        if(v > max){
            return max;
        }
        return v;
    }

    public static double asinh(double x){
        return Math.log(x + Math.sqrt(1+Math.pow(x,2)));
    }

    public static double dotProduct(double uX, double uY, double vX, double vY){
        return Math.fma(uX,vX,uY*vY);
    }

    public static double squaredNorm(double uX, double uY){
        return dotProduct(uX,uY,uX,uY);
    }

    public static double norm(double uX, double uY){
        return Math.sqrt(squaredNorm(uX,uY));
    }

    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY){
        double uX = pX-aX;
        double uY = pY-aY;
        double vX = bX-aX;
        double vY = bY-aY;
        return dotProduct(uX,uY,vX,vY)/norm(vX,vY);
    }

}
