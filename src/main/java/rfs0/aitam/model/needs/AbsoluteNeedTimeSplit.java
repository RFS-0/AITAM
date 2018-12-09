package rfs0.aitam.model.needs;

import java.math.BigDecimal;
import java.util.HashMap;

import rfs0.aitam.utilities.CalculationUtility;

/**
 * <p>This class is used to model the absolute need time split (absolute time spent per need).
 * As such it is designed to record the absolute time spent on each of the needs in minutes.
 * This absolute need time split can be converted into a relative need time split.
 * It uses the following attributes</p>
 *
 * <p>{@link AbsoluteNeedTimeSplit#m_absoluteNeedTimeSplit}: The absolute need time split. It captures the time spent in minutes on each of the needs. E.g. {@link Need#AFFECTION} = 60 (minutes), {@link Need#CREATION} = 60 (minutes) etc.</p>
 * <p>{@link AbsoluteNeedTimeSplit#m_relativeNeedTimeSplit}: The relative need time split equivalent to the actual need actual need time split. It captures the relative time spent on each of the needs. E.g. {@link Need#AFFECTION} = 0.5 (50%), {@link Need#CREATION} = 0.5 (50%).</p>
 * <p>{@link AbsoluteNeedTimeSplit#m_isUpdated}: Indicates whether or not the relative need time split is updated to match the current absolute need time split or not.</p> 
 */
public class AbsoluteNeedTimeSplit {
	
	/**
	 * <p>The absolute need time split. It captures the time spent in minutes on each of the needs. E.g. {@link Need#AFFECTION} = 60 (minutes), {@link Need#CREATION} = 60 (minutes) etc.</p>
	 */
	private HashMap<Need, BigDecimal> m_absoluteNeedTimeSplit = new HashMap<>();
	/**
	 * <p>The relative need time split equivalent to the absolute need time split. It captures the relative time spent on each of the needs. E.g. {@link Need#AFFECTION} = 0.5 (50%), {@link Need#CREATION} = 0.5 (50%).</p>
	 */
	private HashMap<Need, BigDecimal> m_relativeNeedTimeSplit = new HashMap<>();
	/**
	 * <p>Indicates whether or not the relative need time split is updated to match the current absolute need time split or not.</p>
	 */
	private boolean m_isUpdated = false;
	
	public AbsoluteNeedTimeSplit() {}
	
	/**
	 * <p>This method returns the a
	 * @return HashMap<Need, BigDecimal> - the actual need time split (absolute time spent per need).
	 */
	public HashMap<Need, BigDecimal> getAbsoluteNeedTimeSplit() {
		return m_absoluteNeedTimeSplit;
	}
	
	/**
	 * <p>This method updates the absolute time spent for the specified need and the specified amount of time in minutes.</p>
	 * 
	 * @param need - the need for which the absolute time spent should be increased by the specified number of minutes.
	 * @param timeSpentSatisfyingNeed - the number of minutes spent on the need provided.
	 */
	public void updateNeedTimeSplit(Need need, BigDecimal timeSpentSatisfyingNeed) {
		if (m_absoluteNeedTimeSplit.get(need) == null) {
			m_absoluteNeedTimeSplit.put(need, timeSpentSatisfyingNeed);
		}
		else {
			m_absoluteNeedTimeSplit.put(need, m_absoluteNeedTimeSplit.get(need).add(timeSpentSatisfyingNeed));
		}
		m_isUpdated = false;
	}
	
	/**
	 * <p>This method converts the absolute need time split into a relative need time split (if necessary) and returns it.</p>
	 * 
	 * @return HashMap<Need, BigDecimal> - the relative need time split.
	 */
	public HashMap<Need, BigDecimal> getRelativeNeedTimeSplit() {
		if (m_isUpdated) {
			return m_relativeNeedTimeSplit;
		}
		else {
			BigDecimal totalTimeSpentOnAllNeeds = CalculationUtility.sum(m_absoluteNeedTimeSplit.values());
			for (Need need: m_absoluteNeedTimeSplit.keySet()) {
				BigDecimal totalTimeSpentOnNeed = m_absoluteNeedTimeSplit.get(need);
				m_relativeNeedTimeSplit.put(need, CalculationUtility.divide(totalTimeSpentOnNeed, totalTimeSpentOnAllNeeds));
			}
			return m_relativeNeedTimeSplit;
		}
	}
	
	/**
	 * <p>This method resets the absolute need time split.</p>
	 */
	public void clear() {
		m_absoluteNeedTimeSplit.clear();
		m_relativeNeedTimeSplit.clear();
		m_isUpdated = false;
	}
}
