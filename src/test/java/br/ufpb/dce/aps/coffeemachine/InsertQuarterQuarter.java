package br.ufpb.dce.aps.coffeemachine;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class InsertQuarterQuarter extends CoffeeMachineTest {
	
	private InOrder inOrder;

	@Before
	public void given() {
		inOrder = prepareScenarioWithCoins(Coin.quarter, Coin.quarter);
	}

	@Test
	public void cancelWithPossibleDifferentChange() {
		// Operation under test
		facade.cancel();

		// Verification
		verifyCancelMessage(inOrder);
		verifyReleaseCoins(inOrder, Coin.quarter, 2);
		verifyNewSession(inOrder);
	}


}
