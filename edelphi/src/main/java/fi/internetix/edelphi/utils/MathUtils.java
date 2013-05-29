package fi.internetix.edelphi.utils;

public class MathUtils {

  public static double getGCD(double a, double b) {
    // Thanks for this snippet goes to to Nayuki Minase (http://nayuki.eigenstate.org/res/calculate-gcd-javascript.js)

    while (b != 0) {
      double z = a % b;
      a = b;
      b = z;
    }

    return a;
  }

}
