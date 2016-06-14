package aima.test.core.unit.search.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import aima.core.environment.map2d.SimplifiedRoadMapOfPartOfRomania;
import aima.core.search.api.Problem;
import aima.core.search.basic.support.BasicProblem;

public class ProblemFactory {

	public static Problem<GoAction, InLocationState> getSimplifiedRoadMapOfPartOfRomania(String initialState,
			final String... goalStates) {
		final SimplifiedRoadMapOfPartOfRomania simplifidRoadMapOfPartOfRomania = new SimplifiedRoadMapOfPartOfRomania();
		final Set<String> locations = new HashSet<>(simplifidRoadMapOfPartOfRomania.getLocations());
		if (!locations.contains(initialState)) {
			throw new IllegalArgumentException(
					"Initial State " + initialState + " is not a member of the state space.");
		}
		for (String goalState : goalStates) {
			if (!locations.contains(goalState)) {
				throw new IllegalArgumentException("Goal State " + goalState + " is not a member of the state space.");
			}
		}

		Function<InLocationState, Set<GoAction>> actionsFn = inLocationState -> {
			Set<GoAction> actions = new LinkedHashSet<>();
			for (String toLocation : simplifidRoadMapOfPartOfRomania
					.getLocationsLinkedTo(inLocationState.getLocation())) {
				actions.add(new GoAction(toLocation));
			}
			return actions;
		};

		BiFunction<InLocationState, GoAction, InLocationState> resultFn = (state,
				action) -> new InLocationState(action.getGoTo());

		Predicate<InLocationState> goalTestPredicate = inLocationState -> {
			for (String goalState : goalStates) {
				if (goalState.equals(inLocationState.getLocation())) {
					return true;
				}
			}
			return false;
		};

		return new BasicProblem<>(new InLocationState(initialState), actionsFn, resultFn, goalTestPredicate);
	}

	public static List<String> getSimpleBinaryTreeStates() {
		return Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "0", "P", "Q", "R",
				"S", "T", "U", "V", "W", "X", "Y", "Z");
	}

	public static Problem<String, String> getSimpleBinaryTreeProblem(String initialState, String... goalStates) {
		return getSimpleBinaryTreeProblem(initialState, getSimpleBinaryTreeStates(), goalStates);
	}

	public static Problem<String, String> getSimpleBinaryTreeProblem(String initialState, List<String> states,
			final String... goalStates) {
		Set<String> stateSet = new LinkedHashSet<>(states);
		if (stateSet.size() != states.size()) {
			throw new IllegalArgumentException("States provided contain duplicates");
		}
		if (!stateSet.contains(initialState)) {
			throw new IllegalArgumentException(
					"Initial State " + initialState + " is not a member of the state space.");
		}
		for (String goalState : goalStates) {
			if (!stateSet.contains(goalState)) {
				throw new IllegalArgumentException("Goal State " + goalState + " is not a member of the state space.");
			}
		}
		final Map<String, List<String>> simpleBinaryTreeStateSpace = new HashMap<>();
		Queue<String> children = new LinkedList<>(states.subList(1, states.size()));
		for (int i = 0; i < states.size(); i++) {
			String node = states.get(i);
			List<String> targets = new ArrayList<>();
			for (int c = 0; c < 2; c++) {
				if (!children.isEmpty()) {
					targets.add(children.remove());
				}
			}
			simpleBinaryTreeStateSpace.put(node, targets);
		}

		Function<String, Set<String>> actionsFn = state -> {
			if (simpleBinaryTreeStateSpace.containsKey(state)) {
				return new LinkedHashSet<>(simpleBinaryTreeStateSpace.get(state));
			}
			return Collections.emptySet();
		};

		BiFunction<String, String, String> resultFn = (state, action) -> action;

		Predicate<String> goalTestPredicate = state -> {
			for (String goalState : goalStates) {
				if (goalState.equals(state)) {
					return true;
				}
			}
			return false;
		};

		return new BasicProblem<>(initialState, actionsFn, resultFn, goalTestPredicate);
	}
}