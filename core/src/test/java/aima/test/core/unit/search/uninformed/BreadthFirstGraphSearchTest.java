package aima.test.core.unit.search.uninformed;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import aima.core.environment.map2d.SimplifiedRoadMapOfPartOfRomania;
import aima.core.search.api.Problem;
import aima.core.search.api.SearchForActionsFunction;
import aima.core.search.basic.example.ExampleBreadthFirstSearch;
import aima.core.search.basic.queue.GraphGoalTestedFirstQueueSearch;
import aima.core.search.basic.uninformed.BreadthFirstQueueSearch;
import aima.test.core.unit.search.support.GoAction;
import aima.test.core.unit.search.support.ProblemFactory;

@RunWith(Parameterized.class)
public class BreadthFirstGraphSearchTest {

	@Parameters(name = "{index}: {0}")
	public static Collection<Object[]> implementations() {
		return Arrays.asList(new Object[][] { { "ExampleBreadthFirstSearch" }, { "BreadthFirstQueueSearch" } });
	}

	@Parameter
	public String searchFunctionName;

	public <A, S> List<A> searchForActions(Problem<A, S> problem) {
		SearchForActionsFunction<A, S> searchForActionsFunction;
		if ("ExampleBreadthFirstSearch".equals(searchFunctionName)) {
			searchForActionsFunction = new ExampleBreadthFirstSearch<A, S>();
		} else {
			searchForActionsFunction = new BreadthFirstQueueSearch<A, S>(new GraphGoalTestedFirstQueueSearch<>());
		}

		return searchForActionsFunction.apply(problem);
	}

	@Test
	public void testReachableSimpleBinaryTreeGoals() {
		Assert.assertEquals(Arrays.asList((String) null),
				searchForActions(ProblemFactory.getSimpleBinaryTreeProblem("A", "A")));

		Assert.assertEquals(Arrays.asList("B"), searchForActions(ProblemFactory.getSimpleBinaryTreeProblem("A", "B")));

		Assert.assertEquals(Arrays.asList("C"), searchForActions(ProblemFactory.getSimpleBinaryTreeProblem("A", "C")));

		Assert.assertEquals(Arrays.asList("B", "E"),
				searchForActions(ProblemFactory.getSimpleBinaryTreeProblem("A", "E")));

		Assert.assertEquals(Arrays.asList("C", "F", "L"),
				searchForActions(ProblemFactory.getSimpleBinaryTreeProblem("A", "L")));
	}

	@Test
	public void testUnreachableSimpleBinaryTreeGoals() {
		Assert.assertEquals(Collections.<String> emptyList(),
				searchForActions(ProblemFactory.getSimpleBinaryTreeProblem("B", "A")));
	}
	
	@Test
	public void testReachableSimplifiedRoadmapOfPartOfRomania() {
		Assert.assertEquals(Arrays.asList((String) null),
				searchForActions(ProblemFactory.getSimplifiedRoadMapOfPartOfRomania(
						SimplifiedRoadMapOfPartOfRomania.ARAD, SimplifiedRoadMapOfPartOfRomania.ARAD)));

		Assert.assertEquals(
				Arrays.asList(new GoAction(SimplifiedRoadMapOfPartOfRomania.SIBIU),
						new GoAction(SimplifiedRoadMapOfPartOfRomania.FAGARAS),
						new GoAction(SimplifiedRoadMapOfPartOfRomania.BUCHAREST)),
				searchForActions(ProblemFactory.getSimplifiedRoadMapOfPartOfRomania(
						SimplifiedRoadMapOfPartOfRomania.ARAD, SimplifiedRoadMapOfPartOfRomania.BUCHAREST)));
	}
}
