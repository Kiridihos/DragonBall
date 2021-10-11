package co.edu.utadeo;
import java.io.File;
import java.io.IOException;
import java.util.*;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Link;

import static guru.nidi.graphviz.model.Factory.*;

class DragonBall
{
	// A class to represent a graph edge
	class Edge implements Comparable<Edge>
	{
		int src, dest, weight;
		boolean drawn = false;

		// Comparator function used for sorting edges 
		// based on their weight
		public int compareTo(Edge compareEdge)
		{
			return this.weight-compareEdge.weight;
		}
	};

	// A class to represent a subset for union-find
	class subset
	{
		int parent, rank;
	};

	int V, E;    // V-> no. of vertices & E->no.of edges
	Edge edge[]; // collection of all edges

	// Creates a graph with V vertices and E edges
	DragonBall(int v, int e)
	{
		V = v;
		E = e;
		edge = new Edge[E];
		for (int i=0; i<e; ++i)
			edge[i] = new Edge();
	}

	// A utility function to find set of an element i
	// (uses path compression technique)
	int find(subset subsets[], int i)
	{
		// find root and make root as parent of i (path compression)
		if (subsets[i].parent != i)
			subsets[i].parent = find(subsets, subsets[i].parent);

		return subsets[i].parent;
	}

	// A function that does union of two sets of x and y
	// (uses union by rank)
	void Union(subset subsets[], int x, int y)
	{
		int xroot = find(subsets, x);
		int yroot = find(subsets, y);

		// Attach smaller rank tree under root of high rank tree
		// (Union by Rank)
		if (subsets[xroot].rank < subsets[yroot].rank)
			subsets[xroot].parent = yroot;
		else if (subsets[xroot].rank > subsets[yroot].rank)
			subsets[yroot].parent = xroot;

		// If ranks are same, then make one as root and increment
		// its rank by one
		else
		{
			subsets[yroot].parent = xroot;
			subsets[xroot].rank++;
		}
	}

	// The main function to construct MST using Kruskal's algorithm
	long KruskalMST(Graph g)
	{
		Edge result[] = new Edge[V];  // Tnis will store the resultant MST
		int e = 0;  // An index variable, used for result[]
		int i = 0;  // An index variable, used for sorted edges
		for (i=0; i<V; ++i)
			result[i] = new Edge();

		// Step 1:  Sort all the edges in non-decreasing order of their
		// weight.  If we are not allowed to change the given graph, we
		// can create a copy of array of edges
		Arrays.sort(edge);

		// Allocate memory for creating V ssubsets
		subset subsets[] = new subset[V];
		for(i=0; i<V; ++i)
			subsets[i]=new subset();

		// Create V subsets with single elements
		for (int v = 0; v < V; ++v)
		{
			subsets[v].parent = v;
			subsets[v].rank = 0;
		}

		i = 0;  // Index used to pick next edge

		// Number of edges to be taken is equal to V-1
		while (e < V - 1)
		{
			// Step 2: Pick the smallest edge. And increment 
			// the index for next iteration
			Edge next_edge = new Edge();
			next_edge = edge[i++];

			int x = find(subsets, next_edge.src-1);
			int y = find(subsets, next_edge.dest-1);

			// If including this edge does't cause cycle,
			// include it in result and increment the index 
			// of result for next edge
			if (x != y)
			{
				result[e++] = next_edge;
				Union(subsets, x, y);
			}
			// Else discard the next_edge
		}
		
		for (DragonBall.Edge edge: this.edge) {
			Edge equal = null;
			if (edge.drawn) continue;
			if (edge.src != 0) {
				for (Edge edge1: result) {
					if (edge.src == edge1.src
							&& edge.dest == edge1.dest
							&& edge.weight == edge1.weight) {
						equal = edge1;
						break;
					}
					else {
						equal = null;
					}
				}
				if (equal != null) {
					g = g.with(node(Integer.toString(edge.src)).link(to(node(Integer.toString(edge.dest)))
							.with(Style.BOLD, Label.of(Integer.toString(edge.weight)))));
				} else {
					g = g.with(node(Integer.toString(edge.src)).link(to(node(Integer.toString(edge.dest)))
							.with(Label.of(Integer.toString(edge.weight)))));
				}
			}
		}
		
		try {
			Graphviz.fromGraph(g).width(900).render(Format.PNG).toFile(new File("grafoDb.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// print the contents of result[] to display
		// the built MST
		long acum=0;
		for (i = 0; i < e; ++i){

			acum+=result[i].weight;
		}
		
		return acum;
	}

	// Driver Program
	public static void main (String[] args)
	{
		Graph g = graph("dragonBall")
				.linkAttr().with("class", "link-class");
		try {
			Scanner ingreso = new Scanner (System.in);
			int contador=0;
			int i = 1;
			int V = ingreso.nextInt();
			int E = ingreso.nextInt();
			ingreso.nextLine();
			DragonBall graph = new DragonBall(V, E);
			while (i <= E ) {
				String j = ingreso.nextLine();
				String[] partes = j.split(" ");
				int parte1=(int)Double.parseDouble(partes[0]);
				int parte2=(int)Double.parseDouble(partes[1]);
				int parte3=(int)Double.parseDouble(partes[2]);
				graph.edge[contador].src = parte1;
				graph.edge[contador].dest = parte2;
				graph.edge[contador].weight = parte3;
//				g = g.with(node(Integer.toString(parte1))
//						.link(to(node(Integer.toString(parte2)))
//								.with(Label.of(Integer.toString(parte3)))));
				contador++;
				i++;
			}
			System.out.println("El costo de encontrar las esferas es "+graph.KruskalMST(g));
			
		}catch(Exception ArrayIndexOutOfBoundsException) {
			System.out.println("Lo siento Krilin no va a poder revivir Busque otro amigo");
		}

	}
}