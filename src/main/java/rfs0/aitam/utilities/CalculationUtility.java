package rfs0.aitam.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

public final class CalculationUtility {
		
	public static final BigDecimal TWO = BigDecimal.valueOf(2);
	public static final BigDecimal THREE = BigDecimal.valueOf(3);
	public static final BigDecimal FOUR = BigDecimal.valueOf(4);
	public static final BigDecimal FIVE = BigDecimal.valueOf(5);
	public static final BigDecimal SIX = BigDecimal.valueOf(6);
	public static final BigDecimal SEVEN = BigDecimal.valueOf(7);
	public static final BigDecimal EIGHT = BigDecimal.valueOf(8);
	public static final BigDecimal NINE = BigDecimal.valueOf(9);
	public static final BigDecimal ELEVEN = BigDecimal.valueOf(11);
	public static final BigDecimal TWELVE = BigDecimal.valueOf(12);
	
	public  CalculationUtility() {}
	
	public static BigDecimal sum(Collection<BigDecimal> values) {
		BigDecimal result = new BigDecimal(0.0);
		for (BigDecimal value : values) {
			result = result.add(value);
		}
		return result;
	}
	
	public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
		return dividend.divide(divisor, 5, RoundingMode.HALF_UP);
	}
}
