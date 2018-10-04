package individuals;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import activities.ActivityCategory;
import rfs0.aitam.commons.ISimulationSettings;
import rfs0.aitam.model.Environment;
import sim.field.network.Network;
import sim.util.Bag;
import sim.util.geo.MasonGeometry;

public final class IndividualInitializer {
	
	public static final Individual.Builder INDIVIDUAL_BUILDER = new Individual.Builder();
	public static ArrayList<Individual> ALL_INDIVIDUALS = new ArrayList<>();

	public IndividualInitializer() {}
	
	public static ArrayList<Individual> initIndividuals(Environment environment) {
		initEmptyIndividuals();
		initHouseholdAndFamilyRelatedAspects(environment);
		initWorkRelatedAspects(environment);
		initLeisureRelatedAspects(environment);
		return ALL_INDIVIDUALS;
	}
	
	private static void initEmptyIndividuals() {
		for (int i = 0; i < ISimulationSettings.NUMBER_OF_INDIVIDUALS; i++) {
			ALL_INDIVIDUALS.add(INDIVIDUAL_BUILDER.withId(i).build());
		}
	}
	
	private static void initHouseholdAndFamilyRelatedAspects(Environment environment) {
		ArrayList<Integer> initRange = geRangeOfIndividualsToInitialize();
		int networkId = 0;
		ArrayList<MasonGeometry> availableBuildings = getAvailableBuildings(environment);
		while (initRange.size() > 0) {
			ArrayList<Integer> householdMembersIndices = determineNetworkMembers(environment, initRange, ISimulationSettings.MIN_NUMBER_OF_HOUSEHOLD_MEMBERS , ISimulationSettings.MAX_NUMBER_OF_HOUSEHOLD_MEMBERS);
			Network householdNetwork = createNetworkForMemberIndices(householdMembersIndices);
			MasonGeometry homeBuilding = determineLocationForCategory(environment, availableBuildings, ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE);
			MasonGeometry thirdPlaceForHouseholdAndFamilyCare = determineBuildingForCategoryWithinDistance(environment, availableBuildings, homeBuilding, ISimulationSettings.MAX_DISTANCE_TO_THIRD_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE, ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE);
			for (Integer houseHoldMemberIndex: householdMembersIndices) {
				IndividualInitializer.INDIVIDUAL_BUILDER
					.adjust(IndividualInitializer.ALL_INDIVIDUALS.get(houseHoldMemberIndex))
					.withHomeBuilding(homeBuilding)
					.withHousholdMembersNetworkId(networkId)
					.withHousholdMembersNetwork(householdNetwork)
					.withThirdPlaceForHouseholdAndFamilyCareBuilding(thirdPlaceForHouseholdAndFamilyCare)
					.build();
			}
			networkId++;
		}
	}
	
	private static void initWorkRelatedAspects(Environment environment) {
		ArrayList<Integer> initRange = geRangeOfIndividualsToInitialize();
		int networkId = 0;
		ArrayList<MasonGeometry> availableBuildings = getAvailableBuildings(environment);
		while (initRange.size() > 0) {
			ArrayList<Integer> workColleguesIndices = determineNetworkMembers(environment, initRange, ISimulationSettings.MIN_NUMBER_OF_WORK_COLLEGUES , ISimulationSettings.MAX_NUMBER_OF_WORK_COLLEGUES);
			Network workColleguesNetwork = createNetworkForMemberIndices(workColleguesIndices);
			MasonGeometry workBuilding = determineLocationForCategory(environment, availableBuildings, ActivityCategory.WORK);
			MasonGeometry thirdPlaceForWork = determineBuildingForCategoryWithinDistance(environment, availableBuildings, workBuilding, ISimulationSettings.MAX_DISTANCE_TO_THIRD_PLACE_FOR_WORK, ActivityCategory.WORK);
			for (Integer workCollegueIndex: workColleguesIndices) {
				IndividualInitializer.INDIVIDUAL_BUILDER
					.adjust(IndividualInitializer.ALL_INDIVIDUALS.get(workCollegueIndex))
					.withWorkPlaceBuilding(workBuilding)
					.withWorkColleguesNetworkId(networkId)
					.withWorkColleguesNetwork(workColleguesNetwork)
					.withThirdPlaceForWorkBuilding(thirdPlaceForWork)
					.build();
			}
			networkId++;
		}
	}
	
