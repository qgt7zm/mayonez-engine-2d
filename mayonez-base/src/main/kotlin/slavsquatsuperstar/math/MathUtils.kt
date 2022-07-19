package slavsquatsuperstar.math

import kotlin.math.*

/**
 * A library of common math operations designed for the float data type and precision.
 *
 * @author SlavSquatSuperstar
 */
object MathUtils {

    /**
     * The maximum precision a float should have (equal to 10^-6).
     */
    const val EPSILON = 1e-6f

    /**
     * The number π (pi) in float precision.
     */
    const val PI = 3.1415927f

    // Comparison Methods

    /**
     * Determines whether two floats are approximately equal within 6 decimal places.
     *
     * @param num1 a number
     * @param num2 another number
     * @return if they are equal
     */
    @JvmStatic
    fun equals(num1: Float, num2: Float): Boolean = abs(num1 - num2) <= EPSILON

    // Exponents

    /**
     * Squares a value. Shorthand for x * x or x^2.
     *
     * @param value a real number
     * @return the number squared
     */
    fun squared(value: Float): Float = value * value

    /**
     * Takes the principal square root of a number. Intended for Java files as an equivalent for Math.sqrt(double).
     *
     * @param value a non-negative number
     * @return the square root, in float precision.
     */
    @JvmStatic
    fun sqrt(value: Float): Float = sqrt(value.toDouble()).toFloat()

    // Pythagorean Theorem

    /**
     * Calculates the length of the diagonal of an n-dimensional figure with perpendicular edges (rectangle, cuboid, hyper-cuboid, etc.).
     *
     * @param sides the lengths of the figure's sides
     * @return the hypotenuse (pythagorean theorem) in n-dimensions
     */
    @JvmStatic
    fun hypot(vararg sides: Float): Float = sqrt(hypotSq(*sides))

    /**
     * Calculates the length squared of the diagonal of an n-dimensional figure with perpendicular edges (rectangle, cuboid, hyper-cuboid, etc.).
     *
     * @param sides the lengths of the figure's sides
     * @return the hypotenuse (pythagorean theorem) in n-dimensions, squared
     */
    @JvmStatic
    fun hypotSq(vararg sides: Float): Float {
        return sum(*FloatArray(sides.size) { sides[it] * sides[it] })
    }

    @JvmStatic
    fun invHypot(hypot: Float, vararg legs: Float): Float {
        return sqrt(invHypotSq(hypot, *legs))
    }

    @JvmStatic
    fun invHypotSq(hypot: Float, vararg legs: Float): Float {
        return (hypot * hypot) - sum(*FloatArray(legs.size) { legs[it] * legs[it] })
    }

    // Accumulator Methods

    @JvmStatic
    fun avg(vararg values: Float): Float = sum(*values) / values.size

    @JvmStatic
    fun avg(vararg values: Int): Int = sum(*values) / values.size

    @JvmStatic
    fun sum(vararg values: Float): Float {
        var sum = 0f
        for (v in values) sum += v
        return sum
    }

    @JvmStatic
    fun sum(vararg values: Int): Int {
        var sum = 0
        for (v in values) sum += v
        return sum
    }

    // Find Extreme Methods

    @JvmStatic
    fun min(vararg values: Float): Float {
        if (values.isEmpty()) return 0f
        var min = Float.POSITIVE_INFINITY
        for (n in values) if (n < min) min = n
        return min
    }

    @JvmStatic
    fun minIndex(vararg values: Float): Int {
        if (values.isEmpty()) return -1
        var minIndex = 0
        for (i in 1 until values.size)
            if (values[i] < values[minIndex]) minIndex = i
        return minIndex
    }

    @JvmStatic
    fun max(vararg values: Float): Float {
        if (values.isEmpty()) return 0f
        var max = Float.NEGATIVE_INFINITY
        for (n in values) if (n > max) max = n
        return max
    }

    @JvmStatic
    fun maxIndex(vararg values: Float): Int {
        if (values.isEmpty()) return -1
        var maxIndex = 0
        for (i in 1 until values.size)
            if (values[i] > values[maxIndex]) maxIndex = i
        return maxIndex
    }

