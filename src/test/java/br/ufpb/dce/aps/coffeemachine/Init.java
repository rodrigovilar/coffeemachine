package br.ufpb.dce.aps.coffeemachine;

import org.junit.Test;

public class Init extends CoffeeMachineTest {

	@Test
	public void createFacade() {
		// Operation under test
		facade = createFacade(factory);

		// Verification
		verifyDefaultButtonConfiguration();
		verifyNewSession(null);
	}

}
