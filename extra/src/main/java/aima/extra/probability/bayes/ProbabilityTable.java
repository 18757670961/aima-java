package aima.extra.probability.bayes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import aima.extra.probability.ProbabilityNumber;
import aima.extra.probability.RandomVariable;
import aima.extra.probability.constructs.ProbabilityUtilities;
import aima.core.util.math.MixedRadixInterval;
import aima.extra.probability.ProbabilityComputation;
import aima.extra.util.ListOps;

/**
 * This class represents a probability table that is indexed by domain value
 * assignments to finite random variables. The values comprising the
 * ProbabilityTable correspond to the various assignments to all the random
 * variables constituting the distribution. This ProbabilityTable could
 * represent a joint distribution or a conditional probability distribution for
 * finite random variables, although more specific and legal operations for CPDs
 * are defined in {@link ConditionalProbabilityTable}.
 * 
 * @author Ciaran O'Reilly
 * @author Nagaraj Poti
 */
public class ProbabilityTable extends AbstractProbabilityTable implements Factor {

	// Internal fields

	/**
	 * Sum of all values.
	 */
	private ProbabilityNumber sumOfValues = null;

	// Constructor

	/**
	 * Constructor initializes ProbabilityTable with values.
	 * 
	 * @param vars
	 *            is an ordered array of all random variables.
	 * @param values
	 *            is an ordered array of probability values that form the
	 *            probability table.
	 * @param clazz
	 *            specifies the class type of the ProbabilityNumber
	 *            implementation to use.
	 * 
	 * @see MixedRadixInterval class to understand how indexes correspond to
	 *      assignments of values to random variables.
	 */
	public ProbabilityTable(RandomVariable[] vars, ProbabilityNumber[] values,
			Class<? extends ProbabilityNumber> clazz) {
		super(vars, values, clazz);
	}

	/**
	 * Constructor initializes ProbabilityTable with values.
	 * 
	 * @param vars
	 *            is an ordered list of all random variables.
	 * @param values
	 *            is an ordered list of probability values that form the
	 *            probability table.
	 * @param clazz
	 *            specifies the class type of the ProbabilityNumber
	 *            implementation to use.
	 * 
	 * @see MixedRadixInterval class to understand how indexes correspond to
	 *      assignments of values to random variables.
	 */
	public ProbabilityTable(List<RandomVariable> vars, List<ProbabilityNumber> values,
			Class<? extends ProbabilityNumber> clazz) {
		super(vars, values, clazz);
	}

	// Public methods

	// START-ProbabilityMass

	@Override
	public ProbabilityTable setValue(ProbabilityNumber value, Object... eventValues) {
		List<ProbabilityNumber> newValues = new ArrayList<ProbabilityNumber>(this.values);
		int idx = this.getIndex(eventValues);
		newValues.set(idx, value);
		ProbabilityTable newTable = new ProbabilityTable(this.randomVariables, newValues, this.clazz);
		return newTable;
	}

	@Override
	public ProbabilityTable setValue(ProbabilityNumber value, Map<RandomVariable, Object> event) {
		List<ProbabilityNumber> newValues = new ArrayList<ProbabilityNumber>(this.values);
		int idx = this.getIndex(event);
		newValues.set(idx, value);
		ProbabilityTable newTable = new ProbabilityTable(this.randomVariables, newValues, this.clazz);
		return newTable;
	}

	// END-ProbabilityMass

	// START-CategoricalDistribution

	@Override
	public ProbabilityTable normalize() {
		ProbabilityNumber sum = this.getSum();
		List<ProbabilityNumber> normalizedValues = this.values.stream().map(value -> value.divide(sum))
				.collect(Collectors.toList());
		ProbabilityTable result = new ProbabilityTable(this.randomVariables, normalizedValues, this.clazz);
		return result;
	}

	// END-CategoricalDistribution

	/**
	 * @return size (number of values) of the probability table.
	 */
	public int size() {
		return this.values.size();
	}

	/**
	 * Lazy computation of sum (recompute sum only if any probability value is
	 * changed).
	 * 
	 * @return sum of all ProbabilityTable values.
	 */
	public ProbabilityNumber getSum() {
		if (null == this.sumOfValues) {
			ProbabilityComputation adder = new ProbabilityComputation();
			ProbabilityNumber initValue = this.probFactory.valueOf(BigDecimal.ZERO);
			this.sumOfValues = this.values.stream().reduce(initValue, adder::add);
		}
		return this.sumOfValues;
	}

	// START-Factor

	@Override
	public List<RandomVariable> getArgumentVariables() {
		return this.randomVariables;
	}

	public ProbabilityTable sumOut(RandomVariable... sumOutVars) {
		return this.marginalize(sumOutVars);
	}

	public ProbabilityTable pointwiseProduct(Factor multiplier) {
		// TODO - Check if random variables are valid
		ProbabilityTable secondFactor = (ProbabilityTable) multiplier;
		List<RandomVariable> productRandomVariables = ListOps.union(this.randomVariables, secondFactor.randomVariables);
		return this.pointwiseProductPOS(multiplier, productRandomVariables);
	}

	@Override
	public ProbabilityTable pointwiseProductPOS(Factor multiplier, List<RandomVariable> prodVarOrder) {
		ProbabilityTable secondFactor = (ProbabilityTable) multiplier;
		List<Integer> term1Idx = ListOps.getIntersectionIdx(prodVarOrder, this.randomVariables);
		List<Integer> term2Idx = ListOps.getIntersectionIdx(prodVarOrder, secondFactor.randomVariables);
		int[] productRadices = prodVarOrder.stream().mapToInt(var -> var.getDomain().size()).toArray();
		MixedRadixInterval productQueryMRI = new MixedRadixInterval(productRadices);
		int productValuesSize = ProbabilityUtilities.expectedSizeofProbabilityTable(prodVarOrder);
		List<ProbabilityNumber> productValues = new ArrayList<ProbabilityNumber>(Collections.nCopies(productValuesSize, this.probFactory.valueOf(BigDecimal.ZERO)));
		productQueryMRI.stream().forEach(possibleWorldNumerals -> {
			int[] term1Numerals = IntStream.range(0, possibleWorldNumerals.length).filter(term1Idx::contains).sorted()
					.toArray();
			int[] term2Numerals = IntStream.range(0, possibleWorldNumerals.length).filter(term2Idx::contains).sorted()
					.toArray();
			int resultIdx = productQueryMRI.getValueFor(possibleWorldNumerals).intValue();
			int term1ValueIdx = this.queryMRI.getValueFor(term1Numerals).intValue();
			int term2ValueIdx = secondFactor.queryMRI.getValueFor(term2Numerals).intValue();
			ProbabilityNumber operand1 = this.values.get(term1ValueIdx);
			ProbabilityNumber operand2 = multiplier.getValues().get(term2ValueIdx);
			productValues.set(resultIdx, operand1.multiply(operand2));
		});
		ProbabilityTable product = new ProbabilityTable(prodVarOrder, productValues, this.clazz);
		return product;
	}

	// END-Factor

	/**
	 * String representation of ProbabilityTable.
	 */
	@Override
	public String toString() {
		return this.values.stream().map(value -> value.toString()).collect(Collectors.joining(", "));
	}
}