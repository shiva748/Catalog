import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    
    static class Point {
        BigInteger x;
        BigInteger y;
        
        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }
    
    public static void main(String[] args) throws Exception {
        String json = Files.readString(Path.of(args[0]));
        
        int kStart = json.indexOf("\"k\"");
        kStart = json.indexOf(":", kStart) + 1;
        int kEnd = json.indexOf(",", kStart);
        int braceEnd = json.indexOf("}", kStart);
        if (kEnd == -1 || (braceEnd != -1 && braceEnd < kEnd)) {
            kEnd = braceEnd;
        }
        String kStr = json.substring(kStart, kEnd).trim();
        int k = Integer.parseInt(kStr);

        System.out.println("k = " + k);

        List<Point> points = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            try {
                String searchKey = "\"" + i + "\"";
                int keyPos = json.indexOf(searchKey + ":");
                if (keyPos == -1) continue;

                int baseStart = json.indexOf("\"base\"", keyPos);
                baseStart = json.indexOf(":", baseStart) + 1;
                baseStart = json.indexOf("\"", baseStart) + 1;
                int baseEnd = json.indexOf("\"", baseStart);
                String baseStr = json.substring(baseStart, baseEnd).trim();
                int base = Integer.parseInt(baseStr);

                int valueStart = json.indexOf("\"value\"", keyPos);
                valueStart = json.indexOf(":", valueStart) + 1;
                valueStart = json.indexOf("\"", valueStart) + 1;
                int valueEnd = json.indexOf("\"", valueStart);
                String value = json.substring(valueStart, valueEnd).trim();

                System.out.println("Point " + i + ": base=" + base + ", value=" + value);

                BigInteger x = BigInteger.valueOf(i);
                BigInteger y = new BigInteger(value, base);

                points.add(new Point(x, y));
            } catch (Exception e) {
                System.err.println("Error parsing point " + i + ": " + e.getMessage());
            }
        }

        System.out.println("\nTotal points found: " + points.size());

        if (points.size() < k) {
            System.err.println("Not enough points! Need " + k + ", have " + points.size());
            return;
        }

        List<Point> selectedPoints = points.subList(0, k);

        BigInteger secret = lagrangeInterpolation(selectedPoints);

        System.out.println("\nSecret (constant term): " + secret);
    }
    
    static BigInteger lagrangeInterpolation(List<Point> points) {
        BigInteger result = BigInteger.ZERO;
        int k = points.size();
        
        for (int i = 0; i < k; i++) {
            BigInteger xi = points.get(i).x;
            BigInteger yi = points.get(i).y;
            
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;
            
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigInteger xj = points.get(j).x;
                    
                    numerator = numerator.multiply(xj.negate());
                    
                    denominator = denominator.multiply(xi.subtract(xj));
                }
            }
            BigInteger term = yi.multiply(numerator).divide(denominator);
            result = result.add(term);
        }
        
        return result;
    }
}
