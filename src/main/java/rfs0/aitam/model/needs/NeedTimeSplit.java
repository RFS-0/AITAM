package rfs0.aitam.model.needs;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import rfs0.aitam.utilities.CalculationUtility;

public class NeedTimeSplit {
	
	private HashMap<Needs, BigDecimal> m_targetNeedTimeSplit = new HashMap<>();
	
	private NeedTimeSplit() {}
	

	public static class Builder {
		
		private NeedTimeSplit targetNeedTimeSplitToBuild;
		
		public Builder() {
			targetNeedTimeSplitToBuild = new NeedTimeSplit();
		}
		
		public NeedTimeSplit build() {
			equateTargetNeedTimeSplit();
			NeedTimeSplit builtTargetNeedTimeSplit = targetNeedTimeSplitToBuild;
			targetNeedTimeSplitToBuild = new NeedTimeSplit();
			return builtTargetNeedTimeSplit;
		}
		
		public Builder withNeedTimeSplit(Needs targetNeed, BigDecimal targetPercentageOfTime) {
			this.targetNeedTimeSplitToBuild.m_targetNeedTimeSplit.put(targetNeed, targetPercentageOfTime);
			return this;
		}
		
		private void equateTargetNeedTimeSplit () {
			BigDecimal difference =  BigDecimal.ZERO;
			BigDecimal sum = CalculationUtility.sum(targetNeedTimeSplitToBuild.m_targetNeedTimeSplit.values());
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
			targetNeedTimeSplitToBuild.m_targetNeedTimeSplit.put(Needs.NOT_DEFINED, difference);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Needs need: m_targetNeedTimeSplit.keySet()) {
			builder.append(need.name() + ": " + m_targetNeedTimeSplit.get(need).toString());
		}
		if (builder.length() == 0) {
			return super.toString();
		}
		return builder.toString();
	}
	
	public HashMap<Needs, BigDecimal> getTargetNeedTimeSplit() {
		return m_targetNeedTimeSplit;
	}
}
