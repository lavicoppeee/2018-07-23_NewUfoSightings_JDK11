package it.polito.tdp.newufosightings.model;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.newufosightings.db.NewUfoSightingsDAO;

public class Model {

	NewUfoSightingsDAO dao;
	Simulator sim;
	
	private Graph<State,DefaultWeightedEdge> graph;
	Map<String,State> idMap;
	List<State> states;
	
	public Model() {
		dao= new NewUfoSightingsDAO();
		
	}
	
	//getShape
	
	public List<String> getShape(int anno){
		return dao.getShape(anno);
	}
	
	//crea grafo
	public void creaGrafo(int anno, String shape) {
		
		this.graph=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		idMap=new HashMap<>();
		
		states=dao.getStati(anno,shape,idMap);
		Graphs.addAllVertices(graph, idMap.values());
		
		for(Arco a:dao.getVicino(anno,shape,idMap)) {
			if(!this.graph.containsEdge(a.getS1(), a.getS2())) {
				Graphs.addEdgeWithVertices(graph, a.getS1(),a.getS2());
			}
		}
	}
	
	 //peso archi adiacenti
	//dato uno stato mi calcolo la somma degli archi uscenti 
	public Integer pesoTOT(State s){
		Integer res = 0;
		for(DefaultWeightedEdge e : this.graph.outgoingEdgesOf(s)) {
			res += (int) this.graph.getEdgeWeight(e);
		}
		return res;
	}
	
	public List<State> vertici(){
		return states;
	}
	public int nVertici() {
		return this.graph.vertexSet().size();
	}
	
	public int nArchi() {
		return this.graph.edgeSet().size();
	}
	
	public void simula(int year, String shape, int alpha, int t) {
		List<Event> events=this.dao.getAllEvent(year, shape, idMap);
		sim=new Simulator();
		sim.init(events, alpha, t, graph);
		sim.run();
	}
	
	
	public List<Defcon> getDefcon(){
		return sim.getStateDefcon();
	}
}
