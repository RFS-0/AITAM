package rfs0.aitam.needs;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import rfs0.aitam.need.Need;
import rfs0.aitam.need.NeedTimeSplit;
import rfs0.aitam.need.NeedTimeSplit.Builder;
import rfs0.aitam.settings.ISimulationSettings;
import rfs0.aitam.utilities.CalculationUtility;

public class NeedTimeSplitTest {

	@Test
	public void testEquateNeedTimeSplit() {
		Builder builder = new NeedTimeSplit.Builder();
		BigDecimal value = CalculationUtility.createBigDecimal(0.1);
		NeedTimeSplit needTimeSplit = builder
				.withNeedTimeSplit(Need.AFFECTION, value)
				.withNeedTimeSplit(Need.CREATION, value)
				.withNeedTimeSplit(Need.FREEDOM, value)
				.withNeedTimeSplit(Need.IDENTITY, value)
				.build();
		assertEquals(CalculationUtility.createBigDecimal(0.6), needTimeSplit.getNeedTimeSplit().get(Need.NONE));
		needTimeSplit = builder
				.withNeedTimeSplit(Need.AFFECTION, BigDecimal.ONE)
				.withNeedTimeSplit(Need.CREATION, value)
				.withNeedTimeSplit(Need.FREEDOM, value)
				.withNeedTimeSplit(Need.IDENTITY, value)
				.build();
		assertEquals(CalculationUtility.createBigDecimal(-0.3).setScale(ISimulationSettings.SCALE_USED_FOR_BIG_DECIMAL - 1), needTimeSplit.getNeedTimeSplit().get(Need.NONE));
	}
}
