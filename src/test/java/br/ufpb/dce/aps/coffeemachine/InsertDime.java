package br.ufpb.dce.aps.coffeemachine;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class InsertDime extends CoffeeMachineTest {
	
	private InOrder inOrder;

	@Before
	public void given() {
		inOrder = prepareScenarioWithCoins(Coin.dime);
	}

	@Test
	public void selectBlackWithoutMoney2() {
		// Simulating returns
		doContainBlackIngredients();

		// Operation under test
		facade.select(Drink.BLACK);

		// Verification
		inOrder.verify(display).warn(Messages.NO_ENOUGHT_MONEY);
		verifyCloseSession(inOrder, Coin.dime);
	}


}
