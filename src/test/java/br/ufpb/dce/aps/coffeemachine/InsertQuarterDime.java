package br.ufpb.dce.aps.coffeemachine;

import static org.mockito.Matchers.anyDouble;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class InsertQuarterDime extends CoffeeMachineTest {
	
	private InOrder inOrder;

	@Before
	public void given() {
		inOrder = prepareScenarioWithCoins(Coin.quarter, Coin.dime);
	}

	@Test
	public void selectBlackWithoutCoffeePowder() {
		// Simulating returns
		doNotContain(coffeePowderDispenser, anyDouble()); // Out of Coffee
															// powder!
		doContain(waterDispenser, anyDouble());
		doContain(cupDispenser, 1);

		// Operation under test
		facade.select(Drink.BLACK);

		// Verification
		inOrder.verify(cupDispenser).contains(1);
		inOrder.verify(waterDispenser).contains(anyDouble());
		inOrder.verify(coffeePowderDispenser).contains(anyDouble());
		verifyOutOfIngredient(inOrder, Messages.OUT_OF_COFFEE_POWDER,
				Coin.quarter, Coin.dime);
	}

	@Test
	public void selectBlackWithoutWater() {
		// Simulating returns
		doNotContain(waterDispenser, anyDouble()); // Out of Water
		doContain(cupDispenser, 1);

		// Operation under test
		facade.select(Drink.BLACK);

		// Verification
		inOrder.verify(cupDispenser).contains(1);
		inOrder.verify(waterDispenser).contains(anyDouble());
		verifyOutOfIngredient(inOrder, Messages.OUT_OF_WATER, Coin.quarter,
				Coin.dime);
	}

	@Test
	public void selectBlackWithoutCup() {
		// Simulating returns
		doNotContain(cupDispenser, 1); // Out of Cup

		// Operation under test
		facade.select(Drink.BLACK_SUGAR);

		// Verification
		inOrder.verify(cupDispenser).contains(1);
		verifyOutOfIngredient(inOrder, Messages.OUT_OF_CUP, Coin.quarter,
				Coin.dime);
	}

	@Test
	public void selectWhiteWithoutChange() {
		// Simulating returns
		doContainWhiteIngredients();

		// Operation under test
		facade.select(Drink.WHITE);

		// Verification
		verifyWhitePlan(inOrder);
		verifyWhiteMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyNewSession(inOrder);
	}

	@Test
	public void selectBlackWithoutChange() {
		// Simulating returns
		doContainBlackIngredients();

		// Operation under test
		facade.select(Drink.BLACK);

		// Verification
		verifyBlackPlan(inOrder);
		verifyBlackMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyNewSession(inOrder);
	}

	@Test
	public void cancelWithTwoCoins() {
		// Operation under test
		facade.cancel();

		// Verification
		verifyCancel(inOrder, Coin.quarter, Coin.dime);
	}
	
	@Test
	public void blackIngredientsQuantities() {
		// Simulating returns
		doContainBlackIngredients();

		// Operation under test
		facade.select(Drink.BLACK);

		// Verification
		verifyBlackPlan(inOrder);
		verifyBlackMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyNewSession(inOrder);
	}

	@Test
	public void blackSugarIngredientsQuantities() {
		// Simulating returns
		doContainBlackSugarIngredients();

		// Operation under test
		facade.select(Drink.BLACK_SUGAR);

		// Verification
		verifyBlackSugarPlan(inOrder);
		verifyBlackSugarMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyNewSession(inOrder);
	}

	@Test
	public void whiteIngredientsQuantities() {
		// Simulating returns
		doContainWhiteIngredients();

		// Operation under test
		facade.select(Drink.WHITE);

		// Verification
		verifyWhitePlan(inOrder);
		verifyWhiteMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyNewSession(inOrder);
	}

	@Test
	public void whiteSugarIngredientsQuantities() {
		// Simulating returns
		doContainWhiteSugarIngredients();

		// Operation under test
		facade.select(Drink.WHITE_SUGAR);

		// Verification
		verifyWhiteSugarPlan(inOrder);
		verifyWhiteSugarMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyNewSession(inOrder);
	}

}
