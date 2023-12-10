package it.univr.trees.assetderivativevaluation.enhancedproducts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

import it.univr.trees.enhancedapproximatingmodels.ApproximatingTreeModelInterface;
import it.univr.usefulmethodsarrays.UsefulMethodsForArrays;
import net.finmath.time.TimeDiscretization;

/**
 * This class implements the valuation of a Bermudan option, with given payoff at maturity. This is a path
 * dependent option which can be exercised by the holder at some pre-determined times. The option value is
 * computed via an approximation of a continuous time process by a tree model, represented by an object of type
 * ApproximatingTreeModelInterface. In this way, it is possible to conveniently apply a backward
 * evaluation method.
 * 
 * @author Andrea Mazzon
 *
 */
public class BermudanOption {

	private TimeDiscretization possibleExerciseTimesIncludingMaturity;
	private DoubleUnaryOperator payoffFunction;
	

	/**
	 * It constructs an object which represents the implementation of the American option.
	 * @param possibleTimesIncludingMaturity, an object of type TimeDiscretization representing the times at which
	 * 		  the holder can exercise the option. Maturity is included and is of course the last time.
	 * @param payoffFunction, the funtion which identifies the payoff. The payoff is f(S_T) for payoffFunction
	 * 			f and underlying value S_T at maturity. The payoffFunction is represented by a DoubleUnaryOperator
	 */
	public BermudanOption(TimeDiscretization possibleExerciseTimesIncludingMaturity, DoubleUnaryOperator payoffFunction) {
		this.possibleExerciseTimesIncludingMaturity = possibleExerciseTimesIncludingMaturity;
		this.payoffFunction = payoffFunction;
	}
	
		
	
