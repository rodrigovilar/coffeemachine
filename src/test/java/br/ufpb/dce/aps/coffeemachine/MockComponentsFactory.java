package br.ufpb.dce.aps.coffeemachine;

import static org.mockito.Mockito.*;

public class MockComponentsFactory implements ComponentsFactory {

	private Display display;
	private CashBox cashBox;

	public Display getDisplay() {
		if (display == null) {
			display = mock(Display.class);
		}

		return display;
	}

	public CashBox getCashBox() {
		if (cashBox == null) {
			cashBox = mock(CashBox.class);
		}

		return cashBox;
	}

}
