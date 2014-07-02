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
		verify(display).info("Insert coins and select a drink!");
	}

	@Test
	public void insertCoin() {
		// Preparing scenario
		facade = createFacade(factory);
		resetMocks();
		
		// Operation under test
		facade.insertCoin(Coin.dime);
		
		// Verification
		verify(display).info("Total: US$ 0.10");
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
		verify(display).info("Total: US$ 0.55");
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
		facade.insertCoin(Coin.halfDollar);
		InOrder inOrder = resetMocks();

		//Operation under test
		facade.cancel();
		
		//Verification
		inOrder.verify(display).warn(Messages.CANCEL_MESSAGE);
		inOrder.verify(cashBox).release(Coin.halfDollar);
		inOrder.verify(display).info(Messages.INSERT_COINS_MESSAGE);
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
