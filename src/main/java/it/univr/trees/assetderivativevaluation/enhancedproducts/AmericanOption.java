package it.univr.trees.assetderivativevaluation.enhancedproducts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

import it.univr.trees.enhancedapproximatingmodels.ApproximatingTreeModelInterface;
import it.univr.usefulmethodsarrays.UsefulMethodsForArrays;

/**
 * This class implements the valuation of an American option, with given payoff at maturity. This is a path
 * dependent option which can be exercised by the holder at any time. The option value is computed via an
 * approximation of a continuous time process by a tree model, represented by an object of type
 * ApproximatingTreeModelInterface. In this way, it is possible to conveniently apply a backward
 * evaluation method.
 * 
 * @author Andrea Mazzon
 *
 */
public class AmericanOption {

	private double maturity;
	private DoubleUnaryOperator payoffFunction;

	/**
	 * It constructs an object which represents the implementation of the American option.
	 * @param maturity, the maturity of the option
	 * @param payoffFunction, the funtion which identifies the payoff. The payoff is f(S_T) for payoffFunction
	 * 			f and underlying value S_T at maturity. The payoffFunction is represented by a DoubleUnaryOperator
	 */
	public AmericanOption(double maturity, DoubleUnaryOperator payoffFunction) {
		this.maturity = maturity;
		this.payoffFunction = payoffFunction;
	}

	/**
	 * It returns the discounted value of the option written on the continuous time model approximated by
	 * the object of type ApproximatingTreeModelInterface given in input. At any node at time t_{i}, the value
	 * of the option is computed as the maximum between the payoff function evaluated at that node and the
	 * conditional expectation at that node of the values of the option at time t_{i+1}.
	 * This is done by going backward
	 * 
	 * @param approximatingTreeModel, the underlying
	 * @return the value of the option written on the underlying
	 */
	public double getValue(ApproximatingTreeModelInterface approximatingTreeModel) {

		//values of the option at maturity. Then we go backward. 
		double[] optionValues = approximatingTreeModel.getTransformedValuesAtGivenTime(maturity, payoffFunction);

		int numberOfTimeSteps = (int) Math.round(maturity/approximatingTreeModel.getTimeStep());

		/*
		 * We go backward. For any timeIndex and any node, first we compute the conditional expectation of the option value
		 * at timeIndex + 1, and then we compute the maximum between this value and the payoff function evaluated at the node
		 */
		for (int timeIndex = numberOfTimeSteps - 1; timeIndex >= 0; timeIndex--) {
			//delegation to approximatingTreeModel!
			double[] conditionalExpectation = approximatingTreeModel.getConditionalExpectation(optionValues, timeIndex);
			double[] payoffAtCurrentTime = approximatingTreeModel.getTransformedValuesAtGivenTimeIndex(timeIndex, payoffFunction);
			optionValues = UsefulMethodsForArrays.getMaxValuesBetweenTwoArrays(conditionalExpectation,payoffAtCurrentTime);   
		}
		return optionValues[0];
	}

	/**
	 * It returns an object which is a container for an ArrayList of vectors indicating, for each time index, the
	 * values of the option, what one would get if exercising at the current time, the expected values of the option
	 * at future times and an array of characters indicating if it is in expectation better to wait or exercise. 
	 * 
	 * @param approximatingTreeModel, the underlying
	 * @return AmericanAndBermudanOptionData, the wrapper for the informations above.
	 */
	public AmericanAndBermudanOptionData getOptionData(ApproximatingTreeModelInterface approximatingTreeModel) {

		double timeStep = approximatingTreeModel.getTimeStep();
		int numberOfTimeSteps = (int) Math.round(maturity/timeStep);

		//values of the option at maturity. Then we go backward
		double[] optionValues = approximatingTreeModel.getTransformedValuesAtGivenTime(maturity, payoffFunction);

		/*
		 * Here are the ArrayLists of vectors which store our information. Note that one of the main advantages with respect to have 
		 * a matrix is that the vectors can have different length. This will be the case now, because the higher the time index, the
		 * more the possible realizations.
		 */
		ArrayList<char[]> exerciseOrWait = new ArrayList<char[]>(); 
		ArrayList<double[]> americanOptionValues  = new ArrayList<double[]>(); 
		ArrayList<double[]> exerciseValues  = new ArrayList<double[]>(); 
		ArrayList<double[]> waitValues  = new ArrayList<double[]>(); 

		/*
		 * The first entry of these ArrayLists is based on what happens at maturity. The second one will be given by what happens at the
		 * second last exercise time and so on.
		 */
		americanOptionValues.add(optionValues);
		exerciseValues.add(optionValues);
		waitValues.add(optionValues);

		/*
		 * The arrays of characters indicate the exercise region: at nodes where it is (in expectation) better to exercise we have 'e',
		 * at nodes where it is (in expectation) better to wait we have 'w'. At the beginning, i.e., at maturity, we have of course
		 * that one must always exercise, so we have an arrays of 'e's.
		 */
		char[] exerciseOrWaitAsArray = new char[numberOfTimeSteps+1];
		Arrays.fill(exerciseOrWaitAsArray, 'e');
		exerciseOrWait.add(exerciseOrWaitAsArray);


		/*
		 * We go backward. For any timeIndex and any node, first we compute the conditional expectation of the option value
		 * at timeIndex + 1, and then we compute the maximum between this value and the payoff function evaluated at the node,
		 * and we update the ArrayLists with the new vectors
		 */
		for (int timeIndex = numberOfTimeSteps - 1; timeIndex >= 0; timeIndex--) {
			//delegation to approximatingTreeModel!
			double[] conditionalExpectation = approximatingTreeModel.getConditionalExpectation(optionValues, timeIndex);
			double[] payoffAtCurrentTime = approximatingTreeModel.getTransformedValuesAtGivenTimeIndex(timeIndex, payoffFunction);
			optionValues = UsefulMethodsForArrays.getMaxValuesBetweenTwoArrays(conditionalExpectation,payoffAtCurrentTime);

            //update of the ArrayLists 
			americanOptionValues.add(optionValues);
			exerciseValues.add(payoffAtCurrentTime);
			waitValues.add(conditionalExpectation);

			char[] currentExerciseOrWait = new char[timeIndex+1];
			for (int positionIndex = 0; positionIndex <= timeIndex; positionIndex ++) {
				currentExerciseOrWait[positionIndex] = (payoffAtCurrentTime[positionIndex] > conditionalExpectation[positionIndex]) ? 'e' : 'w';
			}   
			exerciseOrWait.add(currentExerciseOrWait);

		}
		//we return the wrapper for our ArrayLists
		return new AmericanAndBermudanOptionData(exerciseOrWait, americanOptionValues, exerciseValues, waitValues, timeStep, numberOfTimeSteps);
	}
}
