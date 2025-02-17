package br.usp.poli.pcs.lti.jmetalhhhelper.imp.algs;

import br.usp.poli.pcs.lti.jmetalhhhelper.core.LowLevelHeuristic;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.TaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.flexiblealgs.Mombi2;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.crossover.DifferentialEvolution;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

/**
 *
 * @author vinicius
 */
public class MOMBI2<S extends Solution<?>> extends Mombi2<S> {

    public MOMBI2(Problem<S> problem, int maxIterations, CrossoverOperator<S> crossover, MutationOperator<S> mutation, SelectionOperator<List<S>, S> selection, SolutionListEvaluator<S> evaluator, String pathWeights) {
        super(problem, maxIterations, crossover, mutation, selection, evaluator, pathWeights);
    }

    protected List<S> generateUsingOpManager(List<S> population) {
        List<S> offspringPopulation = new ArrayList<>();
        int i = 0;
        int max = getMaxPopulationSize();
        while (i < max) {
            LowLevelHeuristic llh = selector.selectOp();
            CrossoverOperator crossoverOp = (CrossoverOperator) llh.getCrossover().getOp();
            MutationOperator mutationOp = (MutationOperator) llh.getMutation().getOp();
            int numberOfParents = crossoverOp.getNumberOfRequiredParents();
            List<S> parents = new ArrayList<>();
            for (int j = 0; j < numberOfParents; j++) {
                parents.add((S) selectionOperator.execute(population));
            }
            if (crossoverOp instanceof DifferentialEvolution) {
                ((DifferentialEvolution) crossoverOp).setCurrentSolution((DoubleSolution) population.get(i));
            }
            List<S> offspring = (List<S>) crossoverOp.execute(parents);
            for (int j = 0; j < offspring.size() && i < max; j++) {
                S s=offspring.get(j);
                if (mutationOp != null) {
                    mutationOp.execute(s);
                }
                //New code-----------
                TaggedSolution s2 = entag(s);
                problem.evaluate((S) s2);//EVALUATION NOW IS HERE
                if (crossoverOp instanceof DifferentialEvolution) {
                    parents.add(population.get(i));
                }
                selector.assignTag(parents, s2, this);
                offspringPopulation.add((S) s2);
                i++;
            }
            selector.updateRewards(parents, offspring);
        }
        return offspringPopulation;
    }

    @Override
    protected List<S> reproduction(List<S> population) {
        if (selector.isHHInUse()) {
            List<S> ax = new ArrayList<>();
            ax.addAll(population);
            return generateUsingOpManager(ax);
        } else {
            List<S> offspringPopulation = super.reproduction(population);
            offspringPopulation = evaluatePopulation(offspringPopulation);
            return offspringPopulation;
        }
    }
}
