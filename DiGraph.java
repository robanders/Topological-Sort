package DiGraph_A5;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DiGraph implements DiGraphInterface {

	// in here go all your data and methods for the graph
	// and the topo sort operation
	public long numNodes;
	public long numEdges;
	public List<Vertex> vertexList = new LinkedList<Vertex>();
	public HashMap<String, Vertex> labelMap = new HashMap<String, Vertex>();
	public HashMap<Long, Vertex> idNumMap = new HashMap<Long, Vertex>();
	public HashMap<Long, Edge> idEdgeMap = new HashMap<Long, Edge>();

	public DiGraph() { // default constructor
		// explicitly include this
		// we need to have the default constructor
		// if you then write others, this one will still be there
		numNodes = 0;
		numEdges = 0;
	}

	public boolean addNode(long idNum, String label) {
		if(idNum < 0 || label == null){
			return false;
		}
		Vertex vertex = labelMap.get(label);
		if(vertex != null){
			return false;
		}
		vertex = idNumMap.get(idNum);
		if(vertex != null){
			return false;
		}
		Vertex newVertex = new Vertex(idNum, label);
		vertexList.add(newVertex);
		labelMap.put(label, newVertex);
		idNumMap.put(idNum, newVertex);
		numNodes++;
		return true;
	}

	public boolean addEdge(long idNum, String sLabel, String dLabel, long weight, String eLabel) {
		Edge edge;
		if(idNum < 0){
			return false;
		}
		Vertex sVertex = labelMap.get(sLabel);
		if(sVertex == null){
			return false;
		}
		Vertex dVertex = labelMap.get(dLabel);
		if(dVertex == null){
			return false;
		}
		edge = idEdgeMap.get(idNum);
		if(edge != null){
			return false;
		}
		//check if there is already an edge between sLabel and dLabel
		Iterator<Edge> edgeIterator = sVertex.edgeList.iterator();
		while(edgeIterator.hasNext()){
			edge = edgeIterator.next();
			if(edge.getdestinationVertex() == dVertex){
				return false;
			}
		}
		
		Edge newEdge = new Edge(idNum, eLabel, weight, sVertex, dVertex);
		idEdgeMap.put(idNum, newEdge);
		numEdges++;
		sVertex.addEdge(newEdge);
		dVertex.incrementInDegree();
		dVertex.addInEdge(newEdge);
		return true;
	}

	public boolean delNode(String label) {
		Edge edge;
		if(label == null){
			return false;
		}
		Vertex vertex = labelMap.get(label);
		if(vertex == null){
			return false;
		}
		vertexList.remove(vertex);
		labelMap.remove(label);
		idNumMap.remove(vertex.getidNum());
		numNodes--;
		Iterator<Edge> edgeIterator = vertex.edgeList.iterator();
		while(edgeIterator.hasNext()){
			edge = edgeIterator.next();
			//System.out.println("out edge list: " + edge.getsourceVertex().getlabel() + " ---> " + edge.getdestinationVertex().getlabel());
			//vertex.edgeList.remove(edge);
			edge.getdestinationVertex().decrementInDegree();
			idEdgeMap.remove(edge.getidNum());
			numEdges--;
		}
		Iterator<Edge> inEdgeIterator = vertex.inEdgeList.iterator();
		while(inEdgeIterator.hasNext()){
			edge = inEdgeIterator.next();
			//System.out.println("in edge list: " + edge.getsourceVertex().getlabel() + " ---> " + edge.getdestinationVertex().getlabel() + "vertex: " + vertex.getlabel());
			if(edge.getsourceVertex() != vertex){
				//System.out.println("in edge list (if): " + edge.getsourceVertex().getlabel() + " ---> " + edge.getdestinationVertex().getlabel());
				edge.getsourceVertex().edgeList.remove(edge);
				idEdgeMap.remove(edge.getidNum());
				numEdges--;
			}
		}
		return true;
	}

	public boolean delEdge(String sLabel, String dLabel) {
		Edge edge;
		boolean found = false;
		Vertex sVertex = labelMap.get(sLabel);
		if(sVertex == null){
			return false;
		}
		Vertex dVertex = labelMap.get(dLabel);
		if(dVertex == null){
			return false;
		}
		Iterator<Edge> edgeIterator = sVertex.edgeList.iterator();
		while(edgeIterator.hasNext()){
			edge = edgeIterator.next();
			if(edge.getdestinationVertex() == dVertex){
				sVertex.edgeList.remove(edge);
				dVertex.decrementInDegree();
				idEdgeMap.remove(edge.getidNum());
				found = true;
			}
		}
		Iterator<Edge> inEdgeIterator = dVertex.inEdgeList.iterator();
		while(inEdgeIterator.hasNext()){
			edge = inEdgeIterator.next();
			if(edge.getsourceVertex() == sVertex){
				dVertex.inEdgeList.remove(edge);
				idEdgeMap.remove(edge.getidNum());
				found = true;
			}
		}
		if(found){
			numEdges--;
			return true;
		}
		return false;
	}

	public long numNodes() {
		return numNodes;
	}

	public long numEdges() {
		return numEdges;
	}
	
	public void print(){
		Vertex vertex;
		Edge edge;
		Iterator<Vertex> vertexIterator = vertexList.iterator();
		while(vertexIterator.hasNext()){
			vertex = vertexIterator.next();
			//System.out.println("(" + vertex.getidNum() + ") " + vertex.getlabel());
			Iterator<Edge> edgeIterator = vertex.edgeList.iterator();
			while(edgeIterator.hasNext()){
				edge = edgeIterator.next();
				//System.out.println("    (" + edge.getidNum() + ")" + "--" + edge.getweight() + "," + edge.getlabel() + "-->" + edge.getdestinationVertex().getlabel());
			}
		}
	}

	public String[] topoSort() {
		Vertex vertex;
		Edge edge;
		int index = 0;
		Queue<Vertex> vertexQueue = new LinkedList<Vertex>();
		Iterator<Vertex> vertexIterator = vertexList.iterator();
		Iterator<Edge> edgeIterator;
		String[] toposort = new String[(int) numNodes];
		
		while(vertexIterator.hasNext()){
			vertex = vertexIterator.next();
			vertex.reset();
			//System.out.println("topo in degree: " + vertex.getlabel() + " " +vertex.getTopoInDegree());
			if(vertex.getTopoInDegree() == 0){
				vertexQueue.add(vertex);
			}
		}
		while(vertexQueue.isEmpty() == false){
			vertex = vertexQueue.remove();
			toposort[index] = vertex.getlabel();
			index++;
			edgeIterator = vertex.edgeList.iterator();
			while(edgeIterator.hasNext()){
				edge = edgeIterator.next();
				edge.getdestinationVertex().decrementtopoInDegree();
				if(edge.getdestinationVertex().getTopoInDegree() == 0){
					vertexQueue.add(edge.getdestinationVertex());
				}
			}	
		}
		if(index != numNodes){
			return null;
		}
		return toposort;
	}
}