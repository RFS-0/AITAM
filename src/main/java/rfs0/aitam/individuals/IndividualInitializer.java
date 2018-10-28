package rfs0.aitam.individuals;

import java.awt.Font;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import rfs0.aitam.activities.ActivityCategory;
import rfs0.aitam.commons.ISimulationSettings;
import rfs0.aitam.model.Environment;
import rfs0.aitam.model.needs.Need;
import rfs0.aitam.model.needs.NeedTimeSplit;
import rfs0.aitam.utilities.CalculationUtility;
import sim.field.network.Network;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.geo.GeomPortrayal;
import sim.portrayal.simple.LabelledPortrayal2D;
import sim.util.Bag;
import sim.util.geo.MasonGeometry;

public final class IndividualInitializer {
	
	public static final Individual.Builder INDIVIDUAL_BUILDER = new Individual.Builder();
	public static final NeedTimeSplit.Builder NEED_TIME_SPLIT_BUILDER = new NeedTimeSplit.Builder(); 
	public static ArrayList<Individual> ALL_INDIVIDUALS = new ArrayList<>();

	public IndividualInitializer() {}
	
	public static ArrayList<Individual> initIndividuals(Environment environment) {
		initEmptyIndividuals(environment);
		initHouseholdAndFamilyRelatedAspects(environment);
		initWorkRelatedAspects(environment);
		initLeisureRelatedAspects(environment);
		initTargetNeedTimeSplits();
		return ALL_INDIVIDUALS;
	}
	
	private static void initEmptyIndividuals(Environment environment) {
		for (int i = 0; i < ISimulationSettings.NUMBER_OF_INDIVIDUALS; i++) {
			ALL_INDIVIDUALS.add(INDIVIDUAL_BUILDER
					.withEnvironment(environment)
					.withId(i)
					.build());
		}
	}
	
