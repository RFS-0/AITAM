package rfs0.aitam.model;

import org.joda.time.DateTime;

import rfs0.aitam.commons.ISimulationSettings;
import sim.engine.SimState;
import sim.engine.Steppable;

public class SimulationTime implements Steppable {

	private static final long serialVersionUID = 1L;
	
	private DateTime m_time = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, ISimulationSettings.BASE_HOUR, ISimulationSettings.BASE_MINUTE);

	@Override
	public void step(SimState state) {
		m_time = m_time.plusMinutes(1);
	}
	
	public DateTime getCurrentTime() {
		return m_time;
	}

	public int getCurrentWeekDay() {
		return m_time.getDayOfWeek();
	}
}
