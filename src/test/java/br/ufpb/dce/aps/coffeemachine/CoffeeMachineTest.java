package br.ufpb.dce.aps.coffeemachine;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.InOrder;

public class CoffeeMachineTest {

	protected ComponentsFactory factory;
	protected CoffeeMachine facade;

	protected Display display;
	protected CashBox cashBox;
	protected Dispenser coffeePowderDispenser;
	protected Dispenser waterDispenser;
	protected Dispenser cupDispenser;
	private DrinkDispenser drinkDispenser;
	protected Dispenser sugarDispenser;
	protected Dispenser creamerDispenser;
	protected Dispenser bouillonDispenser;

	protected final CoffeeMachine createFacade(ComponentsFactory factory) {
		
		CoffeeMachine coffeeMachine = null;
		
		try {
			@SuppressWarnings("unchecked")
			Class<CoffeeMachine> clazz = (Class<CoffeeMachine>) Class
					.forName("br.ufpb.dce.aps.coffeemachine.impl.MyCoffeeMachine");
			
			coffeeMachine = clazz.newInstance();
			coffeeMachine.setFactory(factory);
			
		} catch (ClassNotFoundException e) {
			Assert.fail();
		} catch (InstantiationException e) {
			Assert.fail();
		} catch (IllegalAccessException e) {
			Assert.fail();
		}
		
		return coffeeMachine;
	}

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
		creamerDispenser = factory.getCreamerDispenser();
		bouillonDispenser = factory.getBouillonDispenser();
	}

	@After
	public void genericVerifications() {
		verifyNoMoreInteractions(mocks());
	}

	protected void verifyNewSession(InOrder inOrder) {
		if (inOrder == null) {
			verify(display).info(Messages.INSERT_COINS);
		} else {
			inOrder.verify(display).info(Messages.INSERT_COINS);
		}
	}

	protected void verifySessionMoney(String value) {
		verify(display).info("Total: US$ " + value);
	}

	protected InOrder prepareScenarioWithCoins(Coin... coins) {
		facade = createFacade(factory);
		insertCoins(coins);
		return resetMocks();
	}

	protected void doContainBlackIngredients() {
		doContain(coffeePowderDispenser, anyDouble());
		doContain(waterDispenser, anyDouble());
		doContain(cupDispenser, 1);
	}

	protected void verifyBlackPlan(InOrder inOrder) {
		inOrder.verify(cupDispenser).contains(1);
		inOrder.verify(waterDispenser).contains(100);
		inOrder.verify(coffeePowderDispenser).contains(15);
	}

	protected void verifyBlackMix(InOrder inOrder) {
		inOrder.verify(display).info(Messages.MIXING);
		inOrder.verify(coffeePowderDispenser).release(15);
		inOrder.verify(waterDispenser).release(100);
	}

	@SuppressWarnings("incomplete-switch")
	protected void validSession(Drink drink, Coin... coins) {
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

	protected void doContainBlackSugarIngredients() {
		doContainBlackIngredients();
		doContain(sugarDispenser, anyDouble());
	}

	protected void verifyBlackSugarPlan(InOrder inOrder) {
		verifyBlackPlan(inOrder);
		inOrder.verify(sugarDispenser).contains(5);
	}

	protected void verifyBlackSugarMix(InOrder inOrder) {
		verifyBlackMix(inOrder);
		inOrder.verify(sugarDispenser).release(5);
	}

	protected void verifyCancel(InOrder inOrder, Coin... change) {
		verifyCancelMessage(inOrder);
		verifyReleaseCoins(inOrder, change);
		verifyNewSession(inOrder);
	}

	protected void doContainBouillonIngredients() {
		doContain(bouillonDispenser, anyDouble());
		doContain(waterDispenser, anyDouble());
		doContain(cupDispenser, 1);
	}
	
	protected void verifyBouillonPlan(InOrder inOrder) {
		inOrder.verify(cupDispenser).contains(1);
		inOrder.verify(waterDispenser).contains(100);
		inOrder.verify(bouillonDispenser).contains(10);
	}

	protected void verifyBouillonMix(InOrder inOrder) {
		inOrder.verify(display).info(Messages.MIXING);
		inOrder.verify(bouillonDispenser).release(10);
		inOrder.verify(waterDispenser).release(100);
	}

	protected void doCount(Coin coin, int amount) {
		when(cashBox.count(coin)).thenReturn(amount);
	}

	protected void doContainWhiteSugarIngredients() {
		doContainWhiteIngredients();
		doContain(sugarDispenser, anyDouble());
	}

	protected void verifyWhiteSugarPlan(InOrder inOrder) {
		verifyWhitePlan(inOrder);
		inOrder.verify(sugarDispenser).contains(5);
	}

	protected void verifyWhiteSugarMix(InOrder inOrder) {
		verifyWhiteMix(inOrder);
		inOrder.verify(sugarDispenser).release(5);
	}

	protected void verifyCount(InOrder inOrder, Coin... change) {
		for (Coin coin : change) {
			inOrder.verify(cashBox).count(coin);
		}
	}

	protected void verifyCloseSession(InOrder inOrder, Coin... change) {
		verifyReleaseCoins(inOrder, change);
		verifyNewSession(inOrder);
	}

	protected void doContainWhiteIngredients() {
		doContainBlackIngredients();
		doContain(creamerDispenser, anyDouble());
	}

	protected void verifyWhitePlan(InOrder inOrder) {
		inOrder.verify(cupDispenser).contains(1);
		inOrder.verify(waterDispenser).contains(80);
		inOrder.verify(coffeePowderDispenser).contains(15);
		inOrder.verify(creamerDispenser).contains(20);
	}

	protected void verifyWhiteMix(InOrder inOrder) {
		inOrder.verify(display).info(Messages.MIXING);
		inOrder.verify(coffeePowderDispenser).release(15);
		inOrder.verify(waterDispenser).release(80);
		inOrder.verify(creamerDispenser).release(20);
	}

	protected void verifyOutOfIngredient(InOrder inOrder, String message,
			Coin... coins) {
		inOrder.verify(display).warn(message);
		verifyReleaseCoins(inOrder, coins);
		verifyNewSession(inOrder);
	}

	protected void doContain(Dispenser dispenser, Object amount) {
		when(dispenser.contains(amount)).thenReturn(true);
	}

	protected void doNotContain(Dispenser dispenser, Object amount) {
		when(dispenser.contains(amount)).thenReturn(false);
	}

	protected void verifyDrinkRelease(InOrder inOrder) {
		inOrder.verify(display).info(Messages.RELEASING);
		inOrder.verify(cupDispenser).release(1);
		inOrder.verify(drinkDispenser).release(100);
		inOrder.verify(display).info(Messages.TAKE_DRINK);
	}

	protected void verifyCancelMessage(InOrder inOrder) {
		inOrder.verify(display).warn(Messages.CANCEL);
	}

	protected void verifyCannotInsertCoinsMessage(InOrder inOrder) {
		inOrder.verify(display).warn(Messages.CAN_NOT_INSERT_COINS);
	}

	protected void insertCoins(Coin... coins) {
		for (Coin coin : coins) {
			facade.insertCoin(coin);
		}
	}

	protected void verifyReleaseCoins(InOrder inOrder, Coin coin, int times) {
		inOrder.verify(cashBox, times(times)).release(coin);
	}

	protected void verifyReleaseCoins(InOrder inOrder, Coin... coins) {
		for (Coin coin : coins) {
			inOrder.verify(cashBox).release(coin);
		}
	}

	protected InOrder resetMocks() {
		reset(mocks());
		return inOrder(mocks());
	}

	private Object[] mocks() {
		return asArray(display, cashBox, coffeePowderDispenser, waterDispenser,
				cupDispenser, drinkDispenser, sugarDispenser, creamerDispenser,
				bouillonDispenser);
	}

	private Object[] asArray(Object... objs) {
		return objs;
	}

	protected void verifyBadgeRead(InOrder inOrder) {
		inOrder.verify(display).info(Messages.BADGE_READ);
	}

}
