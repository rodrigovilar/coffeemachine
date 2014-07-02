package br.ufpb.dce.aps.coffeemachine;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;


public abstract class CoffeeMachineTest {

	private ComponentsFactory factory;
	private CoffeeMachine facade; 
	
	private Display display;

	protected abstract CoffeeMachine createFacade(ComponentsFactory factory);

	@Before
	public void init() {
		factory = new MockComponentsFactory();
		display = factory.getDisplay();
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

	private void resetMocks() {
		reset(display);
	}

}
