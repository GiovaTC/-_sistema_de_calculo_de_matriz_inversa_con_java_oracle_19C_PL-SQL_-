import java.sql.*;
import java.util.Scanner;

public class MatrizInversaSP {

    // configuracion oracle .
    static final String URL = "jdbc:oracle:thin:@//localhost:1521/orcl";
    static final String USER = "system";
    static final String PASS = "Tapiero123";

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.print("ingrese tamaño de la matriz ( n * n ): ");
        int n = sc.nextInt();

        double[][] matriz = new double[n][n];

        System.out.println("Ingrese los valores de la matriz:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matriz[i][j] = sc.nextDouble();
            }
        }
        double[][] inversa = calcularInversa(matriz);
        if (inversa == null) {
            System.out.println("❌ La matriz no tiene inversa");
            return;
        }

        System.out.println("\n✅ Matriz Inversa:");
        imprimirMatriz(inversa);

        // 💾 Guardar usando procedimiento
        guardarConSP(n, matriz, inversa);

        sc.close();
    }

    private static double[][] calcularInversa(double[][] matriz) {
        int n = matriz.length;
        double[][] aug = new double[n][2 * n];

        // [A | I]
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                aug[i][j] = matriz[i][j];
            }
            for (int j = n; j < 2 * n; j++) {
                aug[i][j] = (i == (j - n)) ? 1 : 0;
            }
        }

        // eliminacion Gauss-jordan .
        for (int i = 0; i < n; i++) {
            double pivote = aug[i][i];
            if (pivote == 0) return null;

            // normalizar fila
            for (int j = 0; j < 2 * n; j++) {
                aug[i][j] /= pivote;
            }

            // hacer ceros en columna
            for (int k = 0; k < n; k++) {
                if (k != i) {
                    double factor = aug[k][i];
                    for (int j = 0; j < 2 * n; j++) {
                        aug[k][j] -= factor * aug[i][j];
                    }
                }
            }
        }
        double[][] inv = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(aug[i], n, inv[i], 0, n);
        }

        return inv;
        }

        // mostrar matriz .
        public static void imprimirMatriz(double[][] m) {
        for (double[] fila : m) {
            for (double val : fila) {
                System.out.printf("%10.4f " , val);
            }
            System.out.println();
        }
    }

    // serializar matriz a String (para CLOB) .
    public static String matrizToString(double[][] m) {
        StringBuilder sb = new StringBuilder();
        for (double[] fila : m) {
            for (double val : fila) {
                sb.append(String.format("%.4f ", val)).append(",");
            }
            sb.append(";");
        }
        return sb.toString();
    }

    // llamada al procedimiento .
    public static void guardarConSP(int n, double[][] original, double[][] inversa) {

        String call = "{CALL SP_GUARDAR_MATRIZ_INVERSA(?, ?, ?)}";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             CallableStatement cs = conn.prepareCall(call)) {

            cs.setInt(1, n);
            cs.setString(2, matrizToString(original));
            cs.setString(3, matrizToString(inversa));

            cs.execute();

            System.out.println("\n💾 Guardado mediante procedimiento almacenado");

         } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

/* 🧪 3. Ejecución Esperada
📥 Entrada
3
1 0 2
0 1 0
3 0 1

📤 Salida
✅ Matriz Inversa:
   -0.2000    0.0000    0.4000
    0.0000    1.0000    0.0000
    0.6000    0.0000   -0.2000 */   