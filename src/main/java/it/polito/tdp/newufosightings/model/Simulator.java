package it.polito.tdp.newufosightings.model;

import java.time.LocalDateTime;
import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.newufosightings.db.NewUfoSightingsDAO;
import it.polito.tdp.newufosightings.model.Event.EventType;

public class Simulator {

	//in
	int tempo;
	int alpha;

	NewUfoSightingsDAO dao;

	//mondo
	private PriorityQueue<Event> queue;
	private Graph<State,DefaultWeightedEdge> graph;
	
	//out
	Map<String,Defcon> DEFCON; //Mappa di stati dove registro anche il livello 
	LocalDateTime fine;

	public void init(List<Event> events, int alpha2, int t, Graph<State, DefaultWeightedEdge> graph2) {
		this.tempo=t;
		this.alpha=alpha2;
		this.graph=graph2;
		
		//AGGIUNGI TUTTI GLI EVENTI CONSIDERATI 
		this.queue = new PriorityQueue<>();
		queue.addAll(events);
		
		this.DEFCON= new HashMap<>();
		
		//INIZIALLIZZO TUTTI GLI STATI AL LIVELLO 5:
		for(State state : this.graph.vertexSet()) {
			this.DEFCON.put(state.getId(), new Defcon(state, 5.0));
		}
		
		this.fine = events.get(events.size()-1).getTime();
		
	}

	public void run() {
		while(!this.queue.isEmpty()) {
			Event e = this.queue.poll();
			if(e.getTime().isAfter(fine))
				return;
			this.processEvent(e);
		}
	}

	private void processEvent(Event e) {
		
		State state=e.getState();
		String id=state.getId();
		
		switch(e.getType()) {
		
		case ALLERTA:
			//se la mappa contiene lo stato
			//decremento il livello
			if(DEFCON.containsKey(id)) {
				Defcon stateD=this.DEFCON.get(id);
				if(stateD.getLivello()>=2) {
					stateD.setLivello(stateD.getLivello()-1.0);
				}
				
				//genero la probabilità del vicino
				Random randi=new Random();
				Double p=randi.nextDouble();
				
				if(p<=(alpha/100)) {
					//prendo gli adiacenti allo stato di partenza 
					List<State> vicini=Graphs.successorListOf(graph, state);
					for(State v: vicini) {
						//se la mappa contiene gli stati adiacenti
						
						if(DEFCON.containsKey(v.getId())) {
						Defcon def=this.DEFCON.get(v.getId());
						
						//controlo che posso sottrarre e non andare sotto 1
						if(def.getLivello()>=1.5) {
							def.setLivello(stateD.getLivello()-0.5);
						}
						this.queue.add(new Event(EventType.CESSATA_ALLERTA_VICINO,v,e.getTime().plusDays(tempo)));
						
						}
					}
				}
				
				this.queue.add(new Event(EventType.CESSATA_ALLERTA,e.getState(),e.getTime().plusDays(tempo)));
				
			}
			break;
		
		case CESSATA_ALLERTA:
			if(this.DEFCON.containsKey(id)) {
				Defcon sDefcon=this.DEFCON.get(id);
				if(sDefcon.getLivello()<=4) {
					sDefcon.setLivello(sDefcon.getLivello()+1.0);
				}
			}
			break;
			
		case CESSATA_ALLERTA_VICINO:
			if(this.DEFCON.containsKey(id)) {
				Defcon sDefcon=this.DEFCON.get(id);
				if(sDefcon.getLivello()<=4.5) {
					sDefcon.setLivello(sDefcon.getLivello()+0.5);
				}
			}
			break;
			
		
		}
		
	}
	
	public List<Defcon> getStateDefcon() {
		List<Defcon> result = new ArrayList<Defcon>(this.DEFCON.values());
		result.sort(null);
		return result;
	}
	
}
