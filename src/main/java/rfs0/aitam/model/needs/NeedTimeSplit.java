package rfs0.aitam.model.needs;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import rfs0.aitam.commons.ISimulationSettings;
import rfs0.aitam.utilities.CalculationUtility;

public class NeedTimeSplit {
	
	private HashMap<Need, BigDecimal> m_needTimeSplit = new HashMap<>();
	
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
		
		public Builder withNeedTimeSplit(Need targetNeed, BigDecimal targetPercentageOfTime) {
			this.targetNeedTimeSplitToBuild.m_needTimeSplit.put(targetNeed, targetPercentageOfTime);
			return this;
		}
		
		private void equateTargetNeedTimeSplit () {
			BigDecimal difference =  BigDecimal.ZERO;
			BigDecimal sum = CalculationUtility.sum(targetNeedTimeSplitToBuild.m_needTimeSplit.values());
			BigDecimal fraction = sum.subtract(new BigDecimal(sum.toString().split("\\.")[0]));
			if (sum.compareTo(BigDecimal.ONE) == 1) {
				difference = difference.subtract(fraction);
			}
			else if (sum.compareTo(BigDecimal.ONE) == -1) {
				difference = BigDecimal.ONE.subtract(fraction);
			}
			// no difference
			if (difference.compareTo(BigDecimal.ZERO) == 0) {
				return;
			}
			// handle difference bigger than what can be attributed to rounding behavior
			else if (difference.abs().compareTo(ISimulationSettings.TOLERATED_ROUNDING_ERROR) > 0) {
				Logger.getLogger(NeedTimeSplit.class.getName()).log(Level.WARNING, "Check creation of need time split. Detected deviation from correct time allocation of 1.0 taking rounding issues into account: " + difference);
				targetNeedTimeSplitToBuild.m_needTimeSplit.put(Need.NOT_DEFINED, difference);
			}
			else {
				/**
				 *
				 * Unless fractions add up to one we will always get a difference of 0.00001. This is due to the avoidance of non-terminating decimal expansion in the divide method of {@link CalculationUtility}.
				 * We handle this by adding the rounding difference to Need <code>NOT_DEFINED</code>.
				 */
				targetNeedTimeSplitToBuild.m_needTimeSplit.put(Need.NOT_DEFINED , targetNeedTimeSplitToBuild.getFractionForNeed(Need.NOT_DEFINED).add(difference));
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Need need: m_needTimeSplit.keySet()) {
			builder.append(need.name() + ": " + m_needTimeSplit.get(need).toString() + "\n");
		}
		if (builder.length() == 0) {
			return super.toString();
		}
		return builder.toString();
	}
	
	public HashMap<Need, BigDecimal> getNeedTimeSplit() {
		return m_needTimeSplit;
	}
	
	public Set<Need> getNeeds() {
		return m_needTimeSplit.keySet();
	}
	
	public BigDecimal getFractionForNeed(Need need) {
		if (m_needTimeSplit.get(need) == null) {
			return BigDecimal.ZERO;
		}
		return m_needTimeSplit.get(need);
	}
}
