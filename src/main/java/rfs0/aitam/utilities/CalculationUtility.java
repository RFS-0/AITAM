package rfs0.aitam.utilities;

import java.math.BigDecimal;
import java.util.Collection;

public final class CalculationUtility {
	
	public  CalculationUtility() {}
	
	public static BigDecimal sum(Collection<BigDecimal> values) {
		BigDecimal result = new BigDecimal(0.0);
		for (BigDecimal value : values) {
			result = result.add(value);
		}
		return result;
	}
}
