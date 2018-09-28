package rfs0.aitam.model;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import rfs0.aitam.model.needs.Need;
import rfs0.aitam.model.needs.NeedTimeSplit;
import rfs0.aitam.model.needs.NeedTimeSplit.Builder;

public class NeedTimeSplitTest {

	@Test
	public void testEquateNeedTimeSplit() {
		Builder builder = new NeedTimeSplit.Builder();
		NeedTimeSplit needTimeSplit = builder
				.withNeedTimeSplit(Need.AFFECTION, BigDecimal.valueOf(0.1))
				.withNeedTimeSplit(Need.CREATION, BigDecimal.valueOf(0.1))
				.withNeedTimeSplit(Need.FREEDOM, BigDecimal.valueOf(0.1))
				.withNeedTimeSplit(Need.IDENTITY, BigDecimal.valueOf(0.1))
				.build();
		assertEquals(BigDecimal.valueOf(0.6), needTimeSplit.getNeedTimeSplit().get(Need.NOT_DEFINED));

		needTimeSplit = builder
				.withNeedTimeSplit(Need.AFFECTION, BigDecimal.ONE)
				.withNeedTimeSplit(Need.CREATION, BigDecimal.valueOf(0.1))
				.withNeedTimeSplit(Need.FREEDOM, BigDecimal.valueOf(0.1))
				.withNeedTimeSplit(Need.IDENTITY, BigDecimal.valueOf(0.1))
				.build();
		assertEquals(BigDecimal.valueOf(-0.3), needTimeSplit.getNeedTimeSplit().get(Need.NOT_DEFINED));
	}
}
