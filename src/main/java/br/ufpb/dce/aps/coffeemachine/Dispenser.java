package br.ufpb.dce.aps.coffeemachine;

public interface Dispenser {

	void release(Object amount);

	boolean contains(Object amount);

}
