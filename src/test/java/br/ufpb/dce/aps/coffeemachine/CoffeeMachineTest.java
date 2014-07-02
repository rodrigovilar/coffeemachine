package br.ufpb.dce.aps.coffeemachine;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public abstract class CoffeeMachineTest {

	private ComponentsFactory factory;
	private CoffeeMachine facade;

	private Display display;
	private CashBox cashBox;
	private Dispenser coffeePowderDispenser;
	private Dispenser waterDispenser;
	private Dispenser cupDispenser;
	private DrinkDispenser drinkDispenser;
	private Dispenser sugarDispenser;

	protected abstract CoffeeMachine createFacade(ComponentsFactory factory);

	@Before
	public void init() {
		factory = new MockComponentsFactory();
		display = factory.getDisplay();
		cashBox = factory.getCashBox();
		coffeePowderDispenser = factory.getCoffeePowderDispenser();
		waterDispenser = factory.getWaterDispenser();
		cupDispenser = factory.getCupDispenser();
		drinkDispenser = factory.getDrinkDispenser();
		sugarDispenser = factory.getSugarDispenser();
	}
	
	@After
	public void genericVerifications() {
		verifyNoMoreInteractions(mocks());
	}


	@Test
	public void createFacade() {
		// Operation under test
		facade = createFacade(factory);

		// Verification
		verifyNewSession(null);
	}

	private void verifyNewSession(InOrder inOrder) {
		if (inOrder == null) {
			verify(display).info(Messages.INSERT_COINS);
		} else {
			inOrder.verify(display).info(Messages.INSERT_COINS);
		}
	}

	@Test
	public void insertCoin() {
		// Preparing scenario
		facade = createFacade(factory);
		resetMocks();

		// Operation under test
		facade.insertCoin(Coin.dime);

		// Verification
		verifySessionMoney("0.10");
	}

	private void verifySessionMoney(String value) {
		verify(display).info("Total: US$ " + value);
	}

	@Test
	public void insertCoins() {
		// Preparing scenario
		facade = createFacade(factory);
		facade.insertCoin(Coin.halfDollar);
		resetMocks();

		// Operation under test
		facade.insertCoin(Coin.nickel);

		// Verification
		verifySessionMoney("0.55");
	}

	@Test(expected = CoffeeMachineException.class)
	public void nullCoin() {
		// Preparing scenario
		facade = createFacade(factory);
		resetMocks();

		// Operation under test
		facade.insertCoin(null);
	}

	@Test(expected = CoffeeMachineException.class)
	public void cancelWithoutCoins() {
		// Preparing scenario
		facade = createFacade(factory);
		resetMocks();

		// Operation under test
		facade.cancel();
	}

	@Test
	public void cancelWithOneCoin() {
		InOrder inOrder = prepareScenarioWithCoins(Coin.halfDollar);

		// Operation under test
		facade.cancel();

		// Verification
		verifyCancel(inOrder, Coin.halfDollar);
	}

	private InOrder prepareScenarioWithCoins(Coin... coins) {
		facade = createFacade(factory);
		insertCoins(coins);
		return resetMocks();
	}

	@Test
	public void cancelWithTwoCoins() {
		InOrder inOrder = prepareScenarioWithCoins(Coin.penny, Coin.nickel);

		// Operation under test
		facade.cancel();

		// Verification
		verifyCancel(inOrder, Coin.nickel, Coin.penny);
	}

	@Test
	public void cancelWithPossibleDifferentChange() {
		InOrder inOrder = prepareScenarioWithCoins(Coin.quarter, Coin.quarter);

		// Operation under test
		facade.cancel();

		// Verification
		verifyCancelMessage(inOrder);
		verifyReleaseCoins(inOrder, Coin.quarter, 2);
		verifyNewSession(inOrder);
	}

	@Test
	public void selectBlackWithoutChange() {
		InOrder inOrder = prepareScenarioWithCoins(Coin.quarter, Coin.dime);

		// Simulating returns
		doContainBlackIngredients();

		// Operation under test
		facade.select(Drink.BLACK);

		// Verification
		verifyBlackPlan(inOrder);
		verifyBlackMix(inOrder);
		verifyDrinkRelease(inOrder);
	}

	private void doContainBlackIngredients() {
		doContain(coffeePowderDispenser, anyDouble());
		doContain(waterDispenser, anyDouble());
		doContain(cupDispenser, 1);
	}

	private void verifyBlackPlan(InOrder inOrder) {
		inOrder.verify(cupDispenser).contains(1);
		inOrder.verify(waterDispenser).contains(anyDouble());
		inOrder.verify(coffeePowderDispenser).contains(anyDouble());
	}

	private void verifyBlackMix(InOrder inOrder) {
		inOrder.verify(display).info(Messages.MIXING);
		inOrder.verify(coffeePowderDispenser).release(anyDouble());
		inOrder.verify(waterDispenser).release(anyDouble());
	}
	
	@Test
	public void twoDrinks() {
		// Preparing scenario: first drink
		validSession(Drink.BLACK, Coin.dime, Coin.quarter);

		// Preparing scenario: second drink
		insertCoins(Coin.dime, Coin.quarter);
		InOrder inOrder = resetMocks();
		
		// Simulating returns
		doContainBlackSugarIngredients();
		
		// Operation under test
		facade.select(Drink.BLACK_SUGAR);
		
		// Verification
		verifyBlackSugarPlan(inOrder);
		verifyBlackSugarMix(inOrder);
		verifyDrinkRelease(inOrder);
	}

	@SuppressWarnings("incomplete-switch")
	private void validSession(Drink drink, Coin... coins) {
		facade = createFacade(factory);
		insertCoins(coins);
		
		switch (drink) {
		case BLACK:
			doContainBlackIngredients();
			break;
		case BLACK_SUGAR:
			doContainBlackSugarIngredients();
			break;
		}
		
		facade.select(drink);
	}
	
	private void doContainBlackSugarIngredients() {
		doContainBlackIngredients();
		doContain(sugarDispenser, anyDouble());
	}

	private void verifyBlackSugarPlan(InOrder inOrder) {
		verifyBlackPlan(inOrder);
		inOrder.verify(sugarDispenser).contains(anyDouble());
	}

	private void verifyBlackSugarMix(InOrder inOrder) {
		verifyBlackMix(inOrder);
		inOrder.verify(sugarDispenser).release(anyDouble());
	}
	
	@Test
	public void drinkAndCancel() {
		// Preparing scenario: first drink
		validSession(Drink.BLACK_SUGAR, Coin.dime, Coin.quarter);

		// Preparing scenario: before cancel
		insertCoins(Coin.dollar);
		InOrder inOrder = resetMocks();
		
		// Operation under test
		facade.cancel();
		
		// Verification
		verifyCancel(inOrder, Coin.dollar);
	}

	private void verifyCancel(InOrder inOrder, Coin... change) {
		verifyCancelMessage(inOrder);
		verifyReleaseCoins(inOrder, change);
		verifyNewSession(inOrder);
	}

	

	private void doContain(Dispenser dispenser, Object amount) {
		when(dispenser.contains(amount)).thenReturn(true);
	}

	private void verifyDrinkRelease(InOrder inOrder) {
		inOrder.verify(display).info(Messages.RELEASING);
		inOrder.verify(cupDispenser).release(1);
		inOrder.verify(drinkDispenser).release(anyDouble());
		inOrder.verify(display).info(Messages.TAKE_DRINK);
		verifyNewSession(inOrder);
	}

	private void verifyCancelMessage(InOrder inOrder) {
		inOrder.verify(display).warn(Messages.CANCEL);
	}

	private void insertCoins(Coin... coins) {
		for (Coin coin : coins) {
			facade.insertCoin(coin);
		}
	}

	private void verifyReleaseCoins(InOrder inOrder, Coin coin, int times) {
		inOrder.verify(cashBox, times(times)).release(coin);
	}

	private void verifyReleaseCoins(InOrder inOrder, Coin... coins) {
		for (Coin coin : coins) {
			inOrder.verify(cashBox).release(coin);
		}
	}

	private InOrder resetMocks() {
		reset(mocks());
		return inOrder(mocks());
	}

	private Object[] mocks() {
		return asArray(display, cashBox, coffeePowderDispenser, waterDispenser,
				cupDispenser, drinkDispenser, sugarDispenser);
	}

	private Object[] asArray(Object... objs) {
		return objs;
	}

}
