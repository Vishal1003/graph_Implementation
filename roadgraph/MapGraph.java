package roadgraph;

import geography.GeographicPoint;
import util.GraphLoader;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

/**
 * A class which represents a graph of geographic locations Nodes in the graph
 * are intersections between
 *
 */
public class MapGraph {

	private int num_vertices;
	private int num_Edges;
	private Map<GeographicPoint, ArrayList<GraphNode>> adjList;

	/**
	 * Create a new empty MapGraph
	 */
	public MapGraph() {
		// Initializing the variables in this constructor
		num_vertices = 0;
		num_Edges = 0;
		adjList = new HashMap<>();
	}

	/**
	 * Get the number of vertices (road intersections) in the graph
	 * 
	 * @return The number of vertices in the graph.
	 */
	public int getNumVertices() {
		return num_vertices;
	}

	/**
	 * Return the intersections, which are the vertices in this graph.
	 * 
	 * @return The vertices in this graph as GeographicPoints
	 */
	public Set<GeographicPoint> getVertices() {
		return new HashSet<>(adjList.keySet());
	}

	/**
	 * Get the number of road segments in the graph
	 * 
	 * @return The number of edges in the graph.
	 */
	public int getNumEdges() {
		return num_Edges;
	}

	/**
	 * Add a node corresponding to an intersection at a Geographic Point If the
	 * location is already in the graph or null, this method does not change the
	 * graph.
	 * 
	 * @param location The location of the intersection
	 * @return true if a node was added, false if it was not (the node was already
	 *         in the graph, or the parameter is null).
	 */
	public boolean addVertex(GeographicPoint location) {
		if (adjList.containsKey(location) || location.equals(null)) {
			return false;
		}
		adjList.put(location, new ArrayList<>());
		num_vertices++;
		return true;
	}

	/**
	 * Adds a directed edge to the graph from pt1 to pt2. Precondition: Both
	 * GeographicPoints have already been added to the graph
	 * 
	 * @param from     The starting point of the edge
	 * @param to       The ending point of the edge
	 * @param roadName The name of the road
	 * @param roadType The type of the road
	 * @param length   The length of the road, in km
	 * @throws IllegalArgumentException If the points have not already been added as
	 *                                  nodes to the graph, if any of the arguments
	 *                                  is null, or if the length is less than 0.
	 */
	public void addEdge(GeographicPoint from, GeographicPoint to, String roadName, String roadType, double length)
			throws IllegalArgumentException {

		if (!adjList.containsKey(from) && !adjList.containsKey(to)) {
			throw new IllegalArgumentException();
		}
		num_Edges++;
		adjList.get(from).add(new GraphNode(to, roadName, roadType, length));
	}

