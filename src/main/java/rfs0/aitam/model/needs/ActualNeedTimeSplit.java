package rfs0.aitam.model.needs;

import java.math.BigDecimal;
import java.util.HashMap;

public class ActualNeedTimeSplit {
	
	private HashMap<Needs, BigDecimal> m_actualNeedTimeSplit = new HashMap<>();
	
	public ActualNeedTimeSplit() {}
	
	public HashMap<Needs, BigDecimal> getActualNeedTimeSplit() {
		return m_actualNeedTimeSplit;
	}
	
	public void updateNeedTimeSplit(Needs need, BigDecimal timeSpentSatisfyingNeed) {
		if (m_actualNeedTimeSplit.get(need) == null) {
			m_actualNeedTimeSplit.put(need, timeSpentSatisfyingNeed);
		}
		m_actualNeedTimeSplit.put(need, m_actualNeedTimeSplit.get(need).add(timeSpentSatisfyingNeed));
	}
}
