package it.univr.finitedifferences.ourproducts;

import java.util.function.DoubleUnaryOperator;

import it.univr.analyticformulas.OurAnalyticFormulas;
import it.univr.montecarlo.ourproducts.BarrierOption;
import net.finmath.exception.CalculationException;
import net.finmath.finitedifference.models.FDMBlackScholesModel;
import net.finmath.finitedifference.models.FiniteDifference1DModel;
import net.finmath.finitedifference.products.FiniteDifference1DProduct;
import net.finmath.interpolation.RationalFunctionInterpolation;
import net.finmath.interpolation.RationalFunctionInterpolation.ExtrapolationMethod;
import net.finmath.interpolation.RationalFunctionInterpolation.InterpolationMethod;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloBlackScholesModel;
import net.finmath.montecarlo.assetderivativevaluation.products.AbstractAssetMonteCarloProduct;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

/**
 * In this class we test the Finmath library implementation of finite difference methods to price call and put options.
 * The underlying is a Black-Scholes process. We print the prices for different initial values of the underlying and check,
 * via assertArrayEquals, if these are close to the analytic one.
 * 
 * @author Andrea Mazzon
 *
 */
public class BarrierCallTest {



	public static void main(String[] args) throws CalculationException {

		//option parameters
		double upperBarrier = Long.MAX_VALUE;
		double lowerBarrier = 80;
		double maturity = 3.0;		
		double strike = 100;
		
	
		AbstractAssetMonteCarloProduct optionValueMCCalculator = new BarrierOption(maturity, strike, lowerBarrier, upperBarrier);

		double theta = 0.5;

		//the object representing the option (like the classes implementing AbstractMonteCarloProduct)
		FiniteDifference1DProduct optionValueFDCalculator = new FDMBarrierCallOption(maturity, strike, lowerBarrier, upperBarrier, theta);

		//model (i.e., underlying) parameters
		double initialValue = 100;
		double riskFreeRate = 0.0;
		double volatility = 0.3;
		
		
		double analyticalOptionValue = OurAnalyticFormulas.blackScholesDownAndOut(initialValue, riskFreeRate, volatility, maturity, strike, lowerBarrier);
		
		
		//Finite difference discretization parameters
		final int numTimesteps = 70;
		final int numSpacesteps = 300;
		final int numStandardDeviations = 15;
		
		
		//Monte Carlo time discretization parameters
		double initialTime = 0.0;
		double timeStep = 0.1;
		int numberOfTimeSteps = (int) (maturity/timeStep);

		TimeDiscretization times = new TimeDiscretizationFromArray(initialTime, numberOfTimeSteps, timeStep);

		//simulation parameters
		int numberOfPaths = 100000;
		int seed = 1897;


		final FiniteDifference1DModel model = new FDMBlackScholesModel(
				numTimesteps,//for the discretization of the time interval
				numSpacesteps,//for the discretization of the space domain
				numStandardDeviations,//this enters in the computation of the right and left end of the space domain
				strike,//in the finmath library, there is written that this is the center of the space discretization, but this is not true: it has no effect
				theta,
				strike,//the center of the discretization in the class FDMThetaMethod is initialValue*exp(r*timeHorizon)
				riskFreeRate,
				volatility);


		/**
		 * This method prints the prices of a call option for different initial values of the underlying, and checks
		 * if these are close to the analytic ones. This is done via assertArrayEquals, which causes a failure if, at least
		 * for an index i, there is a difference between the i-th elements of the array of analytic prices and of the prices we
		 * compute higher than tolerance.
		 */


		/* 
		 * We don't really have to know that to call the method, but anyway: this method calls a method with the same name
		 * (but different argument list) defined in FiniteDifference1DModel. There, the boundary conditions are given according to
		 * the option: note that  FDMEuropeanCallOption has itw own implementation of the methods giving the boundary
		 * conditions. 
		 */
		final double[][] returnedValues = optionValueFDCalculator.getValue(0.0, model);


		//these are the corresponding prices
		final double[] initialValues = returnedValues[0];
		final double[] optionValues = returnedValues[1];
		

		/*
		 * Since we want to plot a function, and not only the array with respect to the other array, we have to interpolate.
		 * That is, we want to find a continuous function f such that f(x_i)=y_i for all i, where x_i are the initial values
		 * and y_i the prices. We do that with the help of the Finmath library itself.
		 */
		final RationalFunctionInterpolation callInterpolation = new RationalFunctionInterpolation(initialValues, optionValues, InterpolationMethod.CUBIC_SPLINE, ExtrapolationMethod.DEFAULT);

		//in this way, we can define the DoubleUnaryOperator we plot
		DoubleUnaryOperator interpolatedBarrierFunction = x -> callInterpolation.getValue(x);

		final double finiteDifferenceValueOfTheOption = interpolatedBarrierFunction.applyAsDouble(initialValue);		

		BrownianMotion ourDriver = new BrownianMotionFromMersenneRandomNumbers(times, 1 /* numberOfFactors */, numberOfPaths, seed);

	

		//we construct an object of type MonteCarloBlackScholesModel: it represents the simulation of a Black-Scholes process
		MonteCarloBlackScholesModel blackScholesProcess = new MonteCarloBlackScholesModel(initialValue, riskFreeRate, volatility, ourDriver);

		double monteCarloValueOfTheOption = optionValueMCCalculator.getValue(blackScholesProcess);

		System.out.println("The analytic price is: " + analyticalOptionValue);
		System.out.println("The Monte Carlo price is: " + monteCarloValueOfTheOption);
		System.out.println("The Finite difference price is: " + finiteDifferenceValueOfTheOption);
	}

}
