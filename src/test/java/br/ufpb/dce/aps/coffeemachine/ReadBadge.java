package br.ufpb.dce.aps.coffeemachine;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class ReadBadge extends CoffeeMachineTest {
	
	private InOrder inOrder;

	@Before
	public void given() {
		facade = createFacade(factory);
		facade.readBadge(123456);
		inOrder = resetMocks();
	}

	@Test
	public void insertCoin() {
		// Operation under test
		insertCoins(Coin.dime);

		// Verification
		verifyCannotInsertCoinsMessage(inOrder);
		verifyReleaseCoins(inOrder, Coin.dime);
	}
	
	@Test
	public void selectBlack() {
		// Simulating returns
		doContainBlackIngredients();
		doAcceptBadgeCode();
		
		// Operation under test
		facade.select(Drink.BLACK);

		// Verification
		verifyBlackPlan(inOrder);
		verifyPayrollDebit(inOrder, 35, 123456);
		verifyBlackMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyNewSession(inOrder);
	}

}
