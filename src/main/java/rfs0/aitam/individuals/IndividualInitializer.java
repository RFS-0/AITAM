package rfs0.aitam.individuals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import rfs0.aitam.activities.ActivityCategory;
import rfs0.aitam.activities.ActivityInitializer;
import rfs0.aitam.model.Environment;
import rfs0.aitam.model.needs.Need;
import rfs0.aitam.model.needs.NeedTimeSplit;
import rfs0.aitam.settings.ISimulationSettings;
import rfs0.aitam.utilities.CalculationUtility;
import rfs0.aitam.utilities.DebugUtility;
import sim.field.network.Network;
import sim.util.Bag;
import sim.util.geo.MasonGeometry;

/**
 * <p>This class is used to initialize all activities. 
 * The details of all the activities available in the simulation can be found in {@link ActivityInitializer} resp. <a href="activity_configuration.xlsx">the activity configuration file</a>.</p>
 * As indicated by the usage of static methods and variables, this class is designed to be used statically.
 * It requires the following attributes:</p>
 * 
 * <p><b>Environment</b></p>
 * 
 * <p> {@link IndividualInitializer#s_environment}: The environment for which the individuals are initialized.</p>
 * 
 * <p><b>Builders</b></p>
 * 
 * <p> {@link IndividualInitializer#INDIVIDUAL_BUILDER}: The builder used to construct individuals.</p>
 * <p> {@link IndividualInitializer#NEED_TIME_SPLIT_BUILDER}: The builder used to construct {@link NeedTimeSplit}s.</p>
 * 
 * <p><b>Individuals</b></p>
 * 
 * <p> {@link IndividualInitializer#ALL_INDIVIDUALS}: A list containing all individuals built by this initializer.</p>
 *
 */
public final class IndividualInitializer {
	
	/**
	 * @category Environment
	 */
	
	/**
	 * <p> {@link IndividualInitializer#s_environment}: The environment for which the individuals are initialized.</p>
	 */
	private static Environment s_environment;
	
	/**
	 * @category Builder
	 */
	/**
	 * <p> {@link IndividualInitializer#INDIVIDUAL_BUILDER}: The builder used to construct individuals.</p>
	 */
	public static final Individual.Builder INDIVIDUAL_BUILDER = new Individual.Builder();
	/**
	 * <p> {@link IndividualInitializer#NEED_TIME_SPLIT_BUILDER}: The builder used to construct {@link NeedTimeSplit}s.</p>
	 */
	public static final NeedTimeSplit.Builder NEED_TIME_SPLIT_BUILDER = new NeedTimeSplit.Builder(); 
	
	/**
	 * @category Individuals
	 */
	
	/**
	 * <p> {@link IndividualInitializer#ALL_INDIVIDUALS}: A list containing all individuals built by this initializer.</p>
	 */
	public static ArrayList<Individual> ALL_INDIVIDUALS = new ArrayList<>();

	public IndividualInitializer() {}
	
	/**
	 * <p>This method initializes all individuals completely.</p>
	 * 
	 * <p><b>Important:</b> The order in which the methods are called matter.
	 * In particular the method which validates the built individuals must be called at last.</p>
	 * 
	 * @param environment - a reference to the environment for which the individuals are initialized
	 * @return ArrayList<Individual> - the list 
	 */
	public static ArrayList<Individual> initIndividuals(Environment environment) {
		s_environment = environment;
		initBasicIndividuals();
		initHouseholdAndFamilyRelatedAspects();
		initWorkRelatedAspects();
		initLeisureRelatedAspects();
		initTargetNeedTimeSplits();
		return ALL_INDIVIDUALS;
	}
	
	/**
	 * <p>This method initializes the configured number of individuals, but only sets the most basic attributes.</p>
	 */
	private static void initBasicIndividuals( ) {
		for (int i = 0; i < ISimulationSettings.NUMBER_OF_INDIVIDUALS; i++) {
			ALL_INDIVIDUALS.add(INDIVIDUAL_BUILDER
					.withEnvironment(s_environment)
					.withId(i)
					.build());
		}
	}
	
