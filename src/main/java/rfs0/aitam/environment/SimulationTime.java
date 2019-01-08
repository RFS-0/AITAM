package rfs0.aitam.environment;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import rfs0.aitam.settings.ISimulationSettings;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * <p>This class is used to model and keep track of the simulation's time.
 * To do so it relies on the following attributes:</p>
 *
 * <p>{@link SimulationTime#DATE_TIME_FORMATTER}: The formatter used to print the end of each simulation day in human readable form.</p>
 * <p>{@link SimulationTime#m_dateTime}: The current time in the simulation. 
 * It is incremented each step by one minute (i.e. each step in the simulation corresponds to one minute in real time).</p>
 */
public class SimulationTime implements Steppable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * <p>The formatter used to print the end of each simulation day in human readable form.</p>
	 */
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.longDateTime();
	/**
	 * <p>The current time in the simulation. 
	 * It is incremented each step by one minute (i.e. each step in the simulation corresponds to one minute in real time).</p>
	 */
	private DateTime m_dateTime = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, ISimulationSettings.BASE_HOUR, ISimulationSettings.BASE_MINUTE);

	/**
	 * <p>This method advances simulation time by one minute each time it is called (once per simulation step) and print the current date at the end of each day.</p>
	 */
	@Override
	public void step(SimState state) {
		if (m_dateTime.getHourOfDay() == 23 && m_dateTime.getMinuteOfHour() == 59) {
			System.out.println("Simulated: " + DATE_TIME_FORMATTER.print(m_dateTime));
		}
		m_dateTime = m_dateTime.plusMinutes(1); // increment time
		Environment environment = ((Environment) state);
		environment.getOutputHolder().put(ISimulationSettings.TIME_STAMP, m_dateTime); // record time stamp
		environment.getOutputHolder().put(ISimulationSettings.DAY_OF_WEEK, m_dateTime.getDayOfWeek()); // record day of week
		environment.getOutputHolder().put(ISimulationSettings.HOUR_OF_DAY, m_dateTime.getHourOfDay()); // record hour of day
		environment.getOutputHolder().put(ISimulationSettings.MINUTE_OF_HOUR, m_dateTime.getMinuteOfHour()); // record minute of hour
	}
	
	/**
	 * @category Getters
	 */
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
