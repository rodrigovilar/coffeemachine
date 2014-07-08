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
		// Given
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
		Recipe blackRecipe = blackRecipe();
		blackRecipe.setPriceCents(30);
		
		// Operation under test
		facade.configuteDrink(Button.BUTTON_1, blackRecipe);

		// Verification
		verify(buttonDisplay).show("Black: $0.30", "White: $0.35",
				"Black with sugar: $0.35", "White with sugar: $0.35",
				"Bouillon: $0.25", null, null);
	}

	@Test
	public void changeDrinkPriceAndSelectDrink() {
		Recipe blackRecipe = blackRecipe();
		blackRecipe.setPriceCents(30);
		
		// Preparing scenario: change black price and insert coins
		facade.configuteDrink(Button.BUTTON_1, blackRecipe);
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

	@Test
	public void changeRecipe() {
		// Operation under test
		Recipe blackRecipe = blackRecipe();
		blackRecipe.addItem(Recipe.WATER, 70.0); // Decrease water amount
		facade.configuteDrink(Button.BUTTON_1, blackRecipe);

		// Verification
		verify(buttonDisplay).show("Black: $0.35", "White: $0.35",
				"Black with sugar: $0.35", "White with sugar: $0.35",
				"Bouillon: $0.25", null, null);
	}

	protected Recipe blackRecipe() {
		Recipe recipe = new Recipe();
		recipe.setName("Black");
		recipe.setPriceCents(35);

		recipe.addItem(Recipe.WATER, 70.0);
		recipe.addItem(Recipe.COFFEE_POWDER, 15.0);

		return recipe;
	}

}
