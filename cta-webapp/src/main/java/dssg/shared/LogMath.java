/*
 * File:                LogMath.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright June 13, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 * 
 */

package dssg.shared;

/**
 * A utility class for doing math with numbers represented as logarithms. Thus,
 * the number x is instead represented as log(x). This can be useful when
 * doing computation involving many products, such as with probabilities.
 * 
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class LogMath
{

    /** The natural logarithm of 0 (log(0)), which is negative infinity. */
    public static final double LOG_0 = Double.NEGATIVE_INFINITY;

    /** The natural logarithm of 1 (log(1)), which is 0. */
    public static final double LOG_1 = 0.0;

    /** The natural logarithm of 2 (log(2)). */
    public static final double LOG_2 = Math.log(2.0);

    /** The natural logarithm of e (log(e)), which is 1. */
    public static final double LOG_E = 1.0;

    /** The natural logarithm of 10 (log(10)). */
    public static final double LOG_10 = Math.log(10.0);

    /**
     * Converts a number to its log-domain representation (log(x)). Negative
     * values will result in NaN.
     *
     * @param   x
     *      The number. Should not be negative.
     * @return
     *      The logarithm of x (log(x)).
     */
    public static double toLog(
        final double x)
    {
        return Math.log(x);
    }

    /**
     * Converts a number from log-domain representation (x = exp(logX)).
     *
     * @param   logX
     *      The log-domain representation of the number x (log(x)).
     * @return
     *      The value of x, which is exp(logX) = exp(log(x)).
     */
    public static double fromLog(
        final double logX)
    {
        return Math.exp(logX);
    }
    
  /**
   * Adds two log-domain values. It uses a trick to prevent numerical overflow and underflow.
   * <br>See <a href='http://cran.r-project.org/web/packages/Rmpfr/vignettes/log1mexp-note.pdf'>paper</a> 
   * @param logX The first log-domain value (log(x)). Must be the same basis as logY.
   * @param logY The second log-domain value (log(y)). Must be the same basis as logX.
   * @return The log of x plus y (log(x + y)).
   */
  public static double add(final double logX, final double logY) {
    final double maxVal;
    final double minVal;
    if (logX > logY) {
      maxVal = logX;
      minVal = logY;
    } else if (logY > logX) {
      maxVal = logY;
      minVal = logX;
    } else {
      // Since x == y, we have log(x + y) = log(x * 2) = log(x) + log(2).
      return logX + LOG_2;
    }
    final double z = minVal - maxVal;
    if (z <= 18d) 
      return maxVal + Math.log1p(Math.exp(z));
    else if (z <= 33.3d)
      return maxVal + z + Math.exp(-z);
    else
      return maxVal + z;
  }

  /**
   * Subtracts two log-domain values. It uses a trick to prevent numerical overflow and underflow.
   * <br>See <a href='http://cran.r-project.org/web/packages/Rmpfr/vignettes/log1mexp-note.pdf'>paper</a> 
   * 
   * Math.log(1.0 - Math.exp(logY - logX)) + logX;
   * 
   * @param logX The first log-domain value (log(x)). Must be the same basis as logY.
   * @param logY The second log-domain value (log(y)). Must be the same basis as logX.
   * @return The log of x minus y (log(x - y)).
   */
  public static double subtract(final double logX, final double logY) {
    if (logX > logY) {
      final double z = logY - logX;
      if (0d < z && z <= LOG_2)
        return logX + Math.log(-Math.expm1(z));
      else
        return logX + Math.log1p(-Math.exp(z));
    } else if (logY > logX) {
      // Since y > x, we will have a log of a negative number, which
      // does not exist.
      return Double.NaN;
    } else if (logX == Double.POSITIVE_INFINITY) {
      // Infinity minus infinity is normally a NaN.
      return Double.NaN;
    } else {
      // Since x == y, we have log(x - y) = log(0), which is negative
      // infinity.
      return LOG_0;
    }
  }

    /**
     * Multiplies two log-domain values. It uses the identity:
     *     log(x * y) = log(x) + log(y)
     *
     * @param   logX
     *      The first log-domain value (log(x)).
     *      Must be the same basis as logY.
     * @param   logY
     *      The second log-domain value (log(y)).
     *      Must be the same basis as logX.
     * @return
     *      The log of x divided by y (log(x * y)).
     */
    public static double multiply(
        final double logX,
        final double logY)
    {
        return logX + logY;
    }

    /**
     * Divides two log-domain values. It uses the identity:
     *     log(x / y) = log(x) - log(y)
     *
     * @param   logX
     *      The first log-domain value (log(x)) used in the numerator.
     *      Must be the same basis as logY.
     * @param   logY
     *      The second log-domain value (log(y)) used in the denominator.
     *      Must be the same basis as logX.
     * @return
     *      The log of x times y (log(x / y)).
     */
    public static double divide(
        final double logX,
        final double logY)
    {
        return logX - logY;
    }

    /**
     * Takes the inverse of a log-domain value. Using the same identity as
     * the division one and that log(1) = 0 to be:
     *    log(x^-1) = log(1 / x) = log(1) - log(x) = -log(x)
     *
     * @param   logX
     *      The log-domain value (log(x)) to invert.
     * @return
     *      The log of x^-1 = 1 / x (log(1/x)).
     */
    public static double inverse(
        final double logX)
    {
        return -logX;
    }
    
}