	private static void initHouseholdAndFamilyRelatedAspects(Environment environment) {
		ArrayList<Integer> initRange = geRangeOfIndividualsToInitialize();
		int networkId = 0;
		ArrayList<MasonGeometry> availableBuildings = getAvailableBuildingsForActivityCategory(environment, ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE);
		while (initRange.size() > 0) {
			ArrayList<Integer> householdMembersIndices = determineNetworkMembers(environment, initRange, ISimulationSettings.MIN_NUMBER_OF_HOUSEHOLD_MEMBERS , ISimulationSettings.MAX_NUMBER_OF_HOUSEHOLD_MEMBERS);
			Network householdNetwork = createNetworkForMemberIndices(householdMembersIndices);
			MasonGeometry homeBuilding = determineLocationForCategory(environment, availableBuildings, ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE);
			homeBuilding.setUserData(
					new LabelledPortrayal2D(
							new GeomPortrayal(ISimulationSettings.COLOR_OF_BUILDING, ISimulationSettings.SIZE_OF_BUILDING, true), 
						10,
						5,
						0.5,
						0.5,
						new Font("SansSerif",Font.BOLD, 15),
						LabelledPortrayal2D.ALIGN_LEFT,
						null, 
						ISimulationSettings.COLOR_OF_BUILDING, 
						false) {
					private static final long serialVersionUID = 1L;
					@Override
					public String getLabel(Object object, DrawInfo2D info) {
						return String.format("Home of: %s ", String.valueOf(householdMembersIndices));
					}
			});
			ArrayList<MasonGeometry> otherPlaceForHouseholdAndFamilyCareBuildings = determineBuildingsForCategoryWithinDistance(environment, availableBuildings, homeBuilding, ISimulationSettings.MAX_DISTANCE_TO_THIRD_PLACE_FOR_HOUSEHOLD_AND_FAMILY_CARE, ActivityCategory.HOUSEHOLD_AND_FAMILY_CARE);
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
	
	private static void initWorkRelatedAspects(Environment environment) {
		ArrayList<Integer> initRange = geRangeOfIndividualsToInitialize();
		int networkId = 0;
		ArrayList<MasonGeometry> availableBuildings = getAvailableBuildingsForActivityCategory(environment, ActivityCategory.WORK);
		while (initRange.size() > 0) {
			ArrayList<Integer> workColleguesIndices = determineNetworkMembers(environment, initRange, ISimulationSettings.MIN_NUMBER_OF_WORK_COLLEGUES , ISimulationSettings.MAX_NUMBER_OF_WORK_COLLEGUES);
			Network workColleguesNetwork = createNetworkForMemberIndices(workColleguesIndices);
			MasonGeometry workBuilding = determineLocationForCategory(environment, availableBuildings, ActivityCategory.WORK);
			ArrayList<MasonGeometry> otherPlaceForWorkBuildings = determineBuildingsForCategoryWithinDistance(environment, availableBuildings, workBuilding, ISimulationSettings.MAX_DISTANCE_TO_THIRD_PLACE_FOR_WORK, ActivityCategory.WORK);
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
	
	private static void initLeisureRelatedAspects(Environment environment) {
		ArrayList<Integer> initRange = geRangeOfIndividualsToInitialize();
		int networkId = 0;
		ArrayList<MasonGeometry> availableBuildings = getAvailableBuildingsForActivityCategory(environment, ActivityCategory.LEISURE);
		while (initRange.size() > 0) {
			ArrayList<Integer> friendsIndices = determineNetworkMembers(environment, initRange, ISimulationSettings.MIN_NUMBER_OF_FRIENDS, ISimulationSettings.MAX_NUMBER_OF_FRIENDS);
			Network friendsNetwork = createNetworkForMemberIndices(friendsIndices);
			MasonGeometry leisureBuilding = determineLocationForCategory(environment, availableBuildings, ActivityCategory.LEISURE);
			ArrayList<MasonGeometry> otherPlaceForLeisureBuildings = determineBuildingsForCategoryWithinDistance(environment, availableBuildings, leisureBuilding, ISimulationSettings.MAX_DISTANCE_TO_THIRD_PLACE_FOR_WORK, ActivityCategory.LEISURE);
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
					.build();
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
	
	private static ArrayList<MasonGeometry> determineBuildingsForCategoryWithinDistance(Environment environment, ArrayList<MasonGeometry> availableBuildings, MasonGeometry building, double distance, ActivityCategory activityCategory) {
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
			Bag buildingsWithinDistance = environment.getBuildingsField().getObjectsWithinDistance(building, distance);
			if (buildingsWithinDistance.size() < numberOfBuildingsConfiguredForActivityCategory) {
				throw new Exception("Too few buildings available for activity category! Increase distance or decrease number of other places for this category.");
			}
			while (buildingsForCategory.size() < numberOfBuildingsConfiguredForActivityCategory) {
				int indexOfChoosenLocation = environment.random.nextInt(buildingsWithinDistance.size());
				MasonGeometry choosenLocation = availableBuildings.remove(indexOfChoosenLocation);
				choosenLocation.addAttribute(ISimulationSettings.ATTRIBUTE_ACTIVITY_CATEGORY, activityCategory);
				buildingsForCategory.add(choosenLocation);
			}
		} 
		catch (Exception e) {
			Logger.getLogger(IndividualInitializer.class.getName()).log(Level.SEVERE, String.format("Can not determine buildings for activityCategory=%s. Adapt this method to cover it.", String.valueOf(activityCategory)), e);
		}
		return buildingsForCategory;
	}
	
	public static ArrayList<MasonGeometry> getAvailableBuildingsForActivityCategory(Environment environment, ActivityCategory activityCategory) {
		ArrayList<MasonGeometry> availableBuildings = new ArrayList<>();  
		for (Object geometry: environment.getBuildingsField().getGeometries()) {
			  if (geometry instanceof MasonGeometry) {
				  MasonGeometry mg = (MasonGeometry) geometry;
				  if (mg.getAttribute(ISimulationSettings.ATTRIBUTE_ACTIVITY_CATEGORY) == null || mg.getAttribute(ISimulationSettings.ATTRIBUTE_ACTIVITY_CATEGORY) == activityCategory) {
					  availableBuildings.add(mg);
				  }
			  }
		  }
		return availableBuildings;
	}
}
