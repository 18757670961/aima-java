package aima.test.core.unit.search.local;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import aima.core.search.api.Node;
import aima.core.search.basic.local.SimulatedAnnealingSearch;
import aima.core.search.basic.support.BasicNodeFactory;
import aima.core.search.basic.support.BasicProblem;
import aima.test.core.unit.search.support.TestGoAction;


/*
 * @author Anurag Rai
 */
public class SimulatedAnnealingTest {
    Map<String, List<String>> simpleBinaryTreeStateSpace = new HashMap<String, List<String>>() {
		private static final long serialVersionUID = 1L; {
        put("C", Arrays.asList("A", "B"));
        put("E", Arrays.asList("C", "D"));
        put("F", Arrays.asList("X", "D"));
        put("G", Arrays.asList("E", "F"));
        put("I", Arrays.asList("G", "F"));

    }};

    Function<String, Set<TestGoAction>> simpleBinaryTreeActionsFn = state -> {
        if (simpleBinaryTreeStateSpace.containsKey(state)) {
            return new LinkedHashSet<>(simpleBinaryTreeStateSpace.get(state).stream().map(TestGoAction::new).collect(Collectors.toList()));
        }
        return Collections.emptySet();
    };

    BiFunction<String, TestGoAction, String> goActionResultFn = (state, action) -> ((TestGoAction) action).getGoTo();

    //the heuristic function will be represented by the ascii value of the first character in the state name
    ToDoubleFunction<Node<TestGoAction, String>> asciiHeuristicFn = node -> {
        String state = node.state();
        int asciiCode = (int) state.charAt(0);
        return (double) asciiCode;
    };

    @Test
    public void testAsciiHeuristicFunction() {
        SimulatedAnnealingSearch<TestGoAction, String> simulatedAnnealing = new SimulatedAnnealingSearch<>(asciiHeuristicFn);
        BasicNodeFactory<TestGoAction, String> nodeFactory = new BasicNodeFactory<>();
        Node<TestGoAction, String> nodeA = nodeFactory.newRootNode("A");
        Node<TestGoAction, String> nodeB = nodeFactory.newRootNode("B");

        Assert.assertEquals(
                simulatedAnnealing.getHeuristicFunctionH().applyAsDouble(nodeA),
                simulatedAnnealing.getHeuristicFunctionH().applyAsDouble(nodeA),
                0
        );

        Assert.assertEquals(
        		simulatedAnnealing.getHeuristicFunctionH().applyAsDouble(nodeB),
        		simulatedAnnealing.getHeuristicFunctionH().applyAsDouble(nodeB),
                0
        );

        Assert.assertNotEquals(
        		simulatedAnnealing.getHeuristicFunctionH().applyAsDouble(nodeA),
        		simulatedAnnealing.getHeuristicFunctionH().applyAsDouble(nodeB),
                0
        );
    }

    @Test
    public void testAlreadyInGoalState() {
        SimulatedAnnealingSearch<TestGoAction, String> simulatedAnnealing = new SimulatedAnnealingSearch<>(asciiHeuristicFn);

        Assert.assertEquals(
        		Arrays.asList((TestGoAction) null),
                simulatedAnnealing.apply(new BasicProblem<>("A",
                        simpleBinaryTreeActionsFn,
                        goActionResultFn,
                        "A"::equals
                )));

        Assert.assertEquals(
        		Arrays.asList((TestGoAction) null),
                simulatedAnnealing.apply(new BasicProblem<>("B",
                        simpleBinaryTreeActionsFn,
                        goActionResultFn,
                        "B"::equals
                )));
    }

    @Test
    public void testDelE() {
    	SimulatedAnnealingSearch<TestGoAction, String> simulatedAnnealing = new SimulatedAnnealingSearch<>(asciiHeuristicFn);
    	int deltaE = -1;
		double higherTemperature = 30.0;
		double lowerTemperature = 29.5;

		Assert.assertTrue(simulatedAnnealing.probabilityOfAcceptance(lowerTemperature,
				deltaE) < simulatedAnnealing.probabilityOfAcceptance(higherTemperature,
				deltaE));
    }
}
