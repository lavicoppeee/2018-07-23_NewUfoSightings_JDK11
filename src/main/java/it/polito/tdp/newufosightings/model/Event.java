package it.polito.tdp.newufosightings.model;

import java.time.LocalDateTime;

public class Event implements Comparable<Event> {
	
	public enum EventType {
		ALLERTA, CESSATA_ALLERTA, CESSATA_ALLERTA_VICINO
	}
	
    EventType type;
	State state;
	LocalDateTime time;
	
	public Event(EventType type, State state, LocalDateTime time) {
		super();
		this.type = type;
		this.state = state;
		this.time = time;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	@Override
	public int compareTo(Event o) {
		// TODO Auto-generated method stub
		return this.time.compareTo(o.getTime());
	}
	
	
	
	

}
