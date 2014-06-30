package br.ufpb.dce.aps.coffeemachine;

import static org.mockito.Mockito.*;

public class MockComponentsFactory implements ComponentsFactory {

	private Display display;

	public Display getDisplay() {
		if (display == null) {
			display = mock(Display.class);
		}

		return display;
	}

}
