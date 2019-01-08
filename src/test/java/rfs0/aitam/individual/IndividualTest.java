package rfs0.aitam.individual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

import rfs0.aitam.activity.ActivityAgenda;
import rfs0.aitam.environment.Environment;
import rfs0.aitam.individual.Individual;
import rfs0.aitam.settings.ISimulationSettings;
import sim.engine.SimState;
import sim.engine.Steppable;

public class IndividualTest {
	
	@Ignore
	@Test
	public void testPlanActivities() {
		Environment environment = new Environment(1L) {
			private static final long serialVersionUID = 1L;

			@Override
			public void start() {
				super.start();
				// schedule the individual via anonymus classes
				for (Individual individual: this.getIndividuals()) {
					schedule.scheduleRepeating(0.0, 0, new Steppable() {			
						private static final long serialVersionUID = 1L;
						@Override
						public void step(SimState state) {
							individual.planJointActivities();
						}
					});
					schedule.scheduleRepeating(0.0, 1, new Steppable() {			
						private static final long serialVersionUID = 1L;
						@Override
						public void step(SimState state) {
							individual.carryOverJointActivities();
						}
					});
					schedule.scheduleRepeating(0.0, 2, new Steppable() {			
						private static final long serialVersionUID = 1L;
						@Override
						public void step(SimState state) {
							individual.planIndividualActivities();
						}
					});
					schedule.scheduleRepeating(0.0, 3, new Steppable() {			
						private static final long serialVersionUID = 1L;
						@Override
						public void step(SimState state) {
							individual.chooseBestAgenda();
						}
					});
				}
				schedule.scheduleRepeating(0.0, 10, getSimulationTime()); // update clock after indivdual have executed their step
			};
		};
		environment.start();
		environment.schedule.step(environment);
		Individual individual = environment.getIndividuals().get(0);
		ActivityAgenda activityPlan = individual.getActivityAgenda();
		DateTime startOfFirstDay = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, ISimulationSettings.BASE_HOUR, ISimulationSettings.BASE_MINUTE);
		DateTime endOfFirstDay = new DateTime(ISimulationSettings.BASE_YEAR, ISimulationSettings.BASE_MONTH, ISimulationSettings.BASE_DAY, 23, 59);
		
		// plan is not empty
		assertEquals(false, activityPlan.getIntervals().isEmpty());
		
		// the plan covers the whole day
		assertEquals(startOfFirstDay, activityPlan.getFirstPlannedInterval().getStart());
		assertEquals(endOfFirstDay, activityPlan.getLastPlannedInterval().getEnd());
	
		// all entries have an activity
		activityPlan.getIntervals().stream().forEach(interval -> {
			assertNotEquals(null, activityPlan.getActivityForInterval(interval));
		});
		
		// all entries have a location
		activityPlan.getIntervals().stream().forEach(interval -> {
			assertNotEquals(null, activityPlan.getNodeForInterval(interval));
		});
	}
	
	@Test
	public void testExecuteActivity() {
		Environment environment = new Environment(1L) {
			private static final long serialVersionUID = 1L;

			@Override
			public void start() {
				super.start();
				getIndividualsField().clear();
				getIndividualsField().setMBR(getBuildingsField().getMBR());
				for (Individual individual: getIndividuals()) {
					schedule.scheduleRepeating(0.0, 0, new Steppable() {			
						private static final long serialVersionUID = 1L;
						@Override
						public void step(SimState state) {
							if (individual.isPlanningPossible(ISimulationSettings.AVAILABLE_TIME_POINTS_FOR_PLANNING_ACTIVITIES)) {
								individual.planJointActivities();
							}
						}
					});
					schedule.scheduleRepeating(0.0, 1, new Steppable() {			
						private static final long serialVersionUID = 1L;
						@Override
						public void step(SimState state) {
							individual.carryOverJointActivities();
						}
					});
					schedule.scheduleRepeating(0.0, 2, new Steppable() {			
						private static final long serialVersionUID = 1L;
						@Override
						public void step(SimState state) {
							if (individual.isPlanningPossible(ISimulationSettings.AVAILABLE_TIME_POINTS_FOR_PLANNING_ACTIVITIES)) {
								individual.planIndividualActivities();
							}
						}
					});
					schedule.scheduleRepeating(0.0, 3, new Steppable() {			
						private static final long serialVersionUID = 1L;
						@Override
						public void step(SimState state) {
							individual.chooseBestAgenda();
						}
					});
					schedule.scheduleRepeating(0.0, 4, new Steppable() {			
						private static final long serialVersionUID = 1L;
						@Override
						public void step(SimState state) {
							individual.move();
						}
					});
					schedule.scheduleRepeating(0.0, 5, new Steppable() {			
						private static final long serialVersionUID = 1L;
						@Override
						public void step(SimState state) {
							individual.executeActivity();
						}
					});
				}
				schedule.scheduleRepeating(0.0, 6, getSimulationTime()); // update clock after indivdual have executed their step
				schedule.scheduleRepeating(0.0, 7, getIndividualsField().scheduleSpatialIndexUpdater());
			};
		};
		environment.start();
		environment.schedule.step(environment);
	}	
}
