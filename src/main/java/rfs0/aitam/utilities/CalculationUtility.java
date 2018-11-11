package rfs0.aitam.utilities;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.HashMap;

import ec.util.MersenneTwisterFast;
import rfs0.aitam.activities.ActivityAgenda;
import rfs0.aitam.model.needs.Need;
import rfs0.aitam.model.needs.NeedTimeSplit;
import rfs0.aitam.settings.ISimulationSettings;

public final class CalculationUtility {
		
	public static final MathContext MATH_CONTEXT = new MathContext(ISimulationSettings.PRECISION_USED_FOR_BIG_DECIMAL, ISimulationSettings.ROUNDING_MODE_USED_FOR_BIG_DECIMAL);
	public static final BigDecimal TWO = new BigDecimal(2, MATH_CONTEXT);
	public static final BigDecimal THREE = new BigDecimal(3, MATH_CONTEXT);
	public static final BigDecimal FOUR = new BigDecimal(4, MATH_CONTEXT);
	public static final BigDecimal FIVE = new BigDecimal(5, MATH_CONTEXT);
	public static final BigDecimal SIX = new BigDecimal(6, MATH_CONTEXT);
	public static final BigDecimal SEVEN = new BigDecimal(7, MATH_CONTEXT);
	public static final BigDecimal EIGHT = new BigDecimal(8, MATH_CONTEXT);
	public static final BigDecimal NINE = new BigDecimal(9, MATH_CONTEXT);
	public static final BigDecimal ELEVEN = new BigDecimal(11, MATH_CONTEXT);
	public static final BigDecimal TWELVE = new BigDecimal(12, MATH_CONTEXT);
	
	public  CalculationUtility() {}
	
	public static BigDecimal createBigDecimal(int value) {
		BigDecimal bigDecimal = new BigDecimal(value);
		bigDecimal = bigDecimal.setScale(ISimulationSettings.SCALE_USED_FOR_BIG_DECIMAL, MATH_CONTEXT.getRoundingMode());
		return bigDecimal;
	}
	
	public static BigDecimal createBigDecimal(double value) {
		BigDecimal bigDecimal = new BigDecimal(value);
		bigDecimal = bigDecimal.setScale(ISimulationSettings.SCALE_USED_FOR_BIG_DECIMAL, MATH_CONTEXT.getRoundingMode());
		return bigDecimal;
	}
	
	public static BigDecimal add(BigDecimal value, BigDecimal addend) {
		return value.add(addend, MATH_CONTEXT);
	}
	
	public static BigDecimal substract(BigDecimal minuend, BigDecimal substrahend) {
		return minuend.subtract(substrahend, MATH_CONTEXT);
	}
	
	public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
		return dividend.divide(divisor, MATH_CONTEXT);
	}
	
	public static BigDecimal multiply(BigDecimal multiplicand, BigDecimal multiplier) {
		return multiplicand.multiply(multiplier, MATH_CONTEXT);
	}
	
	public static BigDecimal power(BigDecimal value, int power) {
		return value.pow(power, MATH_CONTEXT);
	}
	
	public static BigDecimal sum(Collection<BigDecimal> values) {
		BigDecimal result = createBigDecimal(0);
		for (BigDecimal value : values) {
			result = result.add(value, MATH_CONTEXT);
		}
		return result;
	}
	
	public static BigDecimal calculateMeanSquaredError(ActivityAgenda activityAgenda, NeedTimeSplit targetNeedTimeSplit) {
		BigDecimal meanSquaredError = BigDecimal.ZERO;
		HashMap<Need,BigDecimal> actualRelativeNeedTimeSplit = activityAgenda.getActualNeedTimeSplit().getRelativeNeedTimeSplit();
		for (Need need: targetNeedTimeSplit.getNeedTimeSplit().keySet()) {
			BigDecimal targetFractionForNeed = targetNeedTimeSplit.getFractionForNeed(need);
			BigDecimal actualFractionForNeed = actualRelativeNeedTimeSplit.get(need);
			if (actualFractionForNeed == null) {
				actualFractionForNeed = BigDecimal.ZERO;
			}
			meanSquaredError = CalculationUtility.add(meanSquaredError, CalculationUtility.power(CalculationUtility.substract(actualFractionForNeed, targetFractionForNeed), 2));
		}
		return meanSquaredError;
	}
	
	public static double map(MersenneTwisterFast random, double rangeStart, double rangeEnd) {
		return rangeStart + (rangeEnd - rangeStart) * random.nextDouble();
	}
}
