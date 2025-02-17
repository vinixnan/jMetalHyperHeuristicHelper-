package br.usp.poli.pcs.lti.jmetalhhhelper.flexiblealgs;

import br.usp.poli.pcs.lti.jmetalhhhelper.core.DoubleTaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.OpManager;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.PermutationTaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.TaggedSolution;
import java.util.Comparator;
import java.util.List;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.interfaces.LLHInterface;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.impl.DefaultIntegerPermutationSolution;

/**
 * This class extends the algorithm from jMetal and implements operations
 * necessary for algorithm transitions.
 */
/**
 * The type Nsgaii.
 *
 * @param <S> jMetal need.
 */
@SuppressWarnings("serial")
public class Nsgaii<S extends Solution<?>> extends NSGAII<S> implements
        LLHInterface<S> {

    /**
     * Current iteration.
     */
    protected int iterations;
    /**
     * Max Number of iterations.
     */
    protected int maxIterations;

    protected List<S> offspringPopulation;
    
    protected OpManager selector = new OpManager();

    /**
     * Instantiates a new Nsgaii.
     *
     * @param problem the problem
     * @param maxEvaluations the max evaluations
     * @param populationSize the population size
     * @param matingPoolSize
     * @param offspringPopulationSize
     * @param crossoverOperator the crossover operator
     * @param mutationOperator the mutation operator
     * @param selectionOperator the selection operator
     * @param dominanceComparator
     * @param evaluator the evaluator
     */
    public Nsgaii(Problem problem, int maxEvaluations, int populationSize, int matingPoolSize, int offspringPopulationSize, CrossoverOperator crossoverOperator, MutationOperator mutationOperator, SelectionOperator selectionOperator, Comparator dominanceComparator, SolutionListEvaluator evaluator) {
        super(problem, maxEvaluations, populationSize, matingPoolSize, offspringPopulationSize, crossoverOperator, mutationOperator, selectionOperator, dominanceComparator, evaluator);
        maxIterations = maxEvaluations / populationSize;
    }

    @Override
    public void run() {
        this.initMetaheuristic();
        this.execute();
    }

    @Override
    protected boolean isStoppingConditionReached() {
        return iterations >= maxIterations || evaluations >= maxEvaluations;
    }

     @Override
    public List<S> execute() {
        while (!isStoppingConditionReached()) {
            generateNewPopulation();
        }
        return this.population;
    }

    @Override
    public void generateNewPopulation() {
        this.executeMethod();
        iterations++;
    }

    @Override
    public List<S> executeMethod() {
        List<S> matingPopulation = selection(population);
        offspringPopulation = reproduction(matingPopulation);
        offspringPopulation = evaluatePopulation(offspringPopulation);
        population = replacement(population, offspringPopulation);
        updateProgress();
        return population;
    }

    @Override
    public void initMetaheuristic() {
        population = createInitialPopulation();
        population = evaluatePopulation(population);
        initProgress();
        iterations = 1;
    }

    @Override
    public void initMetaheuristic(List pop) {
        population = pop;
        setPopulation((getPopulation()));
        initProgress();
        iterations = 1;
    }

    @Override
    public int getPopulationSize() {
        return maxPopulationSize;
    }

    @Override
    public void setPopulationSize(int populationSize) {
        maxPopulationSize = populationSize;
    }

    @Override
    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    @Override
    public void setCrossoverOperator(CrossoverOperator<S> crossoverOperator) {
        this.crossoverOperator = crossoverOperator;
    }

    @Override
    public void setMutationOperator(MutationOperator<S> mutationOperator) {
        this.mutationOperator = mutationOperator;
    }

    @Override
    public int getIterations() {
        return iterations;
    }

    @Override
    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public List<S> getPopulation() {
        return population;
    }

    @Override
    public void setPopulation(List<S> population) {
        this.population = population;
    }

    public List<S> getOffspringPopulation() {
        return offspringPopulation;
    }

    public void setOffspringPopulation(List<S> offspringPopulation) {
        this.offspringPopulation = offspringPopulation;
    }

    @Override
    public List<S> execute(List<S> inputPop, int evaluations) {
        population = inputPop;
        iterations = 0;
        maxIterations = evaluations / maxPopulationSize;
        return execute();
    }

    @Override
    public void setMaxIterations(int maxIteration) {
        maxIterations = maxIteration;
    }
    
    @Override
    public void initMetaheuristic(List<S> pop, List<S> pop2) {
        population = pop;
        offspringPopulation=pop2;
        population = replacement(population, offspringPopulation);
        iterations = 1;
        evaluations = pop.size();
    }
    
    @Override
    public OpManager getOpLLHManager() {
        return selector;
    }

    @Override
    public TaggedSolution entag(Solution s) {
        TaggedSolution s2;
        if(s instanceof TaggedSolution){
            return (TaggedSolution) s;
        }
        if (problem instanceof AbstractDoubleProblem) {
            s2 = new DoubleTaggedSolution((DefaultDoubleSolution) s);
        } else {
            s2 = new PermutationTaggedSolution((DefaultIntegerPermutationSolution) s);
        }
        return s2;
    }
}