	/**
	 * <p>This method initializes all the household and family related aspects of the individuals.</p>
	 */
	private static void initHouseholdAndFamilyRelatedAspects() {
		ArrayList<Integer> initRange = geRangeOfIndividualsToInitialize();
		int networkId = 0;
		ArrayList<MasonGeometry> availableBuildings = getAvailableBuildingsForActivityCategory(s_environment, ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE);
		while (initRange.size() > 0) {
			ArrayList<Integer> householdMembersIndices = determineNetworkMembers(initRange, ISimulationSettings.MIN_NUMBER_OF_HOUSEHOLD_MEMBERS , ISimulationSettings.MAX_NUMBER_OF_HOUSEHOLD_MEMBERS);
			Network householdNetwork = createNetworkForMemberIndices(householdMembersIndices);
			MasonGeometry homeBuilding = determineLocationForCategory(availableBuildings, ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE);
			if (ISimulationSettings.IS_DEBUG) {
				homeBuilding.setUserData(DebugUtility.createLabelledPortrayal2DForBuilding(householdMembersIndices));
			}
			ArrayList<MasonGeometry> otherPlaceForHouseholdAndFamilyCareBuildings = determineBuildingsForCategoryWithinDistance(availableBuildings, homeBuilding, ISimulationSettings.MAX_DISTANCE_TO_OTHER_PLACES_FOR_HOUSEHOLD_AND_FAMILY_CARE, ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE);
			for (Integer houseHoldMemberIndex: householdMembersIndices) {
				INDIVIDUAL_BUILDER
					.adjust(IndividualInitializer.ALL_INDIVIDUALS.get(houseHoldMemberIndex))
					.withHomeBuilding(homeBuilding)
					.withHousholdMembersNetworkId(networkId)
					.withHousholdMembersNetwork(householdNetwork)
					.withOtherPlaceForHouseholdAndFamilyCareBuildings(otherPlaceForHouseholdAndFamilyCareBuildings)
					.build();
			}
			networkId++;
		}
	}
	
	/**
	 * <p>This method initializes all work related aspects of the individuals.</p>
	 */
	private static void initWorkRelatedAspects() {
		ArrayList<Integer> initRange = geRangeOfIndividualsToInitialize();
		int networkId = 0;
		ArrayList<MasonGeometry> availableBuildings = getAvailableBuildingsForActivityCategory(s_environment, ActivityCategory.WORK);
		while (initRange.size() > 0) {
			ArrayList<Integer> workColleguesIndices = determineNetworkMembers(initRange, ISimulationSettings.MIN_NUMBER_OF_WORK_COLLEGUES , ISimulationSettings.MAX_NUMBER_OF_WORK_COLLEGUES);
			Network workColleguesNetwork = createNetworkForMemberIndices(workColleguesIndices);
			MasonGeometry workBuilding = determineLocationForCategory(availableBuildings, ActivityCategory.WORK);
			ArrayList<MasonGeometry> otherPlaceForWorkBuildings = determineBuildingsForCategoryWithinDistance(availableBuildings, workBuilding, ISimulationSettings.MAX_DISTANCE_TO_OTHER_PLACES_FOR_WORK, ActivityCategory.WORK);
			for (Integer workCollegueIndex: workColleguesIndices) {
				INDIVIDUAL_BUILDER
					.adjust(IndividualInitializer.ALL_INDIVIDUALS.get(workCollegueIndex))
					.withWorkPlaceBuilding(workBuilding)
					.withWorkColleguesNetworkId(networkId)
					.withWorkColleguesNetwork(workColleguesNetwork)
					.withOtherPlaceForWorkBuildings(otherPlaceForWorkBuildings)
					.build();
			}
			networkId++;
		}
	}
	
	/**
	 * <p>This method initializes all leisure related aspects of the individuals.</p>
	 */
	private static void initLeisureRelatedAspects() {
		ArrayList<Integer> initRange = geRangeOfIndividualsToInitialize();
		int networkId = 0;
		ArrayList<MasonGeometry> availableBuildings = getAvailableBuildingsForActivityCategory(s_environment, ActivityCategory.LEISURE);
		while (initRange.size() > 0) {
			ArrayList<Integer> friendsIndices = determineNetworkMembers(initRange, ISimulationSettings.MIN_NUMBER_OF_FRIENDS, ISimulationSettings.MAX_NUMBER_OF_FRIENDS);
			Network friendsNetwork = createNetworkForMemberIndices(friendsIndices);
			MasonGeometry leisureBuilding = determineLocationForCategory(availableBuildings, ActivityCategory.LEISURE);
			ArrayList<MasonGeometry> otherPlaceForLeisureBuildings = determineBuildingsForCategoryWithinDistance(availableBuildings, leisureBuilding, ISimulationSettings.MAX_DISTANCE_TO_OTHER_PLACES_FOR_LEISURE, ActivityCategory.LEISURE);
			for (Integer friendIndex: friendsIndices) {
				INDIVIDUAL_BUILDER
					.adjust(IndividualInitializer.ALL_INDIVIDUALS.get(friendIndex))
					.withLeisureBuilding(leisureBuilding)
					.withFriendsNetworkId(networkId)
					.withFriendsNetwork(friendsNetwork)
					.withOtherPlaceForLeisureBuildings(otherPlaceForLeisureBuildings)
					.build();
			}
			networkId++;
		}
	}
	
