package br.ufpb.dce.aps.coffeemachine;

public enum Coin {

	penny(1), nickel(5), dime(10), quarter(25), halfDollar(50), dollar(100);

	private final int value;

	private Coin(final int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static Coin[] reverse() {
		return new Coin[] { dollar, halfDollar, quarter, dime, nickel, penny };
	}
}
