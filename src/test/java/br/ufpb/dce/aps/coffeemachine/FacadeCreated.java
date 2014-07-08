package br.ufpb.dce.aps.coffeemachine;

import static org.mockito.Mockito.verify;

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
		facade.select(Button.BUTTON_1);

		// Verification
		inOrder.verify(display).warn(Messages.NO_ENOUGHT_MONEY);
		verifyCloseSession(inOrder);

	}

	@Test
	public void readBadgeCode() {
		// Operation under test
		facade.readBadge(123456);

		// Verification
		verifyBadgeRead(inOrder);
	}
	
	@Test
	public void cancelWithPossibleDifferentChange() {
		//Given
		inOrder = prepareScenarioWithCoins(Coin.quarter, Coin.quarter);
		
		// Operation under test
		facade.cancel();

		// Verification
		verifyCancelMessage(inOrder);
		verifyReleaseCoins(inOrder, Coin.quarter, 2);
		verifyNewSession(inOrder);
	}

	@Test
	public void cancelAndDrink() {
		// Preparing scenario: cancel
		insertCoins(Coin.halfDollar);
		facade.cancel();
		
		// Preparing scenario: before select
		insertCoins(Coin.dime, Coin.quarter);
		inOrder = resetMocks();

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

	@Test
	public void changeDrinkPrice() {
		// Operation under test
		facade.setPrice(Button.BUTTON_1, 30);

		// Verification
		verify(buttonDisplay).show("Black: $0.30", "White: $0.35",
				"Black with sugar: $0.35", "White with sugar: $0.35",
				"Bouillon: $0.25", null, null);
	}

	@Test
	public void changeDrinkPriceAndSelectDrink() {
		// Preparing scenario: change black price and insert coins
		facade.setPrice(Button.BUTTON_1, 30);
		insertCoins(Coin.dime, Coin.dime, Coin.dime);
		inOrder = resetMocks();
		
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