	/**
	 * <p>This method initializes all target need time split related aspects of the individuals.</p>
	 */
	private static void initTargetNeedTimeSplits() {
		for (Individual individual: ALL_INDIVIDUALS) {
			INDIVIDUAL_BUILDER
					.adjust(individual)
					.withTargetNeedTimeSplit(NEED_TIME_SPLIT_BUILDER
							.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
							.withNeedTimeSplit(Need.CREATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
							.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
							.withNeedTimeSplit(Need.IDENTITY, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
							.withNeedTimeSplit(Need.LEISURE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
							.withNeedTimeSplit(Need.PARTICIPATION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
							.withNeedTimeSplit(Need.PROTECTION, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
							.withNeedTimeSplit(Need.SUBSISTENCE, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
							.withNeedTimeSplit(Need.UNDERSTANDING, CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.NINE))
							.build())
					.buildAndValidate();
		}
	}
	
	/**
	 * <p>This method creates a network containing all individuals provided by the method's parameter.</p>
	 * 
	 * @param membersIndices - a list of ids of individuals which are part of the network.
	 * @return Network - a network with all individuals as nodes but without edges linking them. 
	 */
	private static Network createNetworkForMemberIndices(ArrayList<Integer> membersIndices) {
		Network network = new Network();
		for (Integer memberIndex: membersIndices) {
			network.addNode(IndividualInitializer.ALL_INDIVIDUALS.get(memberIndex));
		}
		return network;
	}
	

	/**
	 * <p>This method randomly selects individuals form the remaining range to be part of a network. 
	 * It allows to define a minimum and maximum number of individuals to be selected. 
	 * Consequently the number of selected individuals will be between minNumberOfNetworkMembers <= numberOfMembers <= maxNumberOfNetworkMembers.
	 * However, if the selected number is smaller than the remaining number of members, all the remaining individuals will be selected.
	 * 
	 * @param remainingInitRange - a list of ids of individuals which are not allocated to a network yet.
	 * @param minNumberOfNetworkMembers - the minimum number of individuals in this network.
	 * @param maxNumberOfNetworkMembers - the maximum number of individuals in this network.
	 * @return ArrayList<Integer> - a list of all individuals which will be part of the network.
	 */
	private static ArrayList<Integer> determineNetworkMembers(ArrayList<Integer> remainingInitRange, int minNumberOfNetworkMembers, int maxNumberOfNetworkMembers) {
		ArrayList<Integer> networkMembers = new ArrayList<>();
		int max = Math.max(maxNumberOfNetworkMembers - minNumberOfNetworkMembers, minNumberOfNetworkMembers);
		int numberOfHouseholdMembers = minNumberOfNetworkMembers + s_environment.random.nextInt(max);
		if (numberOfHouseholdMembers > remainingInitRange.size()) {
			numberOfHouseholdMembers = remainingInitRange.size();
		}
		for (int i = 0; i < numberOfHouseholdMembers; i++) {
			int indexOfNextIndividual = s_environment.random.nextInt(remainingInitRange.size());
			networkMembers.add(remainingInitRange.remove(indexOfNextIndividual));
		}
		return networkMembers;
	}
	
	/**
	 * <p>This method creates a list of ids for the number of individuals to be initialized.</p>
	 * 
	 * @return ArrayList<Integer> - a list with an id for each of the individuals.
	 */
	private static ArrayList<Integer> geRangeOfIndividualsToInitialize() {
		return IntStream.range(0, ISimulationSettings.NUMBER_OF_INDIVIDUALS).boxed().collect(Collectors.toCollection(ArrayList::new));
	}
	
	/**
	 * <p>This method randomly chooses a building for the provided category out of all the available buildings.</p>
	 * 
	 * <p><b>Note:</b> Each building can be allocated to only one activity category.</p>
	 * 
	 * @param availableBuildings - a list with all the buildings which have not been allocated a category yet.
	 * @param activityCategory - the activity category for which this building will be used for.
	 * @return MasonGeometry - the building chosen.
	 */
	private static MasonGeometry determineLocationForCategory(ArrayList<MasonGeometry> availableBuildings, ActivityCategory activityCategory) {
		int indexOfChoosenLocation = s_environment.random.nextInt(availableBuildings.size());
		MasonGeometry choosenLocation = availableBuildings.remove(indexOfChoosenLocation);
		choosenLocation.addAttribute(ISimulationSettings.ATTRIBUTE_FOR_ACTIVITY_CATEGORY, activityCategory);
		return choosenLocation;
	}
	
	/**
	 * <p>This method selects the configured number of buildings out of the available buildings which are within the distance of some other building and are note allocated to a category yet.</p>
	 * 
	 * @param availableBuildings - a list of all buildings which have not been allocated to a category yet.
	 * @param building - the building which is used as a reference point to determine other buildings within the provided distance.
	 * @param distance - the distance within buildings will be selected.
	 * @param activityCategory - the activity category which the selected buildings will be allocated.
	 * @return ArrayList<MasonGeometry> - a list of all the selected buildings with the provided activity category set.
	 */
	private static ArrayList<MasonGeometry> determineBuildingsForCategoryWithinDistance(ArrayList<MasonGeometry> availableBuildings, MasonGeometry building, double distance, ActivityCategory activityCategory) {
		ArrayList<MasonGeometry> buildingsForCategory = new ArrayList<>();
		try {
			int numberOfBuildingsConfiguredForActivityCategory = -1;
			switch (activityCategory) {
			case HOUSEHOLD_AND_FAMILY_CARE:
				numberOfBuildingsConfiguredForActivityCategory = ISimulationSettings.NUMBER_OF_OTHER_PLACES_FOR_HOUSEHOLD_AND_FAMILY_CARE;
				break;
			case WORK:
				numberOfBuildingsConfiguredForActivityCategory = ISimulationSettings.NUMBER_OF_OTHER_PLACES_FOR_WORK;
				break;
			case LEISURE:
				numberOfBuildingsConfiguredForActivityCategory = ISimulationSettings.NUMBER_OF_OTHER_PLACES_FOR_LEISURE;
				break;
			default:
				break;
			}
			Bag buildingsWithinDistance = s_environment.getBuildingsField().getObjectsWithinDistance(building, distance);
			if (buildingsWithinDistance.size() < numberOfBuildingsConfiguredForActivityCategory) {
				throw new Exception("Too few buildings available for activity category! Increase distance or decrease number of other places for this category.");
			}
			while (buildingsForCategory.size() < numberOfBuildingsConfiguredForActivityCategory) {
				int indexOfChoosenLocation = s_environment.random.nextInt(buildingsWithinDistance.size());
				MasonGeometry choosenLocation = availableBuildings.remove(indexOfChoosenLocation);
				choosenLocation.addAttribute(ISimulationSettings.ATTRIBUTE_FOR_ACTIVITY_CATEGORY, activityCategory);
				buildingsForCategory.add(choosenLocation);
			}
		} 
		catch (Exception e) {
			Logger.getLogger(IndividualInitializer.class.getName()).log(Level.SEVERE, String.format("Can not determine buildings for activityCategory=%s. Adapt this method to cover it.", String.valueOf(activityCategory)), e);
		}
		return buildingsForCategory;
	}
	
	/**
	 * <p>This method finds all the buildings which can be used for the provided activity category or which do not have a category set yet.
	 * 
	 * @param environment
	 * @param activityCategory
	 * @return
	 */
	public static ArrayList<MasonGeometry> getAvailableBuildingsForActivityCategory(Environment environment, ActivityCategory activityCategory) {
		ArrayList<MasonGeometry> availableBuildings = new ArrayList<>();  
		for (Object geometry: s_environment.getBuildingsField().getGeometries()) {
			  if (geometry instanceof MasonGeometry) {
				  MasonGeometry mg = (MasonGeometry) geometry;
				  if (mg.getAttribute(ISimulationSettings.ATTRIBUTE_FOR_ACTIVITY_CATEGORY) == null || mg.getAttribute(ISimulationSettings.ATTRIBUTE_FOR_ACTIVITY_CATEGORY) == activityCategory) {
					  availableBuildings.add(mg);
				  }
			  }
		  }
		return availableBuildings;
	}
}
