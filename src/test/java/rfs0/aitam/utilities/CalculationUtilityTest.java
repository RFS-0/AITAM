package rfs0.aitam.utilities;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import rfs0.aitam.activities.ActivityAgenda;
import rfs0.aitam.model.needs.Need;
import rfs0.aitam.model.needs.NeedTimeSplit;
import rfs0.aitam.settings.ISimulationSettings;

public class CalculationUtilityTest {
	
	@Test
	public void testCreate() {
		BigDecimal fromInt = CalculationUtility.createBigDecimal(1);
		BigDecimal fromDouble = CalculationUtility.createBigDecimal(1.0);
		assertEquals(ISimulationSettings.SCALE_USED_FOR_BIG_DECIMAL, fromInt.scale());
		assertEquals(ISimulationSettings.SCALE_USED_FOR_BIG_DECIMAL, fromDouble.scale());
	}
	
	@Test
	public void testMathContext() {
		assertEquals(ISimulationSettings.PRECISION_USED_FOR_BIG_DECIMAL, CalculationUtility.MATH_CONTEXT.getPrecision());
		assertEquals(ISimulationSettings.ROUNDING_MODE_USED_FOR_BIG_DECIMAL, CalculationUtility.MATH_CONTEXT.getRoundingMode());
	}
	
	@Test
	public void testSum() {
		List<BigDecimal> values = Stream.of(
				CalculationUtility.createBigDecimal(0.25001), 
				CalculationUtility.createBigDecimal(0.25), 
				CalculationUtility.createBigDecimal(0.25), 
				CalculationUtility.createBigDecimal(0.25))
				.collect(Collectors.toList());
		assertEquals(CalculationUtility.createBigDecimal(1.00001).setScale(ISimulationSettings.SCALE_USED_FOR_BIG_DECIMAL - 1), CalculationUtility.sum(values)); // issue with precision of 6 is that scale will be less than 6 once we deal with numbers >= 1
	}
	
	@Test
	public void testDivide() {
		assertEquals(BigDecimal.ONE, CalculationUtility.divide(BigDecimal.TEN, BigDecimal.TEN));
		assertEquals(BigDecimal.valueOf(0.333333), CalculationUtility.divide(BigDecimal.ONE, CalculationUtility.THREE));
		
	}
	
	@Test
	public void testMultiply() {
		assertEquals(CalculationUtility.createBigDecimal(1.5).setScale(ISimulationSettings.SCALE_USED_FOR_BIG_DECIMAL - 1), CalculationUtility.multiply(CalculationUtility.createBigDecimal(0.75), CalculationUtility.createBigDecimal(2))); // issue with precision of 6 is that scale will be less than 6 once we deal with numbers >= 1
	}
	
	@Test
	public void testCalculateMeanSquaredError() {
		ActivityAgenda activityAgenda = new ActivityAgenda();
		HashMap<Need,BigDecimal> relativeNeedTimeSplit = activityAgenda.getAbsoluteNeedTimeSplit().getRelativeNeedTimeSplit();
		relativeNeedTimeSplit.put(Need.AFFECTION, CalculationUtility.createBigDecimal(0.15));
		relativeNeedTimeSplit.put(Need.CREATION, CalculationUtility.createBigDecimal(0.05));
		relativeNeedTimeSplit.put(Need.FREEDOM, CalculationUtility.createBigDecimal(0.25));
		NeedTimeSplit targetNeedTimeSplit = new NeedTimeSplit.Builder()
				.withNeedTimeSplit(Need.AFFECTION, CalculationUtility.createBigDecimal(0.10))
				.withNeedTimeSplit(Need.CREATION, CalculationUtility.createBigDecimal(0.10))
				.withNeedTimeSplit(Need.FREEDOM, CalculationUtility.createBigDecimal(0.10))
				.build();
		assertEquals(BigDecimal.valueOf(0.0275000).setScale(7), CalculationUtility.calculateMeanSquaredError(activityAgenda.getAbsoluteNeedTimeSplit(), targetNeedTimeSplit));
	}
}
