class DirectionalRect {
    public static double[][] coordinatesOfRectangeWithDirection(double centerX, double centerY, int width, int height, double direction) {
        // açılı duran bir diktörgenin köşe noktalarının koordinatlarını bulma
        double[][] output = new double[2][4];
    /*
    x1, x2, x3, x4,
    y1, y2, y3, y4
     */
        double semiDiagonalLength = Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)) / 2;
        double alpha = Math.toDegrees(Math.atan((double) height / width));
        output[0][0] = centerX + semiDiagonalLength * Math.cos(Math.toRadians(alpha + direction));
        output[1][0] = centerY + semiDiagonalLength * Math.sin(Math.toRadians(alpha + direction));

        output[0][1] = centerX + semiDiagonalLength * Math.cos(Math.toRadians(180 - alpha + direction));
        output[1][1] = centerY + semiDiagonalLength * Math.sin(Math.toRadians(180 - alpha + direction));

        output[0][2] = centerX + semiDiagonalLength * Math.cos(Math.toRadians(180 + alpha + direction));
        output[1][2] = centerY + semiDiagonalLength * Math.sin(Math.toRadians(180 + alpha + direction));

        output[0][3] = centerX + semiDiagonalLength * Math.cos(Math.toRadians(-alpha + direction));
        output[1][3] = centerY + semiDiagonalLength * Math.sin(Math.toRadians(-alpha + direction));

        return output;
    }

    public static int[][] alignCenter(double[][] matrice) {

        int[][] output = new int[matrice.length][matrice[0].length];
        for (int a = 0; a < matrice.length; a++) {
            for (int b = 0; b < matrice[a].length; b++) {
                output[a][b] = (int) matrice[a][b];
            }
        }

        return output;
    }
}
