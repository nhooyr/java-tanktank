package tank;

class Physics {
    protected static double DisplaceX(double v, double theta) {
        return Math.cos(theta) * v;
    }

    protected static double DisplaceY(double v, double theta) {
        return Math.sin(theta) * v;
    }
}
