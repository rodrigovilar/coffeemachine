package br.ufpb.dce.aps.coffeemachine;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class FacadeCreated extends CoffeeMachineTest {
	
	private InOrder inOrder;

	@Before
	public void given() {
		facade = createFacade(factory);
		inOrder = resetMocks();
	}

	@Test
	public void insertCoin() {
		// Operation under test
		facade.insertCoin(Coin.dime);

		// Verification
		verifySessionMoney("0.10");
	}

	@Test(expected = CoffeeMachineException.class)
	public void nullCoin() {
		// Operation under test
		facade.insertCoin(null);
	}

	@Test(expected = CoffeeMachineException.class)
	public void cancelWithoutCoins() {
		// Operation under test
		facade.cancel();
	}
	
	@Test
	public void selectBlackWithoutMoney1() {
		// Simulating returns
		doContainBlackIngredients();

		// Operation under test
		facade.select(Drink.BLACK);

		// Verification
		inOrder.verify(display).warn(Messages.NO_ENOUGHT_MONEY);
		verifyCloseSession(inOrder);

	}

	@Test
	public void readBadgeCode() {
		// Operation under test
		facade.readBadge(123456);

		// Verification
		verifyBadgeRead();
	}

}
