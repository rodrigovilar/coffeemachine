package br.ufpb.dce.aps.coffeemachine;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.*;

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
		verifyCancelMessage(inOrder);
		verifyReleaseCoins(inOrder, Coin.halfDollar);
		verifyNewSession(inOrder);
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
		verifyCancelMessage(inOrder);
		verifyReleaseCoins(inOrder, Coin.nickel, Coin.penny);
		verifyNewSession(inOrder);
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

	private void doContain(Dispenser dispenser, Object amount) {
		when(dispenser.contains(amount)).thenReturn(true);
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
				cupDispenser, drinkDispenser);
	}

	private Object[] asArray(Object... objs) {
		return objs;
	}

}
