package rfs0.aitam.individuals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

import activities.ActivityAgenda;
import individuals.Individual;
import rfs0.aitam.commons.ISimulationSettings;
import rfs0.aitam.model.Environment;
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
							individual.planJointActivities((Environment) state);
						}
					});
					schedule.scheduleRepeating(0.0, 1, new Steppable() {			
						private static final long serialVersionUID = 1L;
						@Override
						public void step(SimState state) {
							individual.carryOverJointActivities((Environment) state);
						}
					});
					schedule.scheduleRepeating(0.0, 2, new Steppable() {			
						private static final long serialVersionUID = 1L;
						@Override
						public void step(SimState state) {
							individual.planIndividualActivities((Environment) state);
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
		assertEquals(endOfFirstDay, activityPlan.getLastPlannedActivityInterval().getEnd());
	
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
				getIndividualsGeomVectorField().clear();
				getIndividualsGeomVectorField().setMBR(m_buildingsGeomVectorField.getMBR());
				for (Individual individual: getIndividuals()) {
					schedule.scheduleRepeating(0.0, 0, new Steppable() {			
						private static final long serialVersionUID = 1L;
						@Override
						public void step(SimState state) {
							Environment environment = (Environment) state;
							if (individual.isPlanningPossible(environment, ISimulationSettings.AVAILABLE_TIME_POINTS_FOR_PLANNING_OF_JOINT_ACTIVITIES)) {
								individual.planJointActivities(environment);
							}
						}
					});
					schedule.scheduleRepeating(0.0, 1, new Steppable() {			
						private static final long serialVersionUID = 1L;
						@Override
						public void step(SimState state) {
							individual.carryOverJointActivities((Environment) state);
						}
					});
					schedule.scheduleRepeating(0.0, 2, new Steppable() {			
						private static final long serialVersionUID = 1L;
						@Override
						public void step(SimState state) {
							Environment environment = (Environment) state;
							if (individual.isPlanningPossible(environment, ISimulationSettings.AVAILABLE_TIME_POINTS_FOR_PLANNING_OF_INDIVIDUAL_ACTIVITIES)) {
								individual.planIndividualActivities(environment);
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
							individual.executeActivity((Environment) state);
						}
					});
				}
				schedule.scheduleRepeating(0.0, 5, getSimulationTime()); // update clock after indivdual have executed their step
				schedule.scheduleRepeating(0.0, 6, getIndividualsGeomVectorField().scheduleSpatialIndexUpdater());
			};
		};
		environment.start();
		environment.schedule.step(environment);
	}	
}
