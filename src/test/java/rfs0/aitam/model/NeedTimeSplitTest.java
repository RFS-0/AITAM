package rfs0.aitam.model;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import rfs0.aitam.model.needs.Needs;
import rfs0.aitam.model.needs.NeedTimeSplit;
import rfs0.aitam.model.needs.NeedTimeSplit.Builder;

public class NeedTimeSplitTest {

	@Test
	public void testEquateNeedTimeSplit() {
		Builder builder = new NeedTimeSplit.Builder();
		NeedTimeSplit needTimeSplit = builder
				.withNeedTimeSplit(Needs.AFFECTION, BigDecimal.valueOf(0.1))
				.withNeedTimeSplit(Needs.CREATION, BigDecimal.valueOf(0.1))
				.withNeedTimeSplit(Needs.FREEDOM, BigDecimal.valueOf(0.1))
				.withNeedTimeSplit(Needs.IDENTITY, BigDecimal.valueOf(0.1))
				.build();
		assertEquals(BigDecimal.valueOf(0.6), needTimeSplit.getTargetNeedTimeSplit().get(Needs.NOT_DEFINED));

		needTimeSplit = builder
				.withNeedTimeSplit(Needs.AFFECTION, BigDecimal.ONE)
				.withNeedTimeSplit(Needs.CREATION, BigDecimal.valueOf(0.1))
				.withNeedTimeSplit(Needs.FREEDOM, BigDecimal.valueOf(0.1))
				.withNeedTimeSplit(Needs.IDENTITY, BigDecimal.valueOf(0.1))
				.build();
		assertEquals(BigDecimal.valueOf(-0.3), needTimeSplit.getTargetNeedTimeSplit().get(Needs.NOT_DEFINED));
	}
}
