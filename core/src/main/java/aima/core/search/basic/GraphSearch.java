package aima.core.search.basic;

import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Supplier;

import aima.core.search.api.Node;
import aima.core.search.api.NodeFactory;
import aima.core.search.api.Problem;
import aima.core.search.api.Search;
import aima.core.search.basic.support.BasicFrontierQueue;
import aima.core.search.basic.support.BasicNodeFactory;

/**
 * Artificial Intelligence A Modern Approach (4th Edition): Figure ??, page ??. <br>
 * <br>
 *
 * <pre>
 * function GRAPH-SEARCH(problem) returns a solution, or failure
 *   initialize the frontier using the initial state of problem
 *   initialize the explored set to be empty
 *   loop do
 *     if the frontier is empty then return failure
 *     choose a leaf node and remove it from the frontier
 *     if the node contains a goal state then return the corresponding solution
 *     add the node to the explored set
 *     expand the chosen node, adding the resulting nodes to the frontier
 *       only if not in the frontier or explored set
 * </pre>
 *
 * Figure ?? An informal description of the general graph-search algorithm.
 *
 *
 * @author Ciaran O'Reilly
 */
public class GraphSearch<A, S> implements Search<A, S> {
	
	private NodeFactory<A, S>           nodeFactory;
    private Supplier<Queue<Node<A, S>>> frontierSupplier;
    private Supplier<Set<S>>            exploredSupplier;
    
    public GraphSearch() {
    	this(new BasicNodeFactory<>(), BasicFrontierQueue::new, HashSet::new);
    }
    
    public GraphSearch(NodeFactory<A, S> nodeFactory, Supplier<Queue<Node<A, S>>> frontierSupplier, Supplier<Set<S>> exploredSupplier) {
    	this.nodeFactory      = nodeFactory;
    	this.frontierSupplier = frontierSupplier;
    	this.exploredSupplier = exploredSupplier;
    }

    // function GRAPH-SEARCH(problem) returns a solution, or failure
    @Override
    public List<A> apply(Problem<A, S> problem) {
        // initialize the frontier using the initial state of problem
        Queue<Node<A, S>> frontier = frontierSupplier.get();
        frontier.add(nodeFactory.newRootNode(problem.initialState(), 0));
        // initialize the explored set to be empty
        Set<S> explored = exploredSupplier.get();
        // loop do
        while (true) {
            // if the frontier is empty then return failure
            if (frontier.isEmpty()) { return failure(); }
            // choose a leaf node and remove it from the frontier
            Node<A, S> node = frontier.remove();
            // if the node contains a goal state then return the corresponding solution
            if (isGoalState(node, problem)) { return solution(node); }
            // add the node to the explored set
            explored.add(node.state());
            // expand the chosen node, adding the resulting nodes to the frontier
            for (A action : problem.actions(node.state())) {
                Node<A, S> child = nodeFactory.newChildNode(problem, node, action);
                // only if not in the frontier or explored set
                if (!(frontier.contains(child.state()) || explored.contains(child.state()))) {
                    frontier.add(child);
                }
            }
        }
    }
}
