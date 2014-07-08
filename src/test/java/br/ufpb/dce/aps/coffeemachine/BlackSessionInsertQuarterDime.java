package br.ufpb.dce.aps.coffeemachine;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class BlackSessionInsertQuarterDime extends CoffeeMachineTest {
	
	private InOrder inOrder;

	@Before
	public void given() {
		facade = createFacade(factory);

		// Preparing scenario: first drink
		validSession(Button.BUTTON_1, Coin.dime, Coin.quarter);

		// Preparing scenario: second drink
		insertCoins(Coin.quarter, Coin.dime);
		inOrder = resetMocks();
	}

	@Test
	public void twoDrinks() {
		// Simulating returns
		doContainBlackSugarIngredients();

		// Operation under test
		facade.select(Button.BUTTON_3);

		// Verification
		verifyBlackSugarPlan(inOrder);
		verifyBlackSugarMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyNewSession(inOrder);
	}

	@Test
	public void drinkAndCancel() {
		// Operation under test
		facade.cancel();

		// Verification
		verifyCancel(inOrder, Coin.quarter, Coin.dime);
	}
	

}
