package br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics;

import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.extrametrics.Spacing;
import java.io.FileNotFoundException;
import java.util.List;

import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.PointSolution;

/**
 * This class extends Calculator class and perform an evaluation using IGD
 * Indicator.
 */
public class SpacingCalculator extends Calculator {

  /**
   * IGD indicator instance.
   */
  private final Spacing metric;

  /**
   * Instantiates a new Igd calculator.
   *
   * @param numberOfObjectives the number of objectives
   * @param path the path
   * @throws java.io.FileNotFoundException file not found.
   */
  public SpacingCalculator(int numberOfObjectives, String path) throws FileNotFoundException {
    super(numberOfObjectives, path);
    this.metric = new Spacing(paretoTrueFront);
    this.indicatorName = "Spacing";
    this.lowerValuesAreBetter=metric.isTheLowerTheIndicatorValueTheBetter();
  }

  /**
   * Instantiates a new Igd calculator.
   *
   * @param numberOfObjectives the number of objectives
   * @param paretoFront the pareto front
   */
  public SpacingCalculator(int numberOfObjectives, Front paretoFront) {
    super(numberOfObjectives, paretoFront, true);
    this.metric = new Spacing(paretoTrueFront);
    this.indicatorName = "Spacing";
    this.lowerValuesAreBetter=metric.isTheLowerTheIndicatorValueTheBetter();
  }
  
  /**
   * Instantiates a new Igd calculator.
   *
   * @param numberOfObjectives the number of objectives
   * @param paretoFront the pareto front
     * @param normalize
   */
  public SpacingCalculator(int numberOfObjectives, Front paretoFront, boolean normalize) {
    super(numberOfObjectives, paretoFront, normalize);
    this.metric = new Spacing(paretoTrueFront);
    this.indicatorName = "Spacing";
    this.lowerValuesAreBetter=metric.isTheLowerTheIndicatorValueTheBetter();
  }


    @Override
    public double calculate(Front front, double[] maximumValues, double[] minimumValues) {
        double val = 1;
        if (maximumValues != null && minimumValues != null) {
            if (!this.isMinMaxTheSame(maximumValues, minimumValues)) {
                FrontNormalizer frontNormalizer = new FrontNormalizer(minimumValues, maximumValues);
                front = frontNormalizer.normalize(front);
            }
            List<PointSolution> normalizedPopulation = FrontUtils.convertFrontToSolutionList(front);
            val = metric.evaluate(FrontUtils.convertFrontToSolutionList(front));
        }
        return val;
    }
}
