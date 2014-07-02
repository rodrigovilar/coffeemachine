package br.ufpb.dce.aps.coffeemachine;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;


public abstract class CoffeeMachineTest {

	private ComponentsFactory factory;
	private CoffeeMachine facade; 
	
	private Display display;
	private CashBox cashBox; 

	protected abstract CoffeeMachine createFacade(ComponentsFactory factory);

	@Before
	public void init() {
		factory = new MockComponentsFactory();
		display = factory.getDisplay();
		cashBox = factory.getCashBox();
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
			verify(display).info(Messages.INSERT_COINS_MESSAGE);
		} else {
			inOrder.verify(display).info(Messages.INSERT_COINS_MESSAGE);			
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
	
	@Test(expected=CoffeeMachineException.class)
	public void nullCoin() {
		// Preparing scenario
		facade = createFacade(factory);
		resetMocks();
		
		// Operation under test
		facade.insertCoin(null);
	}
	
	@Test(expected=CoffeeMachineException.class)
	public void cancelWithoutCoins() {
		//Preparing scenario
		facade = createFacade(factory);
		resetMocks();

		//Operation under test
		facade.cancel();
	}

	@Test
	public void cancelWithOneCoin() {
		//Preparing scenario
		facade = createFacade(factory);
		insertCoins(Coin.halfDollar);
		InOrder inOrder = resetMocks();

		//Operation under test
		facade.cancel();
		
		//Verification
		verifyCancelMessage(inOrder);
		verifyReleaseCoins(inOrder, Coin.halfDollar);
		verifyNewSession(inOrder);
	}

	@Test
	public void cancelWithTwoCoins() {
		//Preparing scenario
		facade = createFacade(factory);
		insertCoins(Coin.penny, Coin.nickel);
		InOrder inOrder = resetMocks();
		
		//Operation under test
		facade.cancel();
		
		verifyCancelMessage(inOrder);
		verifyReleaseCoins(inOrder, Coin.nickel, Coin.penny);
		verifyNewSession(inOrder);
	}


	private void verifyCancelMessage(InOrder inOrder) {
		inOrder.verify(display).warn(Messages.CANCEL_MESSAGE);
	}
	
	private void insertCoins(Coin... coins) {
		for (Coin coin : coins) {
			facade.insertCoin(coin);			
		}
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
		return asArray(display, cashBox);
	}

	private Object[] asArray(Object... objs) {
		return objs;
	}

}
