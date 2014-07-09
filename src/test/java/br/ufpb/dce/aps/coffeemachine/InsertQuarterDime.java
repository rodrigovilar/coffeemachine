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
		facade.select(Button.BUTTON_1);

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
		facade.select(Button.BUTTON_1);

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
		facade.select(Button.BUTTON_3);

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
		facade.select(Button.BUTTON_2);

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
		facade.select(Button.BUTTON_1);

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
		facade.select(Button.BUTTON_1);

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
		facade.select(Button.BUTTON_3);

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
		facade.select(Button.BUTTON_2);

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
		facade.select(Button.BUTTON_4);

		// Verification
		verifyWhiteSugarPlan(inOrder);
		verifyWhiteSugarMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyNewSession(inOrder);
	}

	@Test
	public void changeRecipeAndSelectDrink() {
		// Preparing scenario: decrease water amount and insert coins
		Recipe blackRecipe = blackRecipe();
		blackRecipe.setItem(Recipe.WATER, 70.0); 
		facade.configuteDrink(Button.BUTTON_1, blackRecipe);
		inOrder = resetMocks();

		// Simulating returns
		doContainBlackIngredients();

		// Operation under test
		facade.select(Button.BUTTON_1);

		// Verification
		inOrder.verify(cupDispenser).contains(1);
		inOrder.verify(waterDispenser).contains(70.0);
		inOrder.verify(coffeePowderDispenser).contains(15.0);
		inOrder.verify(display).info(Messages.MIXING);
		inOrder.verify(coffeePowderDispenser).release(15.0);
		inOrder.verify(waterDispenser).release(70.0);
		verifyDrinkRelease(inOrder);
		verifyNewSession(inOrder);
	}

	@Test(expected=CoffeeMachineException.class)
	public void recipeWithoutPrice() {
		// Preparing scenario: recipe without price
		Recipe blackRecipe = blackRecipe();
		blackRecipe.setPriceCents(0); 

		// Operation under test
		facade.configuteDrink(Button.BUTTON_1, blackRecipe);
	}

	@Test(expected=CoffeeMachineException.class)
	public void recipeWithoutItems() {
		// Preparing scenario: recipe without items
		Recipe blackRecipe = blackRecipe();
		blackRecipe.getItems().clear();

		// Operation under test
		facade.configuteDrink(Button.BUTTON_1, blackRecipe);
	}

	@Test(expected=CoffeeMachineException.class)
	public void recipeWithUnknownItem() {
		// Preparing scenario: recipe with unknown item
		Recipe blackRecipe = blackRecipe();
		blackRecipe.setItem(Recipe.MILK, 10.0);

		// Operation under test
		facade.configuteDrink(Button.BUTTON_1, blackRecipe);
	}

}
