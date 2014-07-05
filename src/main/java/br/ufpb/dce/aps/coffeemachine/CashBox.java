package br.ufpb.dce.aps.coffeemachine;

public interface CashBox {

	void release(Coin coin);

	int count(Coin coin);
	
}
