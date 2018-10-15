package rfs0.aitam.model;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;

import rfs0.aitam.commons.ISimulationSettings;

public class EnvironmentTest {

	@Test
	public void testStepEnvironment() {
		Environment environment = new Environment(1L);
		environment.start();
		assertEquals(true, environment.schedule.step(environment));
		DateTime timeAfterFirstStep = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, ISimulationSettings.BASE_HOUR, 1);
		assertEquals(environment.getSimulationTime().getCurrentDateTime(), timeAfterFirstStep);
	}
}
