package br.ufpb.dce.aps.coffeemachine;

import static org.mockito.Matchers.anyDouble;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class InsertQuarter extends CoffeeMachineTest {
	
	private InOrder inOrder;

	@Before
	public void given() {
		inOrder = prepareScenarioWithCoins(Coin.quarter);
	}

	@Test
	public void selectBouillonWithoutChange() {
		// Simulating returns
		doContainBouillonIngredients();

		// Operation under test
		facade.select(Drink.BOUILLON);

		// Verification
		verifyBouillonPlan(inOrder);
		verifyBouillonMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyNewSession(inOrder);
	}

	@Test
	public void selectBouillonWithoutBouillonPowder() {
		// Simulating returns
		doNotContain(bouillonDispenser, anyDouble()); // Out of Bouillon
															// powder!
		doContain(waterDispenser, anyDouble());
		doContain(cupDispenser, 1);

		// Operation under test
		facade.select(Drink.BOUILLON);

		// Verification
		inOrder.verify(cupDispenser).contains(1);
		inOrder.verify(waterDispenser).contains(100);
		inOrder.verify(bouillonDispenser).contains(10);
		verifyOutOfIngredient(inOrder, Messages.OUT_OF_BOUILLON_POWDER,
				Coin.quarter);
	}
	


}
