package it.polito.tdp.newufosightings.model;

public class Defcon implements Comparable<Defcon> {

	State state;
	Double livello;
	
	public Defcon(State state, double livello) {
		super();
		this.state = state;
		this.livello = livello;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Double getLivello() {
		return livello;
	}

	public void setLivello(Double livello) {
		this.livello = livello;
	}

	@Override
	public int compareTo(Defcon o) {
		// TODO Auto-generated method stub
		return this.livello.compareTo(o.getLivello());
	}
	
	public String toString() {
		return this.state.toString()+" | "+livello;
	}
	
}
