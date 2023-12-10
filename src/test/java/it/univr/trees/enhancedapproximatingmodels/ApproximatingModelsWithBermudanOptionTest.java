package it.univr.trees.enhancedapproximatingmodels;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.DoubleUnaryOperator;

import it.univr.trees.assetderivativevaluation.enhancedproducts.AmericanAndBermudanOptionData;
import it.univr.trees.assetderivativevaluation.enhancedproducts.BermudanOption;
import net.finmath.plots.Named;
import net.finmath.plots.Plot2D;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

/**
 * In this class we test the implementation of Bermudan options via Tree models: we print the prices for the four approximating 
 * methods and for increasing number of times, and then we print more detailed informations at a given time index.
 * 
 * @author andreamazzon
 *
 */
public class ApproximatingModelsWithBermudanOptionTest {
	public static void main(String[] strings) throws Exception {


		//model parameters
		double spotPrice = 1;
		double riskFreeRate = 0.02;
		double volatility = 0.7;

		//option parameters
		double strike = 1;
		DoubleUnaryOperator payoffFunction = (x) -> (strike-x > 0 ? strike - x : 0.0);

		double maturity = 3.5;
		double possibleExerciseTimesStep = 0.5;
		double firstPossibleExerciseTime = 0.5;
		int numberOfPossibleExerciseTimes = (int) ((maturity-firstPossibleExerciseTime)/possibleExerciseTimesStep);
		TimeDiscretization possibleExerciseTimes = new TimeDiscretizationFromArray(firstPossibleExerciseTime, numberOfPossibleExerciseTimes, possibleExerciseTimesStep);

		BermudanOption ourOption = new BermudanOption(possibleExerciseTimes, payoffFunction);



		/*
		 * We want to plot the results we get for our approximating models when we increase the number of times.
		 * We use the Plot2D class of finmath-lib-plot-extensions. In order to do that, we have to define the
		 * functions to plot as objects of type DoubleUnaryOperator.
		 * In our case, we want these functions to take the number of times and return the prices approximated
		 * with this number of times. For us numberOfTimesForFunction should be an int, but in order to define
		 * a DoubleUnaryOperator one should take a double. So we first treat it as a double and then we downcast
		 * it when passing it to the getValue of EuropeanBarrierOption.
		 */	
		DoubleUnaryOperator numberOfTimesToPriceBoyleModel = (numberOfTimesForFunction) -> {
			BoyleModel ourModelForFunction = new BoyleModel(spotPrice, riskFreeRate, volatility, maturity, (int) numberOfTimesForFunction);		
			return ourOption.getValue(ourModelForFunction);
		};		

		DoubleUnaryOperator numberOfTimesToPriceCoxRossRubinsteinModel = (numberOfTimesForFunction) -> {
			CoxRossRubinsteinModel ourModelForFunction = new CoxRossRubinsteinModel(spotPrice, riskFreeRate, volatility, maturity, (int) numberOfTimesForFunction);		
			return ourOption.getValue(ourModelForFunction);
		};

		DoubleUnaryOperator numberOfTimesToPriceJarrowRuddModel = (numberOfTimesForFunction) -> {
			JarrowRuddModel ourModelForFunction = new JarrowRuddModel(spotPrice, riskFreeRate, volatility, maturity, (int) numberOfTimesForFunction);		
			return ourOption.getValue(ourModelForFunction);
		};


		DoubleUnaryOperator numberOfTimesToPriceLeisenReimerModel = (numberOfTimesForFunction) -> {
			LeisenReimerModel ourModelForFunction = new LeisenReimerModel(spotPrice, riskFreeRate, volatility, maturity, (int) numberOfTimesForFunction, strike);		
			return ourOption.getValue(ourModelForFunction);
		};

		int maxNumberOfTimes = 300;
		int minNumberOfTimes = 10;

		//plots
		final Plot2D plot = new Plot2D(minNumberOfTimes, maxNumberOfTimes, maxNumberOfTimes-minNumberOfTimes+1, Arrays.asList(
				new Named<DoubleUnaryOperator>("Cox Ross Rubinstein", numberOfTimesToPriceCoxRossRubinsteinModel),
				new Named<DoubleUnaryOperator>("Jarrow-Rudd", numberOfTimesToPriceJarrowRuddModel),
				new Named<DoubleUnaryOperator>("Leisen-Reimer", numberOfTimesToPriceLeisenReimerModel),
				new Named<DoubleUnaryOperator>("Boyle", numberOfTimesToPriceBoyleModel)
				));

		plot.setTitle("Bermudan option pricing, step of exercise time = " + possibleExerciseTimesStep);
		plot.setXAxisLabel("Number of discretized times");
		plot.setYAxisLabel("Price");
		plot.setIsLegendVisible(true);
		plot.setYRange(0.43, 0.46);
		
		plot.show();

		CoxRossRubinsteinModel ourModelForFunction = new CoxRossRubinsteinModel(spotPrice, riskFreeRate, volatility, maturity, 300);
		
		AmericanAndBermudanOptionData ourOptionData = ourOption.getOptionData(ourModelForFunction);
		
		DecimalFormat formatterValue = new DecimalFormat("#.###");
		formatterValue.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));

		int exerciseTimeIndex = 0;//exercise index 0 gives exercise time equal to 0.5
		
		
		System.out.println("Best strategy:");
		System.out.println();
		System.out.println(Arrays.toString(ourOptionData.getExerciseOrWaitAtGivenExerciseTimeIndex(exerciseTimeIndex)));
		System.out.println();

		System.out.println();
		System.out.println("What we get if we exercise:");
		System.out.println();
		Arrays.stream(ourOptionData.getExerciseValuesAtGivenExerciseTimeIndex(exerciseTimeIndex)).forEach(element -> System.out.print(formatterValue.format(element) + " " ));
		System.out.println();

		System.out.println();
		System.out.println("What we get if we wait:");
		System.out.println();
		Arrays.stream(ourOptionData.getWaitAtGivenExerciseTimeIndex(exerciseTimeIndex)).forEach(element -> System.out.print(formatterValue.format(element) + " " ));
		System.out.println();


		System.out.println();
		System.out.println("Option value:");
		System.out.println();
		Arrays.stream(ourOptionData.getValuesAtGivenExerciseTimeIndex(exerciseTimeIndex)).forEach(element -> System.out.print(formatterValue.format(element) + " " ));
		System.out.println();
	}
}
