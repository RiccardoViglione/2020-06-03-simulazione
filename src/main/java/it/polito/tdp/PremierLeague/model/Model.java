package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
private  PremierLeagueDAO dao;
private Map<Integer,Player>idMap;
private Graph<Player,DefaultWeightedEdge>grafo;

public Model() {
	dao=new PremierLeagueDAO ();
	idMap=new HashMap<>();
	dao.listAllPlayers(idMap);
}public void creaGrafo(double x) {
	this.grafo=new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
	Graphs.addAllVertices(this.grafo, dao.getVertici(x, idMap));
	for(Adiacenza a:dao.getArchi(idMap)) {
	
	if(grafo.containsVertex(a.getP1())&&grafo.containsVertex(a.getP2())) {
		if(a.getPeso()>0) {
			Graphs.addEdgeWithVertices(grafo, a.getP1(), a.getP2(),a.getPeso());
		}
		else if(a.getPeso()<0) {
			Graphs.addEdgeWithVertices(grafo, a.getP2(), a.getP1(),a.getPeso()*(-1));
		}
		
	}
	}
	
	
}
public int nVertici() {
	return grafo.vertexSet().size();
}

public int nArchi() {
	return grafo.edgeSet().size();
}
public TopPlayer getTopPlayer() {
	if(grafo == null)
		return null;
	
	Player best = null;
	Integer maxDegree = Integer.MIN_VALUE;
	for(Player p : grafo.vertexSet()) {
		if(grafo.outDegreeOf(p) > maxDegree) {
			maxDegree = grafo.outDegreeOf(p);
			best = p;
		}
	}
	
	TopPlayer topPlayer = new TopPlayer();
	topPlayer.setPlayer(best);
	
	List<Opponent> opponents = new ArrayList<>();
	for(DefaultWeightedEdge edge : grafo.outgoingEdgesOf(topPlayer.getPlayer())) {
		opponents.add(new Opponent(grafo.getEdgeTarget(edge), (int) grafo.getEdgeWeight(edge)));
	}
	Collections.sort(opponents);
	topPlayer.setOpponents(opponents);
	return topPlayer;
	
}
}
