package it.univr.trees.approximatingmodels;

import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

import it.univr.trees.assetderivativevaluation.products.EuropeanNonPathDependentOption;
import it.univr.usefulmethodsarrays.UsefulMethodsForArrays;
import net.finmath.functions.AnalyticFormulas;
import net.finmath.plots.Named;
import net.finmath.plots.Plot;
import net.finmath.plots.Plot2D;

public class ApproximatingModelsTest {
	

	public static void main(String[] strings) throws Exception {

		double spotPrice = 100;
		double riskFreeRate = 0.0;
		double volatility = 0.2;
		double lastTime = 1;
		int numberOfTimes = 100;
		
		double strike = 100;

		JarrowRuddModel ourModel = new JarrowRuddModel(spotPrice, riskFreeRate, volatility, lastTime, numberOfTimes);		
		
		DoubleUnaryOperator payoffFunction = (x) -> (x - strike > 0 ? x - strike : 0.0);
		double[] indicatorsValuesBiggerEqualInitialValuesAtFinalTime = ourModel.getTransformedValuesAtGivenTime(lastTime,payoffFunction);
		double[] probabilitiesValuestFinalTime = ourModel.getValuesProbabilitiesAtGivenTime(lastTime);

		
		double probabilityBiggerEqualInitialValue = UsefulMethodsForArrays.getScalarProductTwoArrays(indicatorsValuesBiggerEqualInitialValuesAtFinalTime, probabilitiesValuestFinalTime);
		System.out.println(Math.exp(-riskFreeRate*lastTime)*probabilityBiggerEqualInitialValue);

		double bsPrice = AnalyticFormulas.blackScholesOptionValue(spotPrice,riskFreeRate,volatility, lastTime,strike,true);
		System.out.println(bsPrice);
		
		EuropeanNonPathDependentOption ourOption = new EuropeanNonPathDependentOption(lastTime, payoffFunction);
		
		System.out.println(ourOption.getValue(ourModel));
		
		
		
		DoubleUnaryOperator dummyFunctionBlackScholesPrice = (numberOfTimesForFunction) -> {
			//return AnalyticFormulas.blackScholesDigitalOptionValue(spotPrice, riskFreeRate, volatility, lastTime, strike);
			return AnalyticFormulas.blackScholesOptionValue(spotPrice,riskFreeRate,volatility, lastTime,strike,true);
		};
		
		DoubleUnaryOperator numberOfTimesToPriceCoxRossRubinsteinModel = (numberOfTimesForFunction) -> {
			CoxRossRubinsteinModel ourModelForFunction = new CoxRossRubinsteinModel(spotPrice, riskFreeRate, volatility, lastTime, (int) numberOfTimesForFunction);		
			return ourOption.getValue(ourModelForFunction);
		};
		
		DoubleUnaryOperator numberOfTimesToPriceJarrowRuddModel = (numberOfTimesForFunction) -> {
			JarrowRuddModel ourModelForFunction = new JarrowRuddModel(spotPrice, riskFreeRate, volatility, lastTime, (int) numberOfTimesForFunction);		
			return ourOption.getValue(ourModelForFunction);
		};
		
		
		DoubleUnaryOperator numberOfTimesToPriceLeisenReimer = (numberOfTimesForFunction) -> {
			LeisenReimerModel ourModelForFunction = new LeisenReimerModel(spotPrice, riskFreeRate, volatility, lastTime, (int) numberOfTimesForFunction, strike);		
			return ourOption.getValue(ourModelForFunction);
		};
		
		
		final Plot plot = new Plot2D(50, 4000.0, 20, Arrays.asList(
				new Named<DoubleUnaryOperator>("Black Scholes Price", dummyFunctionBlackScholesPrice),
				new Named<DoubleUnaryOperator>("Cox Ross Rubinstein", numberOfTimesToPriceCoxRossRubinsteinModel),
				new Named<DoubleUnaryOperator>("Jarrow Rudd", numberOfTimesToPriceJarrowRuddModel),
				new Named<DoubleUnaryOperator>("Leisen Reimer", numberOfTimesToPriceLeisenReimer)));
		plot.setIsLegendVisible(true);
		plot.show();
		

		
	}

}
