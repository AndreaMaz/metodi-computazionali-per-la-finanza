package it.univr.montecarlo.discretizationschemes.finmathlibraryimplementation;

import java.util.Random;

import net.finmath.exception.CalculationException;
import net.finmath.functions.AnalyticFormulas;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.montecarlo.IndependentIncrements;
import net.finmath.montecarlo.assetderivativevaluation.AssetModelMonteCarloSimulationModel;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloAssetModel;
import net.finmath.montecarlo.assetderivativevaluation.models.BachelierModel;
import net.finmath.montecarlo.assetderivativevaluation.products.EuropeanOption;
import net.finmath.montecarlo.model.ProcessModel;
import net.finmath.stochastic.RandomVariable;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;


/**
 * In this class we test the Finmath library implementation of the Monte Carlo method and discretization of a stochastic
 * process for the evaluation of an European call option. We want the underlying to be a Bachelier model and use a classic
 * Euler-Maruyama scheme.
 *
 * @author Andrea Mazzon
 *
 */
public class FinmathLibraryTest {

	public static void main(String[] args) throws CalculationException {

		//parameters for the option

		double maturity = 1.0;

		double strike = 2.0;

		//parameters for the model (i.e., for the SDE)

		double initialValue = 2.0;

		double riskFreeRate = 0.0;

		double volatility = 0.2;

		//we compute and print the analytic value

		double analyticValue = AnalyticFormulas.bachelierOptionValue(initialValue, volatility, maturity, strike, 1.0);

		System.out.println("The analytic value of the option is " + analyticValue);

		//parameters for the discretization scheme and the Monte Carlo simulation

		double timeStep = 0.05;

		int numberOfTimeSteps = (int) (maturity/timeStep);

		int numberOfSimulatedPaths = 50;

		int seed = 1897;

		EuropeanOption ourOption = new EuropeanOption(maturity, strike);

		ProcessModel model = new BachelierModel(initialValue, riskFreeRate, volatility);

		TimeDiscretization ourTimeDiscretization =
				new TimeDiscretizationFromArray(0.0, numberOfTimeSteps, timeStep);

		IndependentIncrements stochasticDriver = new BrownianMotionFromMersenneRandomNumbers(ourTimeDiscretization,
				1, numberOfSimulatedPaths, seed);

		AssetModelMonteCarloSimulationModel ourSimulation = new MonteCarloAssetModel(model, stochasticDriver);//how to construct it?

		//we print and compute the Monte Carlo value

		RandomVariable payoffAtMaturity = ourOption.getValue(0.0, ourSimulation);

		double monteCarloValue = payoffAtMaturity.getAverage(); //we have to change this value

		double secondMonteCarloValue = ourOption.getValue(ourSimulation);

		System.out.println("The Monte carlo value of the option is " + monteCarloValue);
		System.out.println("The Monte carlo value of the option computed with the second methods is " + secondMonteCarloValue);


		double minPrice = monteCarloValue;
		double maxPrice = monteCarloValue;

		int numberOfTests = 200;

		Random seedGenerator = new Random();

		AssetModelMonteCarloSimulationModel currentSimulation;
		IndependentIncrements currentStochasticDriver;
		double currentPrice;

		int currentSeed;
		for (int testIndex = 0; testIndex < numberOfTests; testIndex ++) {
			currentSeed = seedGenerator.nextInt();
			currentStochasticDriver = new BrownianMotionFromMersenneRandomNumbers(ourTimeDiscretization,
					1, numberOfSimulatedPaths, currentSeed);

			currentSimulation = new MonteCarloAssetModel(model, currentStochasticDriver);//how to construct it?
					currentPrice = ourOption.getValue(currentSimulation);
					if (currentPrice < minPrice) {
						minPrice = currentPrice;
					} else if (currentPrice > maxPrice) {
						maxPrice = currentPrice;
					}
		}

		System.out.println();
		System.out.println("Max price:" + maxPrice);
		System.out.println("Min price:" + minPrice);


	}
}
