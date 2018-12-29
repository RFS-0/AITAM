package rfs0.aitam.model;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

import rfs0.aitam.settings.ISimulationSettings;
import sim.engine.SimState;

public class EnvironmentTest {
	
	public static final int MINUTES_IN_DAY = 1440;
	public static final int MINUTES_IN_WEEK = 7 * MINUTES_IN_DAY;
	public static final int NUMBER_OF_TEST_SIMULATIONS = 100;
	

	@Test
	@Ignore
	public void testStepEnvironment() {
		Environment environment = new Environment(1L);
		environment.start();
		assertEquals(true, environment.schedule.step(environment));
		DateTime timeAfterFirstStep = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, ISimulationSettings.BASE_HOUR, 1);
		assertEquals(environment.getSimulationTime().getCurrentDateTime(), timeAfterFirstStep);
	}
	
	@Test
	@Ignore
	public void testDay() {
		Environment environment = new Environment(2L);
		environment.start();
		
		for (int i = 0; i < MINUTES_IN_DAY; i++) {
			environment.schedule.step(environment);
		}
		
		environment.finish();
	}
	
	@Test
	public void testWeek() {
		Environment environment;
		for (int s = 0; s < NUMBER_OF_TEST_SIMULATIONS; s++) {
			environment = new Environment(s);
			environment.start();
			for (int i = 0; i < MINUTES_IN_WEEK; i++) {
				environment.schedule.step(environment);
			}
			environment.finish();
			environment = null;
			System.out.println(String.format("Completed %d of %d simulations", s+1, NUMBER_OF_TEST_SIMULATIONS));
		}
	}
	
	@Test
	@Ignore
	public void testRunForever() {
		SimState.doLoop(Environment.class, new String[0]);
	}
}
