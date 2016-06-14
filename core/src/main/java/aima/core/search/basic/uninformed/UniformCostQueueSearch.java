package aima.core.search.basic.uninformed;

import java.util.HashMap;
import java.util.PriorityQueue;

import aima.core.search.basic.queue.QueueSearchForActions;
import aima.core.search.basic.queue.QueueSearchForActionsWrapper;
import aima.core.search.basic.support.BasicFrontierQueue;

/**
 * 
 * @author Ciaran O'Reilly
 * @author Ruediger Lunde
 */
public class UniformCostQueueSearch<A, S> extends QueueSearchForActionsWrapper<A, S> {
	public UniformCostQueueSearch(QueueSearchForActions<A, S> qsearchImpl) {
		super(qsearchImpl);
		qsearchImpl.getNodeFactory().setNodeCostFunction(node -> node.pathCost());
		qsearchImpl.setFrontierSupplier(() -> new BasicFrontierQueue<A, S>(
				() -> new PriorityQueue<>(getQueueSearchForActionsImpl().getNodeFactory()), HashMap::new));
	}
}