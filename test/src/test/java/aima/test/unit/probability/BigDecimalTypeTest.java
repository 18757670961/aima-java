package aima.test.unit.probability;

import aima.extra.probability.*;
import static org.junit.Assert.*;
import java.math.*;

import org.junit.Test;

public class BigDecimalTypeTest {

	/**
	 * ProbabilityTest to check various functions of the BigDecimalProbabilityNumber class
	 */
	@Test
	public void testBigNumber() {

		double DEFAULT_ROUNDING_THRESHOLD = 1e-8;

		// Constructors with double parameter type

		// Throws IllegalArgumentException
		// ProbabilityNumber testValue1 = ProbabilityFactory.decimalValueOf(4.0);
		// ProbabilityNumber testValue2 = ProbabilityFactory.decimalValueOf(-5.1);

		ProbabilityNumber testValue2 = ProbabilityFactory.decimalValueOf(0.15);

		ProbabilityNumber testValue0 = ProbabilityFactory.decimalValueOf(new BigDecimal("0.000"));
		// Check if zero
		assertEquals(testValue0.isZero(), true);

		ProbabilityNumber testValue1 = ProbabilityFactory.decimalValueOf(
				new BigDecimal("1.000000"));
		// Check if one
		assertEquals(testValue1.isOne(), true);

		ProbabilityNumber testValue4 = ProbabilityFactory.decimalValueOf(
				new BigDecimal("0.15000000000000000000000"));
		// Check if two DoubleProbabilityNumber values are equal or not
		assertEquals(testValue2.equals(testValue4), true);

		ProbabilityNumber testValue5 = ProbabilityFactory.decimalValueOf(0.1);
		ProbabilityNumber testValue6 = ProbabilityFactory.decimalValueOf(0.8);
		
		BigDecimal v2 = BigDecimal.valueOf(0.15);
		BigDecimal v5 = BigDecimal.valueOf(0.1);
		BigDecimal v6 = BigDecimal.valueOf(0.8);
		
		// Add DoubleProbabilityNumber values
		assertEquals(v2.add(v5).doubleValue(), testValue2.add(testValue5).getValue().doubleValue(), DEFAULT_ROUNDING_THRESHOLD);
		assertEquals(v6.add(v2).doubleValue(), testValue6.add(testValue2).getValue().doubleValue(), DEFAULT_ROUNDING_THRESHOLD);

		// Subtract DoubleProbabilityNumber values
		assertEquals(v2.subtract(v5).doubleValue(), testValue2.subtract(testValue5).getValue().doubleValue(), DEFAULT_ROUNDING_THRESHOLD);
		assertEquals(v6.subtract(v2).doubleValue(), testValue6.subtract(testValue2).getValue().doubleValue(), DEFAULT_ROUNDING_THRESHOLD);

		// Multiply DoubleProbabilityNumber values
		assertEquals(v2.multiply(v5).doubleValue(), testValue2.multiply(testValue5).getValue().doubleValue(), DEFAULT_ROUNDING_THRESHOLD);
		assertEquals(v6.multiply(v2).doubleValue(), testValue6.multiply(testValue2).getValue().doubleValue(), DEFAULT_ROUNDING_THRESHOLD);

		// Divide DoubleProbabilityNumber values
		assertEquals(0.19, testValue2.divide(testValue6).getValue().doubleValue(), DEFAULT_ROUNDING_THRESHOLD);

		/*
		// Unlimited precision (non terminating decimal value)
		ProbabilityNumber t1 = ProbabilityFactory.decimalValueOf(new BigDecimal("0.1"), 0);
		ProbabilityNumber t2 = ProbabilityFactory.decimalValueOf(new BigDecimal("0.3"), 0);
		t1.overrideComputationPrecisionGlobally(MathContext.UNLIMITED);
		assertEquals(0.1 / 0.3, t1.divide(t2).getValue().doubleValue(), DEFAULT_ROUNDING_THRESHOLD);
		*/
		
		/* 
		// BigDecimal instances can be initialized with values using different constructors. 
		// The MathContext objects that may be explicitly specified during constructor call
		// are treated by the BigDecimal class in different ways. 
		BigDecimal n = new BigDecimal("1.3000000000", new MathContext(5, RoundingMode.HALF_EVEN));
		System.out.println(n.precision());
		*/
		
		// Check for computations with different precision values
		ProbabilityNumber t1 = ProbabilityFactory.decimalValueOf(new BigDecimal(0.1), 3);
		ProbabilityNumber t2 = ProbabilityFactory.decimalValueOf(new BigDecimal(0.3), 5);
		assertEquals(0.3333, t1.divide(t2).getValue().doubleValue(), DEFAULT_ROUNDING_THRESHOLD);
		
		// Raise DoubleProbabilityNumber values to powers (check for boundary
		// conditions
		// (positive infinity, negative infinity))
		assertEquals(0.15 * 0.15, testValue2.pow(2).getValue().doubleValue(), DEFAULT_ROUNDING_THRESHOLD);
		assertEquals(0.51, testValue6.pow(3).getValue().doubleValue(), DEFAULT_ROUNDING_THRESHOLD);

		// System.out.println(testValue2.pow(BigInteger.valueOf(20)).getValue());

		/**
		 * Computation to test number representation precision
		 */
		// Consider two numbers of type double that are very close to each
		// other.
		double a = 0.005;
		double b = 0.0049;
		// Note the slight precision loss
		// System.out.println("double representation -> " + (a - b));
		// Accurate value when using string constructors
		ProbabilityNumber a1 = ProbabilityFactory.decimalValueOf(new BigDecimal("0.005"));
		ProbabilityNumber a2 = ProbabilityFactory.decimalValueOf(new BigDecimal("0.0049"));
		// Precision of the underlying double type representation is the cause
		// for the inaccuracy
		ProbabilityNumber b1 = ProbabilityFactory.decimalValueOf(new BigDecimal(a));
		ProbabilityNumber b2 = ProbabilityFactory.decimalValueOf(new BigDecimal(b));
		// System.out.println("String constructor initialised -> " + a1.subtract(a2).getValue().doubleValue());
		// System.out.println("Double constructor initialised -> " + b1.subtract(b2).getValue().doubleValue());
		assertEquals(0.0001, a1.subtract(a2).getValue().doubleValue(), DEFAULT_ROUNDING_THRESHOLD);
		assertEquals(0.0001, b1.subtract(b2).getValue().doubleValue(), DEFAULT_ROUNDING_THRESHOLD);
		
		// Raise BigDecimalProbabilityNumber values to powers
		ProbabilityNumber c1 = ProbabilityFactory.decimalValueOf(new BigDecimal("0.6"));
		ProbabilityNumber c2 = ProbabilityFactory.decimalValueOf(new BigDecimal(0.1));
		assertEquals(0.6 * 0.6, c1.pow(2).getValue().doubleValue(), DEFAULT_ROUNDING_THRESHOLD);
		assertEquals(0.1 * 0.1 * 0.1 * 0.1 * 0.1, c2.pow(5).getValue().doubleValue(), DEFAULT_ROUNDING_THRESHOLD);
		// BigDecimal uses integer scale internally, thus scale value cannot
		// exceed beyond the integer range
		// System.out.println("Power -> " + c1.pow(BigInteger.valueOf(1000000000L)).getValue());
	}
}