	private static void initLeisureRelatedAspects(Environment environment) {
		ArrayList<Integer> initRange = geRangeOfIndividualsToInitialize();
		int networkId = 0;
		ArrayList<MasonGeometry> availableBuildings = getAvailableBuildings(environment);
		while (initRange.size() > 0) {
			ArrayList<Integer> friendsIndices = determineNetworkMembers(environment, initRange, ISimulationSettings.MIN_NUMBER_OF_FRIENDS, ISimulationSettings.MAX_NUMBER_OF_FRIENDS);
			Network friendsNetwork = createNetworkForMemberIndices(friendsIndices);
			MasonGeometry leisureBuilding = determineLocationForCategory(environment, availableBuildings, ActivityCategory.LEISURE);
			MasonGeometry thirdPlaceForLeisure = determineBuildingForCategoryWithinDistance(environment, availableBuildings, leisureBuilding, ISimulationSettings.MAX_DISTANCE_TO_THIRD_PLACE_FOR_WORK, ActivityCategory.LEISURE);
			for (Integer friendIndex: friendsIndices) {
				IndividualInitializer.INDIVIDUAL_BUILDER
					.adjust(IndividualInitializer.ALL_INDIVIDUALS.get(friendIndex))
					.withLeisureBuilding(leisureBuilding)
					.withFriendsNetworkId(networkId)
					.withFriendsNetwork(friendsNetwork)
					.withThirdPlaceForLeisureBuilding(thirdPlaceForLeisure)
					.build();
			}
			networkId++;
		}
	}
	
	private static Network createNetworkForMemberIndices(ArrayList<Integer> membersIndices) {
		Network network = new Network();
		for (Integer memberIndex: membersIndices) {
			network.addNode(IndividualInitializer.ALL_INDIVIDUALS.get(memberIndex));
		}
		return network;
	}
	
	private static ArrayList<Integer> determineNetworkMembers(Environment environment, ArrayList<Integer> remainingInitRange, int minNumberOfNetworkMembers, int maxNumberOfNetworkMembers) {
		ArrayList<Integer> networkMembers = new ArrayList<>();
		int numberOfHouseholdMembers = minNumberOfNetworkMembers + environment.random.nextInt(maxNumberOfNetworkMembers);
		if (numberOfHouseholdMembers > remainingInitRange.size()) {
			numberOfHouseholdMembers = remainingInitRange.size();
		}
		for (int i = 0; i < numberOfHouseholdMembers; i++) {
			int indexOfNextIndividual = environment.random.nextInt(remainingInitRange.size());
			networkMembers.add(remainingInitRange.remove(indexOfNextIndividual));
		}
		return networkMembers;
	}
	
	private static ArrayList<Integer> geRangeOfIndividualsToInitialize() {
		return IntStream.range(0, ISimulationSettings.NUMBER_OF_INDIVIDUALS).boxed().collect(Collectors.toCollection(ArrayList::new));
	}
	
	private static MasonGeometry determineLocationForCategory(Environment environment, ArrayList<MasonGeometry> availableBuildings, ActivityCategory activityCategory) {
		int indexOfChoosenLocation = environment.random.nextInt(availableBuildings.size());
		MasonGeometry choosenLocation = availableBuildings.remove(indexOfChoosenLocation);
		choosenLocation.addAttribute(ISimulationSettings.ATTRIBUTE_ACTIVITY_CATEGORY, activityCategory);
		return choosenLocation;
	}
	
	private static MasonGeometry determineBuildingForCategoryWithinDistance(Environment environment, ArrayList<MasonGeometry> availableBuildings, MasonGeometry building, double distance, ActivityCategory activityCategory) {
		Bag buildingsWithinDistance = environment.m_buildingsGeomVectorField.getObjectsWithinDistance(building, distance);
		int indexOfChoosenLocation = environment.random.nextInt(buildingsWithinDistance.size());
		MasonGeometry choosenLocation = availableBuildings.remove(indexOfChoosenLocation);
		choosenLocation.addAttribute(ISimulationSettings.ATTRIBUTE_ACTIVITY_CATEGORY, activityCategory);
		return choosenLocation;
	}
	
	public static ArrayList<MasonGeometry> getAvailableBuildings(Environment environment) {
		ArrayList<MasonGeometry> availableBuildings = new ArrayList<>();  
		for (Object geometry: environment.m_buildingsGeomVectorField.getGeometries()) {
			  if (geometry instanceof MasonGeometry) {
				  MasonGeometry mg = (MasonGeometry) geometry;
				  if (mg.getAttribute(ISimulationSettings.ATTRIBUTE_ACTIVITY_CATEGORY) == null) { // no attribute allocated yet
					  availableBuildings.add(mg);
				  }
			  }
		  }
		return availableBuildings;
	}
}