    // Clamp / Range Methods

    /**
     * Restricts a float's value within a provided range.
     *
     * @param value any real number
     * @param min   the lower bound, inclusive
     * @param max   the upper bound, inclusive
     * @return a number within the bounds
     */
    @JvmStatic
    fun clamp(value: Float, min: Float, max: Float): Float {
        val range = Range(min, max)
        return range.min.coerceAtLeast(value).coerceAtMost(range.max)
    }

    /**
     * Checks whether a number is within a provided range, including the bounds.
     *
     * @param value any real number
     * @param min   the lower bound, inclusive
     * @param max   the upper bound, inclusive
     * @return if the value is within range
     */
    @JvmStatic
    fun inRange(value: Float, min: Float, max: Float): Boolean = value in Range(min, max)

    // Random Number Methods

    /**
     * Generates a random integer between the two provided bounds.
     *
     * @param min the lower bound (inclusive)
     * @param max the maximum bound (inclusive)
     * @return the random float
     */
    @JvmStatic
    fun random(min: Float, max: Float): Float {
        val range = Range(min, max)
        return (Math.random() * (range.max - range.min + Float.MIN_VALUE)).toFloat() + range.min
    }

    /**
     * Generates a random integer between the two provided bounds.
     *
     * @param min the lower bound (inclusive)
     * @param max the maximum bound (inclusive)
     * @return The random integer.
     */
    @JvmStatic
    fun random(min: Int, max: Int): Int {
        val range = Range(min.toFloat(), max.toFloat())
        return (Math.random() * (range.max - range.min + 1) + range.min).toInt()
    }

    /**
     * Generates a random event with a certain percent change of succeeding.
     * @param percent the change of succeeding, from 0-1
     * @return true a percentage of the time, otherwise false
     */
    @JvmStatic
    fun randomPercent(percent: Float): Boolean {
        return Math.random() < percent
    }

    // Rounding Methods

    /**
     * Rounds up the given number according to the specified precision.
     *
     * @param value a decimal number
     * @param decimalPlaces the number of decimal places to round
     *
     * @return the rounded number
     */
    @JvmStatic
    fun round(value: Float, decimalPlaces: Int): Float {
        val temp = value * 10f.pow(decimalPlaces)
        return round(temp) / 10f.pow(decimalPlaces)
    }

    /**
     * Rounds down the given number according to the specified precision.
     *
     * @param value a decimal number
     * @param decimalPlaces the number of decimal places to round
     *
     * @return the truncated number
     */
    @JvmStatic
    fun truncate(value: Float, decimalPlaces: Int): Float {
        val temp = value * 10f.pow(decimalPlaces)
        return truncate(temp) / 10f.pow(decimalPlaces)
    }

    // Trig / Angle Methods

    // TODO check precision on kotlin sin/cos and toRadians
    /**
     * Takes the sine of an angle.
     *
     * @param degrees the measure of the angle, in degrees
     * @return the sine of the angle
     */
    @JvmStatic
    fun sin(degrees: Float): Float = sin(Math.toRadians(degrees.toDouble())).toFloat()

    /**
     * Takes the cosine of an angle.
     *
     * @param degrees the measure of the angle, in degrees
     * @return the cosine of the angle
     */
    @JvmStatic
    fun cos(degrees: Float): Float = cos(Math.toRadians(degrees.toDouble())).toFloat()

    /**
     * Converts an angle from radians to degrees.
     *
     * @param radians the angle measure in radians
     * @return the angle measure in degrees
     */
    @JvmStatic
    fun toDegrees(radians: Float): Float = Math.toDegrees(radians.toDouble()).toFloat()

    /**
     * Converts an angle from degrees to radians.
     *
     * @param degrees the angle measure in degrees
     * @return the angle measure in radians
     */
    @JvmStatic
    fun toRadians(degrees: Float): Float = Math.toRadians(degrees.toDouble()).toFloat()

}