	/**
	 * Find the path from start to goal using breadth first search
	 * 
	 * @param start The starting location
	 * @param goal  The goal location
	 * @return The list of intersections that form the shortest (unweighted) path
	 *         from start to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
		Consumer<GeographicPoint> temp = (x) -> {
		};
		return bfs(start, goal, temp);
	}

	/**
	 * Find the path from start to goal using breadth first search
	 * 
	 * @param start        The starting location
	 * @param goal         The goal location
	 * @param nodeSearched A hook for visualization. See assignment instructions for
	 *                     how to use it.
	 * @return The list of intersections that form the shortest (unweighted) path
	 *         from start to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint goal,
			Consumer<GeographicPoint> nodeSearched) {

		HashMap<GeographicPoint, GeographicPoint> parentMap = new HashMap<>();
		Queue<GeographicPoint> toExplore = new LinkedList<>();
		HashSet<GeographicPoint> visited = new HashSet<>();
		toExplore.add(start);

		while (!toExplore.isEmpty()) {
			GeographicPoint curr = toExplore.poll();
			if (curr.equals(goal)) {
				return constructPath(start, goal, parentMap);
			} // if found quit loop

			for (GraphNode gp : adjList.get(curr)) {
				GeographicPoint gpVal = gp.getValue();
				if (!visited.contains(gpVal)) {
					visited.add(gpVal);
					toExplore.add(gpVal);
					parentMap.put(gpVal, curr);

					// hook for visualization
					nodeSearched.accept(gpVal);
				}
			}
		}
		return null;

	}

	private List<GeographicPoint> constructPath(GeographicPoint start, GeographicPoint goal,
			HashMap<GeographicPoint, GeographicPoint> parentMap) {
		LinkedList<GeographicPoint> path = new LinkedList<>();

		GeographicPoint curr = goal;
		while (!curr.equals(start)) {
			if (parentMap.get(curr) == null && !curr.equals(start)) {
				return null;
			}
			path.addFirst(curr);
			curr = parentMap.get(curr);
		}
		path.addFirst(start);
		return path;
	}

	/**
	 * Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start The starting location
	 * @param goal  The goal location
	 * @return The list of intersections that form the shortest path from start to
	 *         goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
		// You do not need to change this method.
		Consumer<GeographicPoint> temp = (x) -> {
		};
		return dijkstra(start, goal, temp);
	}

	private double maxSpeedLimit(double X, double Y) {
		try {

			String out = new Scanner(
					new URL("https://overpass-api.de/api/interpreter?data=[out:json];way[highway](around:333," + X + ","
							+ Y + ");out;").openStream(),
					"UTF-8").useDelimiter("\\A").next();
			if (out.contains("maxspeed")) {
				int index = out.indexOf("maxspeed");
				String speed = out.substring(index + 12, out.indexOf(" ", index + 12));

				return Integer.parseInt(speed);
			}

		} catch (Exception e) {
			System.out.println(e);
		}
		return 55;
	}

	/**
	 * Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start        The starting location
	 * @param goal         The goal location
	 * @param nodeSearched A hook for visualization. See assignment instructions for
	 *                     how to use it.
	 * @return The list of intersections that form the shortest path from start to
	 *         goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint goal,
			Consumer<GeographicPoint> nodeSearched) {

		PriorityQueue<GraphNode> toExplore = new PriorityQueue();
		HashSet<GeographicPoint> visited = new HashSet<>();
		HashMap<GeographicPoint, GeographicPoint> parentMap = new HashMap<>();
		HashMap<GeographicPoint, Double> distances = new HashMap<>();

		GraphNode starterNode = new GraphNode(start);
		toExplore.add(starterNode);
		distances.put(start, 0.0);

		while (!toExplore.isEmpty()) {
			GraphNode curr = toExplore.poll();
			if (!visited.contains(curr.getValue())) {
				visited.add(curr.getValue());
				if (curr.getValue().equals(goal)) {
					return constructPath(start, goal, parentMap);
				} // if found quit loop
				for (GraphNode node : adjList.get(curr.getValue())) {
					if (!visited.contains(node.getValue()) && (!distances.containsKey(node.getValue())
							|| distances.get(curr.getValue()) + node.getLength() < distances.get(node.getValue()))) {

						distances.put(node.getValue(),
								(node.getLength() / maxSpeedLimit(node.getValue().getX(), node.getValue().getY()))
										+ distances.get(curr.getValue()));
						parentMap.put(node.getValue(), curr.getValue());
						toExplore.add(node);

// hook for visualization
						nodeSearched.accept(node.getValue());
					}
				}
			}
		}
		return null;
	}

	/**
	 * Find the path from start to goal using A-Star search
	 * 
	 * @param start The starting location
	 * @param goal  The goal location
	 * @return The list of intersections that form the shortest path from start to
	 *         goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
		Consumer<GeographicPoint> temp = (x) -> {
		};
		return aStarSearch(start, goal, temp);
	}

	/**
	 * Find the path from start to goal using A-Star search
	 * 
	 * @param start        The starting location
	 * @param goal         The goal location
	 * @param nodeSearched A hook for visualization. See assignment instructions for
	 *                     how to use it.
	 * @return The list of intersections that form the shortest path from start to
	 *         goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint goal,
			Consumer<GeographicPoint> nodeSearched) {

		PriorityQueue<GraphNode> toExplore = new PriorityQueue();
		HashSet<GeographicPoint> visited = new HashSet<>();
		HashMap<GeographicPoint, GeographicPoint> parentMap = new HashMap<>();
		HashMap<GeographicPoint, Double> distances = new HashMap<>();

		GraphNode starterNode = new GraphNode(start);
		toExplore.add(starterNode);
		distances.put(start, 0.0);

		while (!toExplore.isEmpty()) {
			GraphNode curr = toExplore.poll();
			if (!visited.contains(curr.getValue())) {
				visited.add(curr.getValue());
				if (curr.getValue().equals(goal)) {
					return constructPath(start, goal, parentMap);
				} // if found quit loop
				for (GraphNode node : adjList.get(curr.getValue())) {
					if (!visited.contains(node.getValue()) && (!distances.containsKey(node.getValue())
							|| distances.get(curr.getValue()) + node.getLength() < distances.get(node.getValue()))) {
						distances.put(node.getValue(), start.distance(node.getValue()));
						parentMap.put(node.getValue(), curr.getValue());
						node.setLength(start.distance(node.getValue()) + goal.distance(node.getValue()));
						toExplore.add(node);

// hook for visualization
						nodeSearched.accept(node.getValue());
					}
				}
			}
		}
		return null;
	}

	public static void main(String[] args) {
		System.out.print("Making a new map...");
		MapGraph firstMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/testdata/simpletest.map", firstMap);
		System.out.println("DONE.");

		// You can use this method for testing.

		/*
		 * Here are some test cases you should try before you attempt the Week 3 End of
		 * Week Quiz, EVEN IF you score 100% on the programming assignment.
		 */
		/*
		 * MapGraph simpleTestMap = new MapGraph();
		 * GraphLoader.loadRoadMap("data/testdata/simpletest.map", simpleTestMap);
		 * 
		 * GeographicPoint testStart = new GeographicPoint(1.0, 1.0); GeographicPoint
		 * testEnd = new GeographicPoint(8.0, -1.0);
		 * 
		 * System.out.
		 * println("Test 1 using simpletest: Dijkstra should be 9 and AStar should be 5"
		 * ); List<GeographicPoint> testroute =
		 * simpleTestMap.dijkstra(testStart,testEnd); List<GeographicPoint> testroute2 =
		 * simpleTestMap.aStarSearch(testStart,testEnd);
		 * 
		 * 
		 * MapGraph testMap = new MapGraph();
		 * GraphLoader.loadRoadMap("data/maps/utc.map", testMap);
		 * 
		 * // A very simple test using real data testStart = new
		 * GeographicPoint(32.869423, -117.220917); testEnd = new
		 * GeographicPoint(32.869255, -117.216927); System.out.
		 * println("Test 2 using utc: Dijkstra should be 13 and AStar should be 5");
		 * testroute = testMap.dijkstra(testStart,testEnd); testroute2 =
		 * testMap.aStarSearch(testStart,testEnd);
		 * 
		 * 
		 * // A slightly more complex test using real data testStart = new
		 * GeographicPoint(32.8674388, -117.2190213); testEnd = new
		 * GeographicPoint(32.8697828, -117.2244506); System.out.
		 * println("Test 3 using utc: Dijkstra should be 37 and AStar should be 10");
		 * testroute = testMap.dijkstra(testStart,testEnd); testroute2 =
		 * testMap.aStarSearch(testStart,testEnd);
		 */

		/* Use this code in Week 3 End of Week Quiz */
		/*
		 * MapGraph theMap = new MapGraph();
		 * System.out.print("DONE. \nLoading the map...");
		 * GraphLoader.loadRoadMap("data/maps/utc.map", theMap);
		 * System.out.println("DONE.");
		 * 
		 * GeographicPoint start = new GeographicPoint(32.8648772, -117.2254046);
		 * GeographicPoint end = new GeographicPoint(32.8660691, -117.217393);
		 * 
		 * 
		 * List<GeographicPoint> route = theMap.dijkstra(start,end);
		 * List<GeographicPoint> route2 = theMap.aStarSearch(start,end);
		 * 
		 */

	}

}
