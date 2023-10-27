package it.univr.trees.approximatingmodels;


import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

import it.univr.trees.assetderivativevaluation.products.EuropeanNonPathDependentOption;
import it.univr.usefulmethodsarrays.UsefulMethodsForArrays;
import net.finmath.functions.AnalyticFormulas;
import net.finmath.plots.Named;
import net.finmath.plots.Plot2D;


public class ApproximatingModelsTest {


	public static void main(String[] strings) throws Exception {

		double spotPrice = 100;
		double riskFreeRate = 0.0;
		double volatility = 0.2;
		double lastTime = 1.0;
		int numberOfTimes = 100;

		double strike = 80;
		
		//first test for Cox-Ross-Rubinstein: we just look at its epxected value
		CoxRossRubinsteinModel firstApproximatingModel = new CoxRossRubinsteinModel(
				spotPrice, riskFreeRate, volatility,  lastTime, numberOfTimes);
		
		double[] valuesAtFinalTime =
				firstApproximatingModel.getValuesAtGivenTime(lastTime);
		
		double[] probabilitiesOfValuesAtFinalTime =
				firstApproximatingModel.getValuesProbabilitiesAtGivenTime(lastTime);

		double expectedValue =
				UsefulMethodsForArrays.getScalarProductTwoArrays(valuesAtFinalTime, probabilitiesOfValuesAtFinalTime);

		System.out.println("The expected value at final time for CRB is: " + expectedValue);
		
		
		//first test for Leisen-Reimer: we just look at its epxected value
		LeisenReimerModel secondApproximatingModel = new LeisenReimerModel(
				spotPrice, riskFreeRate, volatility,  lastTime, numberOfTimes, strike);
		
		double[] valuesAtFinalTimeLeisenReimer =
				secondApproximatingModel.getValuesAtGivenTime(lastTime);
		
		double[] probabilitiesOfValuesAtFinalTimeLeisenReimer =
				secondApproximatingModel.getValuesProbabilitiesAtGivenTime(lastTime);

		double expectedValueLeisenReimer =
				UsefulMethodsForArrays.getScalarProductTwoArrays(valuesAtFinalTimeLeisenReimer, probabilitiesOfValuesAtFinalTimeLeisenReimer);

		System.out.println("The expected value at final time for LR is: " + expectedValueLeisenReimer);
		
		//first test for Jarrow-Rudd: we just look at its epxected value
		JarrowRuddModel thirdApproximatingModel = new JarrowRuddModel(
				spotPrice, riskFreeRate, volatility,  lastTime, numberOfTimes);
		
		double[] valuesAtFinalTimeJarrowRudd =
				thirdApproximatingModel.getValuesAtGivenTime(lastTime);
		
		double[] probabilitiesOfValuesAtFinalTimeJarrowRudd =
				thirdApproximatingModel.getValuesProbabilitiesAtGivenTime(lastTime);

		double expectedValueJarrowRudd =
				UsefulMethodsForArrays.getScalarProductTwoArrays(valuesAtFinalTimeJarrowRudd, probabilitiesOfValuesAtFinalTimeJarrowRudd);

		System.out.println("The expected value at final time for JR is: " + expectedValueJarrowRudd);
		
		/*
		 * We now test an option: we want to plot the results we get for our approximating models when we
		 * increase the number of times. In the same plot, we want to show also the analyic price of the option,
		 * as a benchmark.
		 */
		DoubleUnaryOperator payoffFunction = (x) -> (x - strike > 0 ? 1.0 : 0.0);//European call
		
		EuropeanNonPathDependentOption ourOption = new EuropeanNonPathDependentOption(lastTime, payoffFunction);

		//System.out.println(ourOption.getValue(thirdApproximatingModel));
		//System.out.println(ourOption.getValueDirect(thirdApproximatingModel));

		
		
		/*
		 * We use the Plot2D class of finmath-lib-plot-extensions. In order to do that, we have to define the
		 * functions to plot as objects of type DoubleUnaryOperator.
		 * In our case, we want these functions to take the number of times and return the prices approximated
		 * with this number of times. For us numberOfTimesForFunction should be an int, but in order to define
		 * a DoubleUnaryOperator one should take a double. So we first treat it as a double and then we downcast
		 * it when passing it to the getValue of EuropeanNonPathDependentOption.
		 */
		DoubleUnaryOperator numberOfTimesToPriceCoxRossRubinsteinModel = (numberOfTimesForFunction) -> {
			CoxRossRubinsteinModel ourModelForFunction = new CoxRossRubinsteinModel(spotPrice, riskFreeRate,
					volatility, lastTime, (int) numberOfTimesForFunction);		
			return ourOption.getValue(ourModelForFunction);
		};
		
		DoubleUnaryOperator numberOfTimesToPriceJarrowRuddModel = (numberOfTimesForFunction) -> {
			JarrowRuddModel ourModelForFunction = new JarrowRuddModel(spotPrice, riskFreeRate,
					volatility, lastTime, (int) numberOfTimesForFunction);		
			return ourOption.getValue(ourModelForFunction);
		};
		
		
		DoubleUnaryOperator numberOfTimesToPriceLeisenReimer = (numberOfTimesForFunction) -> {
			LeisenReimerModel ourModelForFunction = new LeisenReimerModel(spotPrice,
					riskFreeRate, volatility, lastTime, (int) numberOfTimesForFunction, strike);		
			return ourOption.getValue(ourModelForFunction);
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
		
//		//look at the Plot2D constructor.
//		final Plot2D plotCRR = new Plot2D(minNumberOfTimes, maxNumberOfTimes, maxNumberOfTimes-minNumberOfTimes+1,
//				Arrays.asList(
//				new Named<DoubleUnaryOperator>("Cox Ross Rubinstein", numberOfTimesToPriceCoxRossRubinsteinModel),
//				new Named<DoubleUnaryOperator>("Black-Scholes", dummyFunctionBlackScholesPrice))
//				);
		
//		plotCRR.setXAxisLabel("Number of discretized times");
//		plotCRR.setYAxisLabel("Price");
//		plotCRR.setIsLegendVisible(true);
//		plotCRR.show();
//		
//		final Plot2D plotJR = new Plot2D(minNumberOfTimes, maxNumberOfTimes, maxNumberOfTimes-minNumberOfTimes+1, Arrays.asList(
//				new Named<DoubleUnaryOperator>("Jarrow Rudd", numberOfTimesToPriceJarrowRuddModel),
//				new Named<DoubleUnaryOperator>("Black-Scholes", dummyFunctionBlackScholesPrice)));
//		plotJR.setXAxisLabel("Number of discretized times");
//		plotJR.setYAxisLabel("Price");
//		plotJR.setIsLegendVisible(true);
//		plotJR.show();
//		
//		final Plot2D plotLR = new Plot2D(minNumberOfTimes, maxNumberOfTimes,  maxNumberOfTimes-minNumberOfTimes+1, Arrays.asList(
//				new Named<DoubleUnaryOperator>("Leisen Reimer", numberOfTimesToPriceLeisenReimer),
//				new Named<DoubleUnaryOperator>("Black-Scholes", dummyFunctionBlackScholesPrice)));
//		plotLR.setXAxisLabel("Number of discretized times");
//		plotLR.setYAxisLabel("Price");
//		plotLR.setIsLegendVisible(true);
//		plotLR.show();
		

		
		
		final Plot2D plotCRR = new Plot2D(minNumberOfTimes, maxNumberOfTimes, (maxNumberOfTimes-minNumberOfTimes+2)/2, Arrays.asList(
				new Named<DoubleUnaryOperator>("Cox Ross Rubinstein", numberOfTimesToPriceCoxRossRubinsteinModel),
				new Named<DoubleUnaryOperator>("Black-Scholes", dummyFunctionBlackScholesPrice)));
		
		plotCRR.setXAxisLabel("Number of discretized times");
		plotCRR.setYAxisLabel("Price");
		plotCRR.setIsLegendVisible(true);
		plotCRR.show();
		
		final Plot2D plotJR = new Plot2D(minNumberOfTimes, maxNumberOfTimes, (maxNumberOfTimes-minNumberOfTimes+2)/2, Arrays.asList(
				new Named<DoubleUnaryOperator>("Jarrow Rudd", numberOfTimesToPriceJarrowRuddModel),
				new Named<DoubleUnaryOperator>("Black-Scholes", dummyFunctionBlackScholesPrice)));
		plotJR.setXAxisLabel("Number of discretized times");
		plotJR.setYAxisLabel("Price");
		plotJR.setIsLegendVisible(true);
		plotJR.show();
		
		final Plot2D plotLR = new Plot2D(minNumberOfTimes, maxNumberOfTimes,  (maxNumberOfTimes-minNumberOfTimes+2)/2, Arrays.asList(
				new Named<DoubleUnaryOperator>("Leisen Reimer", numberOfTimesToPriceLeisenReimer),
				new Named<DoubleUnaryOperator>("Black-Scholes", dummyFunctionBlackScholesPrice)));
		plotLR.setXAxisLabel("Number of discretized times");
		plotLR.setYAxisLabel("Price");
		plotLR.setIsLegendVisible(true);
		plotLR.show();
	}
}
