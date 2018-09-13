package rfs0.aitam.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import rfs0.aitam.utilities.CalculationUtility;

public class NeedTimeSplit {
	
	private HashMap<Needs, BigDecimal> m_needTimeSplit = new HashMap<>();
	
	private NeedTimeSplit() {}
	

	public static class Builder {
		
		private NeedTimeSplit needTimeSplitToBuild;
		
		Builder() {
			needTimeSplitToBuild = new NeedTimeSplit();
		}
		
		NeedTimeSplit build() {
			equateNeedTimeSplit();
			NeedTimeSplit builtNeedTimeSplit = needTimeSplitToBuild;
			needTimeSplitToBuild = new NeedTimeSplit();
			return builtNeedTimeSplit;
		}
		
		Builder addNeedTimeSplit(Needs need, BigDecimal percentageOfTime) {
			this.needTimeSplitToBuild.m_needTimeSplit.put(need, percentageOfTime);
			return this;
		}
		
		private void equateNeedTimeSplit () {
			BigDecimal difference =  BigDecimal.ZERO;
			BigDecimal sum = CalculationUtility.sum(needTimeSplitToBuild.m_needTimeSplit.values());
			BigDecimal fraction = sum.subtract(new BigDecimal(sum.toString().split("\\.")[0]));
			if (sum.compareTo(BigDecimal.ONE) == 1) {
				difference = difference.subtract(fraction);
			}
			else if (sum.compareTo(BigDecimal.ONE) == -1) {
				difference = BigDecimal.ONE.subtract(fraction);
			}
			if (difference.equals(BigDecimal.ZERO)) {
				Logger.getLogger(NeedTimeSplit.class.getName()).log(Level.WARNING, "Check creation of need time split. Detected deviation from correct time allocation of 1.0: " + difference);
			}
			needTimeSplitToBuild.m_needTimeSplit.put(Needs.NOT_DEFINED, difference);
		}
	}
	
	public HashMap<Needs, BigDecimal> getNeedTimeSplit() {
		return m_needTimeSplit;
	}
}
