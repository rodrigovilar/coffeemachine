package br.ufpb.dce.aps.coffeemachine;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class ReadBadge extends CoffeeMachineTest {
	
	private InOrder inOrder;

	@Before
	public void given() {
		facade = createFacade(factory);
		facade.readBadge(123456);
		inOrder = resetMocks();
	}

	@Test
	public void readBadgeInsertCoin() {
		// Operation under test
		insertCoins(Coin.dime);

		// Verification
		verifyCannotInsertCoinsMessage(inOrder);
		verifyReleaseCoins(inOrder, Coin.dime);
	}

}
