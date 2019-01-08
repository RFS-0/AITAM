package rfs0.aitam.individuals;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.vividsolutions.jts.planargraph.Node;

import rfs0.aitam.environment.Environment;
import rfs0.aitam.individual.Individual;
import rfs0.aitam.settings.ISimulationSettings;

public class IndividualInitializerTest {

	@Test
	public void testInitIndividuals() {
		Environment environment = new Environment(1L);
		ArrayList<Individual> allIndividuals = environment.getIndividuals();
		assertBuildingsValid(allIndividuals);
		assertPointsValid(allIndividuals);
		assertNetworksValid(allIndividuals);
	}
	
	private void assertBuildingsValid(ArrayList<Individual> allIndividuals) {
		List<Individual> invalidHomeBuildings = allIndividuals.stream()
				.filter(individual -> !(individual.getHomeNode() instanceof Node))
				.collect(Collectors.toList());
		assertEquals(0, invalidHomeBuildings.size());
		List<Individual> invalidWorkPlaces = allIndividuals.stream()
				.filter(individual -> !(individual.getWorkPlaceNode() instanceof Node))
				.collect(Collectors.toList());
		assertEquals(0, invalidWorkPlaces.size());
		List<Individual> invalidPlacesForLeisure = allIndividuals.stream()
				.filter(individual -> !(individual.getLeisureNode() instanceof Node))
				.collect(Collectors.toList());
		assertEquals(0, invalidPlacesForLeisure.size());
	}
	
	private void assertPointsValid(ArrayList<Individual> allIndividuals) {
		List<Individual> currentLocationPoints = allIndividuals.stream()
				.filter(individual -> !(individual.getCurrentNode() instanceof Node))
				.collect(Collectors.toList());
		assertEquals(0, currentLocationPoints.size());
		List<Individual> invalidWorkPlacePoints = allIndividuals.stream()
				.filter(individual -> !(individual.getWorkPlaceNode() instanceof Node))
				.collect(Collectors.toList());
		assertEquals(0, invalidWorkPlacePoints.size());
		List<Individual> invalidPlacesForLeisurePoints = allIndividuals.stream()
				.filter(individual -> !(individual.getLeisureNode() instanceof Node))
				.collect(Collectors.toList());
		assertEquals(0, invalidPlacesForLeisurePoints.size());
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
