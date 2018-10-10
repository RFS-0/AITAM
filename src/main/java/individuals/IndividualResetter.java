package individuals;

import rfs0.aitam.model.Environment;
import sim.engine.SimState;
import sim.engine.Steppable;

public class IndividualResetter implements Steppable {

	private static final long serialVersionUID = 1L;

	@Override
	public void step(SimState state) {
		Environment environment = (Environment) state;
		for (Individual individual: environment.getIndividuals()) {
			individual.getActivityAgenda().clearAgenda();
			individual.getActivityAgenda().clearLocations();
			individual.getJointActivityAgenda().clearAgenda();
			individual.getJointActivityAgenda().clearLocations();
			individual.setNumberOfFriendsNetworkActivitiesPlanned(0);
			individual.setNumberOfHouseholdNetworkActivitiesPlanned(0);
			individual.setNumberOfWorkColleguesNetworkActivitiesPlanned(0);
		}
	}
}
