package rfs0.aitam.model;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import rfs0.aitam.model.NeedTimeSplit.Builder;

public class NeedTimeSplitTest {

	@Test
	public void testEquateNeedTimeSplit() {
		Builder builder = new NeedTimeSplit.Builder();
		NeedTimeSplit needTimeSplit = builder.addNeedTimeSplit(Needs.AFFECTION, BigDecimal.valueOf(0.1))
				.addNeedTimeSplit(Needs.CREATION, BigDecimal.valueOf(0.1))
				.addNeedTimeSplit(Needs.FREEDOM, BigDecimal.valueOf(0.1))
				.addNeedTimeSplit(Needs.IDENTITY, BigDecimal.valueOf(0.1))
				.build();
		assertEquals(BigDecimal.valueOf(0.6), needTimeSplit.getNeedTimeSplit().get(Needs.NOT_DEFINED));

		needTimeSplit = builder.addNeedTimeSplit(Needs.AFFECTION, BigDecimal.ONE)
				.addNeedTimeSplit(Needs.CREATION, BigDecimal.valueOf(0.1))
				.addNeedTimeSplit(Needs.FREEDOM, BigDecimal.valueOf(0.1))
				.addNeedTimeSplit(Needs.IDENTITY, BigDecimal.valueOf(0.1))
				.build();
		assertEquals(BigDecimal.valueOf(-0.3), needTimeSplit.getNeedTimeSplit().get(Needs.NOT_DEFINED));
	}
}
