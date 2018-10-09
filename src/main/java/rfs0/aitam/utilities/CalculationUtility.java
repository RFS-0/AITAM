package rfs0.aitam.utilities;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.HashMap;

import activities.ActivityPlan;
import rfs0.aitam.commons.ISimulationSettings;
import rfs0.aitam.model.needs.Need;
import rfs0.aitam.model.needs.NeedTimeSplit;

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
	
	public static BigDecimal sum(Collection<BigDecimal> values) {
		BigDecimal result = createBigDecimal(0);
		for (BigDecimal value : values) {
			result = result.add(value, MATH_CONTEXT);
		}
		return result;
	}
	
	public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
		return dividend.divide(divisor, MATH_CONTEXT);
	}
	
	public static BigDecimal multiply(BigDecimal multiplicand, BigDecimal multiplier) {
		return multiplicand.multiply(multiplier, MATH_CONTEXT);
	}
	
	public static BigDecimal calculateMeanSquaredError(ActivityPlan activityPlan, NeedTimeSplit targetNeedTimeSplit) {
		BigDecimal meanSquaredError = BigDecimal.ZERO;
		HashMap<Need,BigDecimal> actualRelativeNeedTimeSplit = activityPlan.getActualNeedTimeSplit().getRelativeNeedTimeSplit();
		for (Need need: actualRelativeNeedTimeSplit.keySet()) {
			BigDecimal targetFractionForNeed = targetNeedTimeSplit.getFractionForNeed(need);
			BigDecimal actualFractionForNeed = actualRelativeNeedTimeSplit.get(need);
			meanSquaredError = meanSquaredError.add(actualFractionForNeed.subtract(targetFractionForNeed).pow(2));
		}
		meanSquaredError = CalculationUtility.divide(meanSquaredError, BigDecimal.valueOf(actualRelativeNeedTimeSplit.size()));
		return meanSquaredError;
	}
}
