package aima.gui.demo.agent;

import aima.core.agent.Action;
import aima.core.environment.vacuum.*;
import aima.core.search.nondeterministic.NondeterministicProblem;

/**
 * Applies AND-OR-GRAPH-SEARCH to a non-deterministic version of the Vacuum World.
 * 
 * 
 * @author Andrew Brown
 * @auther Ruediger Lunde
 */
public class NondeterministicVacuumEnvironmentDemo {
	public static void main(String[] args) {
		System.out.println("NON-DETERMINISTIC-VACUUM-ENVIRONMENT DEMO");
		System.out.println("");
		startAndOrSearch();
	}

	private static void startAndOrSearch() {
		System.out.println("AND-OR-GRAPH-SEARCH");
	    
	    NondeterministicVacuumAgent agent = new NondeterministicVacuumAgent
                (percept -> percept); // percept == env state!

        // create world
        NondeterministicVacuumEnvironment world = new NondeterministicVacuumEnvironment
                (VacuumEnvironment.LocationState.Dirty, VacuumEnvironment.LocationState.Dirty);
        world.addAgent(agent, VacuumEnvironment.LOCATION_A);

        // provide the agent with a problem formulation so that a contingency plan can be computed.
        VacuumEnvironmentState state = (VacuumEnvironmentState) world.getCurrentState();
        NondeterministicProblem<VacuumEnvironmentState, Action> problem = new NondeterministicProblem<>(
                state,
                VacuumWorldFunctions::getActions,
                VacuumWorldFunctions.createResultsFunction(agent),
                VacuumWorldFunctions::testGoal,
                (s, a, sPrimed) -> 1.0);
        agent.setProblem(problem);
        
        // execute and show plan
        System.out.println("Initial Plan: " + agent.getContingencyPlan());
        StringBuilder sb = new StringBuilder();
        world.addEnvironmentView(new VacuumEnvironmentViewActionTracker(sb));
        world.stepUntilDone();
        System.out.println("Remaining Plan: " + agent.getContingencyPlan());
        System.out.println("Actions Taken: " + sb);
        System.out.println("Final State: " + world.getCurrentState());
	}
}
