package br.ufpb.dce.aps.coffeemachine;

public interface CoffeeMachine {

	void insertCoin(Coin dime);

	void cancel();

	void select(Drink drink);
}
