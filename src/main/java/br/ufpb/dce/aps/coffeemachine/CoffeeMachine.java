package br.ufpb.dce.aps.coffeemachine;

public interface CoffeeMachine {

	void insertCoin(Coin dime);

	void cancel();

	void select(Button drink);

	void setFactory(ComponentsFactory factory);

	void readBadge(int badgeCode);
}
