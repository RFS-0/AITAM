package rfs0.aitam.model.needs;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import rfs0.aitam.settings.ISimulationSettings;
import rfs0.aitam.utilities.CalculationUtility;

/**
 * <p>This class is used to model the ideal relative need time split.
 * The individuals goal is to spent its time satisfying it different needs such that its absolute need time split is as close as possible to it target need time split. 
 * As such it is designed to record the relative time spent on each of the needs (i.e. percentage of time spent on each of the needs).
 * It uses the following attributes:</p>
 * 
 * <p> {@link TargetNeedTimeSplit#m_needTimeSplit}: The mapping from need to the ideal relative time spent satisfying it.</p>
 */
public class TargetNeedTimeSplit {
	
	/**
	 * <p>The mapping from need to the ideal relative time spent satisfying it.</p>
	 */
	private HashMap<Need, BigDecimal> m_needTimeSplit = new HashMap<>();
	
	private TargetNeedTimeSplit() {}

	public static class Builder {
		
		private TargetNeedTimeSplit targetNeedTimeSplitToBuild;
		
		/**
		 * <p>This builder must be used to instantiate {@link TargetNeedTimeSplit}s. 
		 * Furthermore, it ensures that the fractions all needs add upt to 1.0 (100%).</p>
		 */
		public Builder() {
			targetNeedTimeSplitToBuild = new TargetNeedTimeSplit();
		}

		/**
		 * <p>This method sets the target percentage of time for the provided need.</p>
		 * 
		 * @param targetNeed - the need for which the target percentage of time is defined.
		 * @param targetPercentageOfTime - the ideal relative amount of time spent satisfying this need.
		 * 
		 * @return {@link Builder} - the target need time split to be built with the target percentage of time set for the specified need.
		 */
		public Builder withNeedTimeSplit(Need targetNeed, BigDecimal targetPercentageOfTime) {
			this.targetNeedTimeSplitToBuild.m_needTimeSplit.put(targetNeed, targetPercentageOfTime);
			return this;
		}
		
		/**
		 * <p>This method ensures that the sum of percentages over all needs add up to 1 (i.e. 100%).</p>
		 */
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
				Logger.getLogger(TargetNeedTimeSplit.class.getName()).log(Level.WARNING, "Check creation of need time split. Detected deviation from correct time allocation of 1.0 taking rounding issues into account: " + difference);
				targetNeedTimeSplitToBuild.m_needTimeSplit.put(Need.NONE, difference);
			}
			else {
				/**
				 *
				 * Unless fractions add up to one we will always get a difference of 0.00001. This is due to the avoidance of non-terminating decimal expansion in the divide method of {@link CalculationUtility}.
				 * We handle this by adding the rounding difference to Need <code>NOT_DEFINED</code>.
				 */
				targetNeedTimeSplitToBuild.m_needTimeSplit.put(Need.NONE , targetNeedTimeSplitToBuild.getFractionForNeed(Need.NONE).add(difference));
			}
		}
		
		/**
		 * <p>This method builds a {@link TargetNeedTimeSplit} and initializes a new {@link TargetNeedTimeSplit} to be built. 
		 * Furthermore, it ensures that the fractions all needs add up to 1.0 (100%).</p>
		 * 
		 * @return RelativeNeedTimeSplit - the relative need time split built.
		 */
		public TargetNeedTimeSplit build() {
			equateTargetNeedTimeSplit();
			TargetNeedTimeSplit builtTargetNeedTimeSplit = targetNeedTimeSplitToBuild;
			targetNeedTimeSplitToBuild = new TargetNeedTimeSplit();
			return builtTargetNeedTimeSplit;
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
	
	/**
	 * @category Getters
	 */
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
