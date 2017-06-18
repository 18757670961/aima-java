package aima.core.search.uninformed;

import java.security.cert.PKIXRevocationChecker;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import aima.core.agent.Action;
import aima.core.search.framework.Metrics;
import aima.core.search.framework.Node;
import aima.core.search.framework.NodeExpander;
import aima.core.search.framework.SearchForActions;
import aima.core.search.framework.SearchForStates;
import aima.core.search.framework.SearchUtils;
import aima.core.search.framework.problem.Problem;
import aima.core.util.CancelableThread;

/**
 * Artificial Intelligence A Modern Approach (3rd Edition): Figure 3.18, page
 * 89.<br>
 * <br>
 * 
 * <pre>
 * function ITERATIVE-DEEPENING-SEARCH(problem) returns a solution, or failure
 *   for depth = 0 to infinity  do
 *     result &lt;- DEPTH-LIMITED-SEARCH(problem, depth)
 *     if result != cutoff then return result
 * </pre>
 * 
 * Figure 3.18 The iterative deepening search algorithm, which repeatedly
 * applies depth-limited search with increasing limits. It terminates when a
 * solution is found or if the depth- limited search returns failure, meaning
 * that no solution exists.
 * 
 * @author Ravi Mohan
 * @author Ciaran O'Reilly
 * @author Ruediger Lunde
 */
public class IterativeDeepeningSearch<S, A> implements SearchForActions<S, A>, SearchForStates<S, A> {
	public static final String METRIC_NODES_EXPANDED = "nodesExpanded";
	public static final String METRIC_PATH_COST = "pathCost";

	private final NodeExpander<S, A> nodeExpander;
	private final Metrics metrics;

	public IterativeDeepeningSearch() {
		this(new NodeExpander<>());
	}
	
	public IterativeDeepeningSearch(NodeExpander<S, A> nodeExpander) {
		this.nodeExpander = nodeExpander;
		this.metrics = new Metrics();
	}
	
	
	// function ITERATIVE-DEEPENING-SEARCH(problem) returns a solution, or
	// failure
	@Override
	public Optional<List<A>> findActions(Problem<S, A> p) {
		nodeExpander.useParentLinks(true);
		Optional<Node<S, A>> node = findNode(p);
		return SearchUtils.toActions(node);
	}

	@Override
	public Optional<S> findState(Problem<S, A> p) {
		nodeExpander.useParentLinks(false);
		Optional<Node<S, A>> node = findNode(p);
		return SearchUtils.toState(node);
	}

	private Optional<Node<S, A>> findNode(Problem<S, A> p) {
		clearInstrumentation();
		// for depth = 0 to infinity do
		for (int i = 0; !CancelableThread.currIsCanceled(); i++) {
			// result <- DEPTH-LIMITED-SEARCH(problem, depth)
			DepthLimitedSearch<S, A> dls = new DepthLimitedSearch<>(i, nodeExpander);
			Optional<Node<S, A>> result = dls.findNode(p);
			updateMetrics(dls.getMetrics());
			// if result != cutoff then return result
			if (!dls.isCutoffResult(result))
				return result;
		}
		return Optional.empty();
	}
	
	@Override
	public Metrics getMetrics() {
		return metrics;
	}

	/**
	 * Sets the nodes expanded and path cost metrics to zero.
	 */
	private void clearInstrumentation() {
		metrics.set(METRIC_NODES_EXPANDED, 0);
		metrics.set(METRIC_PATH_COST, 0);
	}

	@Override
	public void addNodeListener(Consumer<Node<S, A>> listener)  {
		nodeExpander.addNodeListener(listener);
	}

	@Override
	public boolean removeNodeListener(Consumer<Node<S, A>> listener) {
		return nodeExpander.removeNodeListener(listener);
	}

	//
	// PRIVATE METHODS
	//

	private void updateMetrics(Metrics dlsMetrics) {
		metrics.set(METRIC_NODES_EXPANDED,
				metrics.getInt(METRIC_NODES_EXPANDED) + dlsMetrics.getInt(METRIC_NODES_EXPANDED));
		metrics.set(METRIC_PATH_COST, dlsMetrics.getDouble(METRIC_PATH_COST));
	}
}