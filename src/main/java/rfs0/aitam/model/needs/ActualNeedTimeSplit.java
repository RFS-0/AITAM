package rfs0.aitam.model.needs;

import java.math.BigDecimal;
import java.util.HashMap;

import rfs0.aitam.utilities.CalculationUtility;

public class ActualNeedTimeSplit {
	
	private HashMap<Need, BigDecimal> m_absoluteNeedTimeSplit = new HashMap<>();
	private boolean m_isUpdated = false;
	private HashMap<Need, BigDecimal> m_relativeNeedTimeSplit = new HashMap<>();
	
	public ActualNeedTimeSplit() {}
	
	public HashMap<Need, BigDecimal> getAbsoluteNeedTimeSplit() {
		return m_absoluteNeedTimeSplit;
	}
	
	public void updateNeedTimeSplit(Need need, BigDecimal timeSpentSatisfyingNeed) {
		if (m_absoluteNeedTimeSplit.get(need) == null) {
			m_absoluteNeedTimeSplit.put(need, timeSpentSatisfyingNeed);
		}
		else {
			m_absoluteNeedTimeSplit.put(need, m_absoluteNeedTimeSplit.get(need).add(timeSpentSatisfyingNeed));
		}
		m_isUpdated = false;
	}
	
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
	
	public void clear() {
		m_absoluteNeedTimeSplit.clear();
		m_relativeNeedTimeSplit.clear();
		m_isUpdated = false;
	}
}
