package it.univr.trees.approximatingmodels;

import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

import it.univr.trees.assetderivativevaluation.products.EuropeanNonPathDependentOption;
import it.univr.trees.assetderivativevaluation.products.NotTooNiceEuropeanNonPathDependentOptionForTrinomialModel;
import net.finmath.functions.AnalyticFormulas;
import net.finmath.plots.Named;
import net.finmath.plots.Plot2D;

public class ApproximatingTrinomialModelTest {

	public static void main(String[] args) {
		double spotPrice = 100;
		double riskFreeRate = 0.0;
		double volatility = 0.2;
		double lastTime = 1.0;

		double strike = 100;


		/*
		 * We now test an option: we want to plot the results we get for our approximating models when we
		 * increase the number of times. In the same plot, we want to show also the analyic price of the option,
		 * as a benchmark.
		 */
		DoubleUnaryOperator payoffFunction = (x) -> (x - strike > 0 ? 1.0 : 0.0);//European call
		
		NotTooNiceEuropeanNonPathDependentOptionForTrinomialModel ourOption =
				new NotTooNiceEuropeanNonPathDependentOptionForTrinomialModel(lastTime, payoffFunction);

		
		EuropeanNonPathDependentOption ourOtherOption =	new EuropeanNonPathDependentOption(lastTime, payoffFunction);

		
		
		/*
		 * We use the Plot2D class of finmath-lib-plot-extensions. In order to do that, we have to define the
		 * functions to plot as objects of type DoubleUnaryOperator.
		 * In our case, we want these functions to take the number of times and return the prices approximated
		 * with this number of times. For us numberOfTimesForFunction should be an int, but in order to define
		 * a DoubleUnaryOperator one should take a double. So we first treat it as a double and then we downcast
		 * it when passing it to the getValue of EuropeanNonPathDependentOption.
		 */
		DoubleUnaryOperator numberOfTimesToPriceTrinomialModel = (numberOfTimesForFunction) -> {
			BoyleModel ourModelForFunction = new BoyleModel(spotPrice, riskFreeRate,
					volatility, lastTime, (int) numberOfTimesForFunction);		
			return ourOption.getValue(ourModelForFunction);
		};
		
		
		DoubleUnaryOperator numberOfTimesToPriceCoxRossModel = (numberOfTimesForFunction) -> {
			CoxRossRubinsteinModel ourModelForFunction = new CoxRossRubinsteinModel(spotPrice, riskFreeRate,
					volatility, lastTime, (int) numberOfTimesForFunction);		
			return ourOtherOption.getValue(ourModelForFunction);
		};
		
		DoubleUnaryOperator numberOfTimesToPriceLeisenReimerModel = (numberOfTimesForFunction) -> {
			LeisenReimerModel ourModelForFunction = new LeisenReimerModel(spotPrice, riskFreeRate,
					volatility, lastTime, (int) numberOfTimesForFunction, strike);		
			return ourOtherOption.getValue(ourModelForFunction);
		};
		
		DoubleUnaryOperator numberOfTimesToPriceJarrowRuddModel = (numberOfTimesForFunction) -> {
			JarrowRuddModel ourModelForFunction = new JarrowRuddModel(spotPrice, riskFreeRate,
					volatility, lastTime, (int) numberOfTimesForFunction);		
			return ourOtherOption.getValue(ourModelForFunction);
		};

		
		/*
		 * This is the DoubleUnaryOperator to plot the analytic price. "Dummy" in the sense that it is a function
		 * that always gives the same value.
		 */
		DoubleUnaryOperator dummyFunctionBlackScholesPrice = (numberOfTimesForFunction) -> {
			return AnalyticFormulas.blackScholesDigitalOptionValue(spotPrice, riskFreeRate, volatility, lastTime, strike);
			//return AnalyticFormulas.blackScholesOptionValue(spotPrice,riskFreeRate,volatility, lastTime,strike,true);
		};
		
		
		
		
		//we now plot the functions from a minimum number of points to a maximum number of points
		int maxNumberOfTimes = 500;
		int minNumberOfTimes = 10;
		
		//look at the Plot2D constructor.
		final Plot2D plotBoyle = new Plot2D(minNumberOfTimes, maxNumberOfTimes, maxNumberOfTimes-minNumberOfTimes+1,
				Arrays.asList(
				new Named<DoubleUnaryOperator>("Boyle", numberOfTimesToPriceTrinomialModel),
				new Named<DoubleUnaryOperator>("Cox-Ross-Rubinstein", numberOfTimesToPriceCoxRossModel),
				new Named<DoubleUnaryOperator>("Jarrow-Rudd", numberOfTimesToPriceJarrowRuddModel),
				new Named<DoubleUnaryOperator>("Leisen-Reimer", numberOfTimesToPriceLeisenReimerModel),
				new Named<DoubleUnaryOperator>("Black-Scholes", dummyFunctionBlackScholesPrice))
				);	
		plotBoyle.setXAxisLabel("Number of discretized times");
		plotBoyle.setYAxisLabel("Price");
		plotBoyle.setIsLegendVisible(true);
		plotBoyle.show();
	}

}
