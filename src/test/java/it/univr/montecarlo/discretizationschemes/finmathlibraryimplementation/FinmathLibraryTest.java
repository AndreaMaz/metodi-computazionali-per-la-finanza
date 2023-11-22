package it.univr.montecarlo.discretizationschemes.finmathlibraryimplementation;

import net.finmath.exception.CalculationException;
import net.finmath.functions.AnalyticFormulas;


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
		
		//paremeters for the option
		double maturity = 1.0;
		double strike = 2.0;
		
		//parameters for the model (i.e., for the SDE)
		double initialValue = 2.0;
		double riskFreeRate = 0.0;
		double volatility = 0.2;
	
		//we compute and print the analytic value
		double analyticValue = AnalyticFormulas.bachelierOptionValue(initialValue, volatility, maturity, strike, 1);
		System.out.println("The analytic value of the option is " + analyticValue);
		
		//parameters for the discretization scheme and the Monte Carlo simulation
		double timeStep = 0.05;
		int numberOfTimesSteps = (int) (maturity/timeStep);
		int numberOfSimulatedPaths = 10000;
		
		int seed = 1897;
		
	
		
		//we print and compute the Monte Carlo value
		double monteCarloValue = 0.0; //we have to change this value
		System.out.println("The Monte carlo value of the option is " + monteCarloValue);		
	}
}
