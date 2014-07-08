package br.ufpb.dce.aps.coffeemachine;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.times;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class InsertHalfDollar extends CoffeeMachineTest {
	
	private InOrder inOrder;

	@Before
	public void given() {
		inOrder = prepareScenarioWithCoins(Coin.halfDollar);
	}

	@Test
	public void cancelWithOneCoin() {
		// Operation under test
		facade.cancel();

		// Verification
		verifyCancel(inOrder, Coin.halfDollar);
	}

	@Test
	public void selectBlackWithoutSugar() {
		// Simulating returns
		doContain(coffeePowderDispenser, anyDouble());
		doContain(waterDispenser, anyDouble());
		doContain(cupDispenser, 1);
		doNotContain(sugarDispenser, anyDouble()); // Out of Sugar

		// Operation under test
		facade.select(Button.BUTTON_3);

		// Verification
		inOrder.verify(cupDispenser).contains(1);
		inOrder.verify(waterDispenser).contains(anyDouble());
		inOrder.verify(coffeePowderDispenser).contains(anyDouble());
		inOrder.verify(sugarDispenser).contains(anyDouble());
		verifyOutOfIngredient(inOrder, Messages.OUT_OF_SUGAR, Coin.halfDollar);
	}

	@Test
	public void selectWhiteSugarWithChange() {
		// Simulating returns
		doCount(Coin.dime, 10);
		doCount(Coin.nickel, 10);
		doContainWhiteSugarIngredients();

		// Operation under test
		facade.select(Button.BUTTON_4);

		// Verification
		verifyWhiteSugarPlan(inOrder);
		verifyCount(inOrder, Coin.dime, Coin.nickel);
		verifyWhiteSugarMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyCloseSession(inOrder, Coin.dime, Coin.nickel);
	}

	@Test
	public void selectBlackWithoutEnoughtChange() {
		// Simulating returns
		doCount(Coin.dime, 0);
		doCount(Coin.nickel, 0);
		doCount(Coin.penny, 0); // Out of change
		doContainWhiteIngredients();

		// Operation under test
		facade.select(Button.BUTTON_2);

		// Verification
		verifyWhitePlan(inOrder);
		verifyCount(inOrder, Coin.dime, Coin.nickel, Coin.penny);
		inOrder.verify(display).warn(Messages.NO_ENOUGHT_CHANGE);
		verifyCloseSession(inOrder, Coin.halfDollar);
	}

	@Test
	public void selectWhiteWithNonTrivialChange() {
		// Simulating returns
		doCount(Coin.dime, 0);
		doCount(Coin.nickel, 10);
		doContainWhiteIngredients();

		// Operation under test
		facade.select(Button.BUTTON_2);

		// Verification
		verifyWhitePlan(inOrder);
		verifyCount(inOrder, Coin.dime, Coin.nickel);
		verifyWhiteMix(inOrder);
		verifyDrinkRelease(inOrder);

		inOrder.verify(cashBox, times(3)).release(Coin.nickel);
		verifyNewSession(inOrder);
	}

	@Test
	public void selectWhiteWithoutCreamer() {
		// Simulating returns
		doContain(coffeePowderDispenser, anyDouble());
		doContain(waterDispenser, anyDouble());
		doContain(cupDispenser, 1);
		doNotContain(creamerDispenser, anyDouble()); // Out of Creamer!

		// Operation under test
		facade.select(Button.BUTTON_2);

		// Verification
		inOrder.verify(cupDispenser).contains(1);
		inOrder.verify(waterDispenser).contains(anyDouble());
		inOrder.verify(coffeePowderDispenser).contains(anyDouble());
		inOrder.verify(creamerDispenser).contains(anyDouble());
		verifyOutOfIngredient(inOrder, Messages.OUT_OF_CREAMER, Coin.halfDollar);
	}
	
	@Test
	public void selectBouillonWithChange() {
		// Simulating returns
		doCount(Coin.quarter, 10);
		doContainBouillonIngredients();

		// Operation under test
		facade.select(Button.BUTTON_5);

		// Verification
		verifyBouillonPlan(inOrder);
		verifyCount(inOrder, Coin.quarter);
		verifyBouillonMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyCloseSession(inOrder, Coin.quarter);
	}
	
	@Test
	public void insertCoins() {
		// Operation under test
		facade.insertCoin(Coin.nickel);

		// Verification
		verifySessionMoney("0.55");
	}

}
