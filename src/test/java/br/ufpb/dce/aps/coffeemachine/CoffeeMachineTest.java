package br.ufpb.dce.aps.coffeemachine;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;


public abstract class CoffeeMachineTest {

	private ComponentsFactory factory;
	
	private Display display; 

	protected abstract CoffeeMachine createFacade(ComponentsFactory factory);

	@Before
	public void init() {
		factory = new MockComponentsFactory();

		display = factory.getDisplay();

	}
	
	@Test
	public void createFacade() {
		createFacade(factory);
		verify(display).info("Insert coins and select a drink!");
	}


}