	/**
	 * It returns the discounted value of the option written on the continuous time model approximated by
	 * the object of type ApproximatingTreeModelInterface given in input. At any node at time t_i, the value
	 * of the option is computed_
	 * - as the maximum between the payoff function evaluated at that node and the conditional expectation at
	 * that node of the values of the option at time t_{i+1} if t_i is the closest discretized time to a
	 * possible exercise time;
	 * - as the conditional expectation at that node of the values of the option at time t_{i+1} otherwise.
	 * This is done by going backward.
	 * 
	 * @param approximatingTreeModel, the underlying
	 * @return the value of the option written on the underlying
	 */
	public double getValue(ApproximatingTreeModelInterface approximatingTreeModel) {
		
		
		double timeStep = approximatingTreeModel.getTimeStep();//the time step of the process discretization
		double timeStepHalf = timeStep/2;//it will be used in the for loop to check if a discretized time is close to an exercise time 
		
		
		int numberOfPossibleExerciseTimesIncludingMaturity = possibleExerciseTimesIncludingMaturity.getNumberOfTimes();

		double maturity = possibleExerciseTimesIncludingMaturity.getTime(possibleExerciseTimesIncludingMaturity.getNumberOfTimes()-1);
		int numberOfDiscretizedTimeSteps = (int) Math.round(maturity/timeStep);

		//values of the option at maturity. Then we go backward. 
		double[] optionValues = approximatingTreeModel.getTransformedValuesAtGivenTime(maturity, payoffFunction);

		//it will be updated during the for loop. We will check if it is close to an exercise time.
		double currentTime = maturity;
		
		/*
		 * Maturity has index numberOfPossibleExerciseTimesIncludingMaturity - 1, and the next exercise time
		 * numberOfPossibleExerciseTimesIncludingMaturity - 1.
		 */
		int currentExerciseTimeIndex = numberOfPossibleExerciseTimesIncludingMaturity - 2;
		
		//the next exercise time after maturity
		double currentExerciseTime = possibleExerciseTimesIncludingMaturity.getTime(currentExerciseTimeIndex);
		
		/*
		 * We go backward. For any timeIndex and any node, first we compute the conditional expectation of the option value
		 * at timeIndex + 1, and then if the discretized time identified by timeIndex is the closest one to nextExerciseTime,
		 * we compute the maximum between this value and the payoff function evaluated at the node. Otherwise, the value of
		 * the option is just given by the conditional expectation.
		 */
		for (int timeIndex = numberOfDiscretizedTimeSteps - 1; timeIndex >= 0; timeIndex--) {
			//delegation to approximatingBinomialModel!
        	double[] conditionalExpectation = approximatingTreeModel.getConditionalExpectation(optionValues, timeIndex);
        	if (Math.abs(currentTime-currentExerciseTime) < timeStepHalf)/*if true, the time for timeIndex is the closest to nextExerciseTime */ {
            	double[] payoffAtCurrentTime = approximatingTreeModel.getTransformedValuesAtGivenTimeIndex(timeIndex, payoffFunction);
                optionValues = UsefulMethodsForArrays.getMaxValuesBetweenTwoArrays(conditionalExpectation,payoffAtCurrentTime);   
                
                //we update nextExerciseTimeIndex and consequently nextExerciseTimeIndex 
                
                //if nextExerciseTimeIndex = 0, then we already have the last one, and we do not change it (otherwise then we get an exception)
                currentExerciseTimeIndex = Math.max(currentExerciseTimeIndex-1, 0);
                currentExerciseTime = possibleExerciseTimesIncludingMaturity.getTime(currentExerciseTimeIndex);
        	} else {
            optionValues = conditionalExpectation;   
        	}
        	//we update currentTime
            currentTime -= timeStep;
        }
		return optionValues[0];
	}
	
	
	/**
	 * It returns an object which is a container for an ArrayList of vectors indicating, for each index of the possible exercise times, the
	 * values of the option, what one would get if exercising at the current time, the expected values of the option at future times and
	 * an array of characters indicating if it is in expectation better to wait or exercise. 
	 * 
	 * @param approximatingTreeModel, the underlying
	 * @return AmericanAndBermudanOptionData, the wrapper for the informations above.
	 */
	public AmericanAndBermudanOptionData getOptionData(ApproximatingTreeModelInterface approximatingTreeModel) {
		
		double timeStep = approximatingTreeModel.getTimeStep();//the time step of the process discretization
		double timeStepHalf = timeStep/2;//it will be used in the for loop to check if a discretized time is close to an exercise time 
		
		
		int numberOfPossibleExerciseTimesIncludingMaturity = possibleExerciseTimesIncludingMaturity.getNumberOfTimes();

		double maturity = possibleExerciseTimesIncludingMaturity.getTime(possibleExerciseTimesIncludingMaturity.getNumberOfTimes()-1);
		int numberOfDiscretizedTimeSteps = (int) Math.round(maturity/timeStep);

		//values of the option at maturity. Then we go backward. 
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
		americanOptionValues.add(0, optionValues);
		exerciseValues.add(0, optionValues);
		waitValues.add(0, optionValues);
		
		/*
		 * The arrays of characters indicate the exercise region: at nodes where it is (in expectation) better to exercise we have 'e',
		 * at nodes where it is (in expectation) better to wait we have 'w'. At the beginning, i.e., at maturity, we have of course
		 * that one must always exercise, so we have an arrays of 'e's.
		 */
		char[] exerciseOrWaitAsArray = new char[numberOfDiscretizedTimeSteps+1];
		Arrays.fill(exerciseOrWaitAsArray, 'e');
		exerciseOrWait.add(exerciseOrWaitAsArray);
		
		
		//it will be updated during the for loop. We will check if it is close to an exercise time.
		double currentTime = maturity;
		
		/*
		 * Maturity has index numberOfPossibleExerciseTimesIncludingMaturity - 1, and the next exercise time
		 * numberOfPossibleExerciseTimesIncludingMaturity - 1.
		 */
		int currentExerciseTimeIndex = numberOfPossibleExerciseTimesIncludingMaturity - 2;
		
		//the next exercise time after maturity
		double currentExerciseTime = possibleExerciseTimesIncludingMaturity.getTime(currentExerciseTimeIndex);
		
		
		/*
		 * We go backward. For any timeIndex and any node, first we compute the conditional expectation of the option value
		 * at timeIndex + 1, and then if the discretized time identified by timeIndex is the closest one to nextExerciseTime,
		 * we compute the maximum between this value and the payoff function evaluated at the node and we update the ArrayLists with
		 * the new vectors. Otherwise, the value of the option is just given by the conditional expectation and we do not make anything
		 * else.
		 */
		for (int timeIndex = numberOfDiscretizedTimeSteps - 1; timeIndex >= 0; timeIndex--) {
			//delegation to approximatingBinomialModel!
        	double[] conditionalExpectation = approximatingTreeModel.getConditionalExpectation(optionValues, timeIndex);
        	if (Math.abs(currentTime-currentExerciseTime) < timeStepHalf)/*if true, the time for timeIndex is the closest to nextExerciseTime */ {
            	double[] payoffAtCurrentTime = approximatingTreeModel.getTransformedValuesAtGivenTimeIndex(timeIndex, payoffFunction);
                optionValues = UsefulMethodsForArrays.getMaxValuesBetweenTwoArrays(conditionalExpectation,payoffAtCurrentTime);   
                
                //update of the ArrayLists 
            	americanOptionValues.add(optionValues);
        		exerciseValues.add(payoffAtCurrentTime);
        		waitValues.add(conditionalExpectation);
   
        		char[] currentExerciseOrWait = new char[conditionalExpectation.length];
        		for (int positionIndex = 0; positionIndex <= conditionalExpectation.length - 1; positionIndex ++) {
        			 currentExerciseOrWait[positionIndex] = (conditionalExpectation[positionIndex]>payoffAtCurrentTime[positionIndex]) ? 'w' : 'e';
                }   
        		exerciseOrWait.add(currentExerciseOrWait);
        		 
                //if nextExerciseTimeIndex = 0, then we already have the last one, and we do not change it (otherwise then we get an exception)
                currentExerciseTimeIndex = Math.max(currentExerciseTimeIndex-1, 0);
                currentExerciseTime = possibleExerciseTimesIncludingMaturity.getTime(currentExerciseTimeIndex);
        	} else {
            optionValues = conditionalExpectation;   
        	}
        	//we update currentTime
            currentTime -= timeStep;  
        }
		
		//we return the wrapper for our ArrayLists
		return new AmericanAndBermudanOptionData(exerciseOrWait, americanOptionValues, exerciseValues, waitValues, possibleExerciseTimesIncludingMaturity);
	}

	
	
}