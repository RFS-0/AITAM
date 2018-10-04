package rfs0.aitam.individuals;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import individuals.Individual;
import individuals.IndividualInitializer;
import rfs0.aitam.commons.ISimulationSettings;
import rfs0.aitam.model.Environment;
import sim.util.geo.MasonGeometry;

public class IndividualInitializerTest {

	@Test
	public void testInitIndividuals() {
		Environment environment = new Environment(1L);
		ArrayList<Individual> allIndividuals = IndividualInitializer.initIndividuals(environment);
		assertBuildingsValid(allIndividuals);
		assertPointsValid(allIndividuals);
		assertNetworksValid(allIndividuals);
	}
	
	private void assertBuildingsValid(ArrayList<Individual> allIndividuals) {
		List<Individual> invalidHomeBuildings = allIndividuals.stream()
				.filter(individual -> individual.getHomeBuilding() == null)
				.filter(individual -> !(individual.getHomeBuilding() instanceof MasonGeometry))
				.collect(Collectors.toList());
		assertEquals(0, invalidHomeBuildings.size());
		List<Individual> invalidThirdPlaceForHouseholdAndFamilyCareBuilding = allIndividuals.stream()
				.filter(individual -> individual.getThirdPlaceForHouseholdAndFamilyCareBuilding() == null)
				.filter(individual -> !(individual.getThirdPlaceForHouseholdAndFamilyCareBuilding() instanceof MasonGeometry))
				.collect(Collectors.toList());
		assertEquals(0, invalidThirdPlaceForHouseholdAndFamilyCareBuilding.size());
		List<Individual> invalidWorkPlaces = allIndividuals.stream()
				.filter(individual -> individual.getWorkPlaceBuilding() == null)
				.filter(individual -> !(individual.getWorkPlaceBuilding() instanceof MasonGeometry))
				.collect(Collectors.toList());
		assertEquals(0, invalidWorkPlaces.size());
		List<Individual> invalidThirdPlacesForWork = allIndividuals.stream()
				.filter(individual -> individual.getThirdPlaceForWorkBuilding() == null)
				.filter(individual -> !(individual.getThirdPlaceForWorkBuilding() instanceof MasonGeometry))
				.collect(Collectors.toList());
		assertEquals(0, invalidThirdPlacesForWork.size());
		List<Individual> invalidPlacesForLeisure = allIndividuals.stream()
				.filter(individual -> individual.getLeisureBuilding() == null)
				.filter(individual -> !(individual.getLeisureBuilding() instanceof MasonGeometry))
				.collect(Collectors.toList());
		assertEquals(0, invalidPlacesForLeisure.size());
		List<Individual> invalidThirdPlaceForLeisureBuilding = allIndividuals.stream()
				.filter(individual -> individual.getThirdPlaceForLeisureBuilding() == null)
				.filter(individual -> !(individual.getThirdPlaceForLeisureBuilding() instanceof MasonGeometry))
				.collect(Collectors.toList());
		assertEquals(0, invalidThirdPlaceForLeisureBuilding.size());
	}
	
