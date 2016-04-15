package aima.core.search.framework;

import java.util.Collections;
import java.util.List;

import aima.core.agent.Action;
import aima.core.util.CancelableThread;
import aima.core.util.datastructure.Queue;

/**
 * Base class for search implementations, especially for <code>TreeSearch</code> and <code>GraphSearch</code>.
 * @author Ravi Mohan
 * @author Ciaran O'Reilly
 * @author Mike Stampone
 * @author Ruediger Lunde
 */
public abstract class QueueSearch extends NodeExpander {
	public static final String METRIC_QUEUE_SIZE = "queueSize";
	public static final String METRIC_MAX_QUEUE_SIZE = "maxQueueSize";
	public static final String METRIC_PATH_COST = "pathCost";

	protected Queue<Node> frontier;

	private boolean checkGoalBeforeAddingToFrontier = false;

	/**
	 * Returns a list of actions to the goal if the goal was found, a list
	 * containing a single NoOp Action if already at the goal, or an empty list
	 * if the goal could not be found. This template method provides a base
	 * for tree and graph search implementations. It can be customized by
	 * overriding some primitive operations, especially
	 * <code>getResultingNodesToAddToFrontier</code>.
	 * 
	 * @param problem
	 *            the search problem
	 * @param frontier
	 *            the collection of nodes that are waiting to be expanded
	 * 
	 * @return a list of actions to the goal if the goal was found, a list
	 *         containing a single NoOp Action if already at the goal, or an
	 *         empty list if the goal could not be found.
	 */
	public List<Action> search(Problem problem, Queue<Node> frontier) {
		this.frontier = frontier;

		clearInstrumentation();
		// initialize the frontier using the initial state of the problem
		Node root = new Node(problem.getInitialState());
		if (isCheckGoalBeforeAddingToFrontier()) {
			if (SearchUtils.isGoalState(problem, root))
				return getSolution(root);
		}
		frontier.insert(root);
		updateMetrics(frontier.size());
		while (!isFrontierEmpty() && !CancelableThread.currIsCanceled()) {
			// choose a leaf node and remove it from the frontier
			Node nodeToExpand = popNodeFromFrontier();
			updateMetrics(frontier.size());
			// Only need to check the nodeToExpand if have not already
			// checked before adding to the frontier
			if (!isCheckGoalBeforeAddingToFrontier()) {
				// if the node contains a goal state then return the
				// corresponding solution
				if (SearchUtils.isGoalState(problem, nodeToExpand))
					return getSolution(nodeToExpand);
			}
			// expand the chosen node, adding the resulting nodes to the
			// frontier
			for (Node successor : getResultingNodesToAddToFrontier(nodeToExpand, problem)) {
				if (isCheckGoalBeforeAddingToFrontier()) {
					if (SearchUtils.isGoalState(problem, successor))
						return getSolution(successor);
				}
				frontier.insert(successor);
			}
			updateMetrics(frontier.size());
		}
		// if the frontier is empty then return failure
		return failure();
	}

	/**
	 * Primitive operation which decides if nodes for all successor states are
	 * added to the frontier or only a selected subset.
	 */
	protected abstract List<Node> getResultingNodesToAddToFrontier(Node nodeToExpand, Problem p);

	public boolean isCheckGoalBeforeAddingToFrontier() {
		return checkGoalBeforeAddingToFrontier;
	}

	public void setCheckGoalBeforeAddingToFrontier(boolean checkGoalBeforeAddingToFrontier) {
		this.checkGoalBeforeAddingToFrontier = checkGoalBeforeAddingToFrontier;
	}

	/**
	 * Removes and returns the node at the head of the frontier.
	 * 
	 * @return the node at the head of the frontier.
	 */
	protected Node popNodeFromFrontier() {
		return frontier.pop();
	}

	protected boolean isFrontierEmpty() {
		return frontier.isEmpty();
	}

	protected List<Action> getSolution(Node node) {
		metrics.set(METRIC_PATH_COST, node.getPathCost());
		return SearchUtils.actionsFromNodes(node.getPathFromRoot());
	}

	@Override
	public void clearInstrumentation() {
		super.clearInstrumentation();
		metrics.set(METRIC_QUEUE_SIZE, 0);
		metrics.set(METRIC_MAX_QUEUE_SIZE, 0);
		metrics.set(METRIC_PATH_COST, 0);
	}

	private void updateMetrics(int queueSize) {

		metrics.set(METRIC_QUEUE_SIZE, queueSize);
		int maxQSize = metrics.getInt(METRIC_MAX_QUEUE_SIZE);
		if (queueSize > maxQSize) {
			metrics.set(METRIC_MAX_QUEUE_SIZE, queueSize);
		}
	}

	public int getQueueSize() {
		return metrics.getInt("queueSize");
	}

	public int getMaxQueueSize() {
		return metrics.getInt(METRIC_MAX_QUEUE_SIZE);
	}

	public double getPathCost() {
		return metrics.getDouble(METRIC_PATH_COST);
	}

	//
	// PRIVATE METHODS
	//
	private List<Action> failure() {
		return Collections.emptyList();
	}
}