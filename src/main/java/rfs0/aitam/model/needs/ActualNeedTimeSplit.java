package rfs0.aitam.model.needs;

import java.math.BigDecimal;
import java.util.HashMap;

public class ActualNeedTimeSplit {
	
	private HashMap<Need, BigDecimal> m_actualNeedTimeSplit = new HashMap<>();
	
	public ActualNeedTimeSplit() {}
	
	public HashMap<Need, BigDecimal> getActualNeedTimeSplit() {
		return m_actualNeedTimeSplit;
	}
	
	public void updateNeedTimeSplit(Need need, BigDecimal timeSpentSatisfyingNeed) {
		if (m_actualNeedTimeSplit.get(need) == null) {
			m_actualNeedTimeSplit.put(need, timeSpentSatisfyingNeed);
		}
		m_actualNeedTimeSplit.put(need, m_actualNeedTimeSplit.get(need).add(timeSpentSatisfyingNeed));
	}
}