	private void assertPointsValid(ArrayList<Individual> allIndividuals) {
		List<Individual> currentLocationPoints = allIndividuals.stream()
				.filter(individual -> individual.getCurrentLocation() == null)
				.filter(individual -> !(individual.getCurrentLocation() instanceof MasonGeometry))
				.filter(individual -> individual.getCurrentLocation().isMovable == false)
				.collect(Collectors.toList());
		assertEquals(0, currentLocationPoints.size());
		List<Individual> invalidThirdPlacesForHouseholdAndFamilyCarePoints = allIndividuals.stream()
				.filter(individual -> individual.getThirdPlaceForHouseholdAndFamilyCarePoint() == null)
				.filter(individual -> !(individual.getThirdPlaceForHouseholdAndFamilyCarePoint() instanceof MasonGeometry))
				.filter(individual -> individual.getThirdPlaceForHouseholdAndFamilyCarePoint().isMovable == true)
				.collect(Collectors.toList());
		assertEquals(0, invalidThirdPlacesForHouseholdAndFamilyCarePoints.size());
		List<Individual> invalidWorkPlacePoints = allIndividuals.stream()
				.filter(individual -> individual.getWorkPlacePoint() == null)
				.filter(individual -> !(individual.getWorkPlacePoint() instanceof MasonGeometry))
				.filter(individual -> individual.getWorkPlacePoint().isMovable == true)
				.collect(Collectors.toList());
		assertEquals(0, invalidWorkPlacePoints.size());
		List<Individual> invalidThidPlacesForWorkPoints = allIndividuals.stream()
				.filter(individual -> individual.getThirdPlaceForWorkPoint() == null)
				.filter(individual -> !(individual.getThirdPlaceForWorkPoint() instanceof MasonGeometry))
				.filter(individual -> individual.getThirdPlaceForWorkPoint().isMovable == true)
				.collect(Collectors.toList());
		assertEquals(0, invalidThidPlacesForWorkPoints.size());
		List<Individual> invalidPlacesForLeisurePoints = allIndividuals.stream()
				.filter(individual -> individual.getLeisurePoint() == null)
				.filter(individual -> !(individual.getLeisurePoint() instanceof MasonGeometry))
				.filter(individual -> individual.getLeisurePoint().isMovable == true)
				.collect(Collectors.toList());
		assertEquals(0, invalidPlacesForLeisurePoints.size());
		List<Individual> invalidThirdPlacesForLeisurePoints = allIndividuals.stream()
				.filter(individual -> individual.getThirdPlaceForLeisurePoint() == null)
				.filter(individual -> !(individual.getThirdPlaceForLeisurePoint() instanceof MasonGeometry))
				.filter(individual -> individual.getThirdPlaceForLeisurePoint().isMovable == true)
				.collect(Collectors.toList());
		assertEquals(0, invalidThirdPlacesForLeisurePoints.size());
	}
	
	private void assertNetworksValid(ArrayList<Individual> allIndividuals) {
		List<Individual> invalidHouseholdNetworkIds = allIndividuals.stream()
				.filter(individual -> individual.getHouseholdMembersNetworkId() == -1)
				.collect(Collectors.toList());
		assertEquals(0, invalidHouseholdNetworkIds.size());
		List<Individual> invalidHouseholdNetworks = allIndividuals.stream()
				.filter(individual -> individual.getHouseholdMembersNetwork().allNodes.size() < ISimulationSettings.MIN_NUMBER_OF_HOUSEHOLD_MEMBERS)
				.filter(individual -> individual.getHouseholdMembersNetwork().allNodes.size() > ISimulationSettings.MAX_NUMBER_OF_HOUSEHOLD_MEMBERS)
				.collect(Collectors.toList());
		assertEquals(0, invalidHouseholdNetworks.size());
		List<Individual> invalidWorkColleguesNetworkIds = allIndividuals.stream()
				.filter(individual -> individual.getWorkColleguesNetworkId() == -1)
				.collect(Collectors.toList());
		assertEquals(0, invalidWorkColleguesNetworkIds.size());
		List<Individual> invalidWorkColleguesNetworks = allIndividuals.stream()
				.filter(individual -> individual.getHouseholdMembersNetwork().allNodes.size() < ISimulationSettings.MIN_NUMBER_OF_WORK_COLLEGUES)
				.filter(individual -> individual.getHouseholdMembersNetwork().allNodes.size() > ISimulationSettings.MAX_NUMBER_OF_WORK_COLLEGUES)
				.collect(Collectors.toList());
		assertEquals(0, invalidWorkColleguesNetworks.size());
		List<Individual> invalidFriendsNetworkIds = allIndividuals.stream()
				.filter(individual -> individual.getFriendsNetworkId() == -1)
				.collect(Collectors.toList());
		assertEquals(0, invalidFriendsNetworkIds.size());
		List<Individual> invalidFriendsNetworks = allIndividuals.stream()
				.filter(individual -> individual.getHouseholdMembersNetwork().allNodes.size() < ISimulationSettings.MIN_NUMBER_OF_FRIENDS)
				.filter(individual -> individual.getHouseholdMembersNetwork().allNodes.size() > ISimulationSettings.MAX_NUMBER_OF_FRIENDS)
				.collect(Collectors.toList());
		assertEquals(0, invalidFriendsNetworks.size());
	}
}
