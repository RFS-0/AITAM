package rfs0.aitam.model;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import rfs0.aitam.settings.ISimulationSettings;
import sim.engine.SimState;
import sim.engine.Steppable;

public class SimulationTime implements Steppable {

	private static final long serialVersionUID = 1L;
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.longDateTime();
	
	private DateTime m_dateTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, ISimulationSettings.BASE_HOUR, ISimulationSettings.BASE_MINUTE);

	@Override
	public void step(SimState state) {
		((Environment) state).getOutputHolder().put(ISimulationSettings.TIME_STAMP, m_dateTime); // record current value
		m_dateTime = m_dateTime.plusMinutes(1); // increment time
		if (m_dateTime.getHourOfDay() == 23 && m_dateTime.getMinuteOfHour() == 59) {
			System.out.println("Simulated: " + DATE_TIME_FORMATTER.print(m_dateTime));
		}
	}
	
	public DateTime getCurrentDateTime() {
		return m_dateTime;
	}
	
	public DateTime getCurrentTime() {
		return m_dateTime.withDate(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY);
	}

	public int getCurrentWeekDay() {
		return m_dateTime.getDayOfWeek();
	}
}
