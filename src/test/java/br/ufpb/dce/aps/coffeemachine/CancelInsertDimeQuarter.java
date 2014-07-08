package br.ufpb.dce.aps.coffeemachine;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class CancelInsertDimeQuarter extends CoffeeMachineTest {
	
	private InOrder inOrder;

	@Before
	public void given() {
		facade = createFacade(factory);

		// Preparing scenario: cancel
		insertCoins(Coin.halfDollar);
		facade.cancel();

		// Preparing scenario: before select
		insertCoins(Coin.dime, Coin.quarter);
		inOrder = resetMocks();
	}

	@Test
	public void cancelAndDrink() {
		// Simulating returns
		doContainBlackIngredients();

		// Operation under test
		facade.select(Button.BUTTON_1);

		// Verification
		verifyBlackPlan(inOrder);
		verifyBlackMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyNewSession(inOrder);
	}
}
