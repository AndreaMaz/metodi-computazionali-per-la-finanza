package it.univr.trees.assetderivativevaluation.enhancedproducts;

import java.util.ArrayList;

import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

/**
 * This class is a container for American and Bermudan option data: it has fields which are ArrayList of vectors indicating,
 * for each time index, the values of the option, what one would get if exercising at the current time, the expected values of the option
 * at future times and an array of characters indicating if it is in expectation better to wait or exercise.
 * It has a public constructor which sets these objects and public getters of arrays for given time and time index
 * 
 * @author Andrea Mazzon
 *
 */
public class AmericanAndBermudanOptionData {
	
	ArrayList<char[]> exerciseOrWait; 
	ArrayList<double[]> optionValues; 
	ArrayList<double[]> exerciseValues; 
	ArrayList<double[]> waitValues; 
	private TimeDiscretization exerciseTimes;
	int arrayListSize;
	
	/**
	 * It constructs a container for the informations about American or bermudan options
	 * 
	 * @param exerciseOrWait: ArrayList of vectors of characters indicating at each node if it is in expectation better
	 * 						  to wait (continue) or exercise the option
	 * @param optionValues:   ArrayList of vectors of doubles indicating at each node the value of the option
	 * @param exerciseValues:   ArrayList of vectors of doubles indicating at each node the amount one gets exercising of the option
	 * @param waitValues: :   ArrayList of vectors of doubles indicating at each node the expected value of the option at future times
	 * @param exerciseTimes:  the TimeDiscretization of exercise times
	 */
	public AmericanAndBermudanOptionData(ArrayList<char[]> exerciseOrWait, ArrayList<double[]> optionValues,
			ArrayList<double[]> exerciseValues, ArrayList<double[]> waitValues,
			TimeDiscretization exerciseTimes) {
		this.exerciseOrWait = exerciseOrWait;
		this.optionValues = optionValues;
		this.exerciseValues = exerciseValues;
		this.waitValues = waitValues;
		this.exerciseTimes = exerciseTimes;	
		arrayListSize = exerciseOrWait.size();
	}
	
	/**
	 * It constructs a container for the informations about American or bermudan options
	 * 
	 * @param exerciseOrWait: ArrayList of vectors of characters indicating at each node if it is in expectation better
	 * 						  to wait (continue) or exercise the option
	 * @param optionValues:   ArrayList of vectors of doubles indicating at each node the value of the option
	 * @param exerciseValues:   ArrayList of vectors of doubles indicating at each node the amount one gets exercising of the option
	 * @param waitValues: :   ArrayList of vectors of doubles indicating at each node the expected value of the option at future times
	 * @param exerciseTimes:  the time step of the time discretization of exercise times
	 * @param numberOfTimeSteps:  the number of exercise times
	 */
	public AmericanAndBermudanOptionData(ArrayList<char[]> exerciseOrWait, ArrayList<double[]> optionValues,
			ArrayList<double[]> exerciseValues, ArrayList<double[]> waitValues, double timeStep, int numberOfTimeSteps) {
		this(exerciseOrWait, optionValues, exerciseValues, waitValues, new TimeDiscretizationFromArray(0.0, numberOfTimeSteps, timeStep));
	}
	
	
	/**
	 * 
	 * @param exerciseTimeIndex, the time index at which we want to get the information
	 * @return an array of characters with 'w' and 'e' in the position of nodes where it is in
	 * 		  expectation better to wait and exercise, respectively, from the indices where the value of
	 * 		  the underlying is higher.
	 */
	public char[] getExerciseOrWaitAtGivenExerciseTimeIndex(int exerciseTimeIndex){
		//remember that, in the ArrayList, we first enter the vector at maturity and so on. So we invert.
		return exerciseOrWait.get(arrayListSize-1-exerciseTimeIndex);
	}
	
	/**
	 * @param exerciseTimeIndex, the time index at which we want to get the information
	 * @return an array of doubles with the value of the option at any index, from the indices where the value of
	 * 		 the underlying is higher.
	 */
	public double[] getValuesAtGivenExerciseTimeIndex(int exerciseTimeIndex){
		//remember that, in the ArrayList, we first enter the vector at maturity and so on. So we invert.
		return optionValues.get(arrayListSize-1-exerciseTimeIndex);
	}
	
	/**
	 * @param exerciseTimeIndex, the time index at which we want to get the information
	 * @return an array of doubles with the value of the payoff at any index, from the indices where the value of
	 * 		 the underlying is higher.
	 */
	public double[] getExerciseValuesAtGivenExerciseTimeIndex(int exerciseTimeIndex){
		//remember that, in the ArrayList, we first enter the vector at maturity and so on. So we invert.
		return exerciseValues.get(arrayListSize-1-exerciseTimeIndex);
	}
	
	/**
	 * @param exerciseTimeIndex, the time index at which we want to get the information
	 * @return an array of doubles with the expected value of the option at any index, from the indices where the value of
	 * 		 the underlying is higher.
	 */
	public double[] getWaitAtGivenExerciseTimeIndex(int exerciseTimeIndex){
		//remember that, in the ArrayList, we first enter the vector at maturity and so on. So we invert.
		return waitValues.get(arrayListSize-1-exerciseTimeIndex);
	}
	
	/**
	 * 
	 * @param exerciseTime, the time at which we want to get the information
	 * @return an array of characters with 'w' and 'e' in the position of nodes where it is in
	 * 		  expectation better to wait and exercise, respectively, from the indices where the value of
	 * 		  the underlying is higher.
	 */
	public char[] getExerciseOrWaitAtGivenExerciseTime(double exerciseTime){
		int exerciseTimeIndex = exerciseTimes.getTimeIndex(exerciseTime);
		//remember that, in the ArrayList, we first enter the vector at maturity and so on. So we invert.
		return exerciseOrWait.get(arrayListSize-1-exerciseTimeIndex);
	}
	
	/**
	 * @param exerciseTime, the time at which we want to get the information
	 * @return an array of doubles with the value of the option at any index, from the indices where the value of
	 * 		 the underlying is higher.
	 */
	public double[] getValuesAtGivenExerciseTime(double exerciseTime){
		int exerciseTimeIndex = exerciseTimes.getTimeIndex(exerciseTime);
		//remember that, in the ArrayList, we first enter the vector at maturity and so on. So we invert.
		return optionValues.get(arrayListSize-1-exerciseTimeIndex);
	}
	
	/**
	 * @param exerciseTime, the time at which we want to get the information
	 * @return an array of doubles with the value of the payoff at any index, from the indices where the value of
	 * 		 the underlying is higher.
	 */
	public double[] getExerciseValuesAtGivenExerciseTime(double exerciseTime){
		int exerciseTimeIndex = exerciseTimes.getTimeIndex(exerciseTime);
		//remember that, in the ArrayList, we first enter the vector at maturity and so on. So we invert.
		return exerciseValues.get(arrayListSize-1-exerciseTimeIndex);
	}
	
	/**
	 * @param exerciseTime, the time at which we want to get the information
	 * @return an array of doubles with the expected value of the option at any index, from the indices where the value of
	 * 		 the underlying is higher.
	 */
	public double[] getWaitAtGivenExerciseTime(double exerciseTime){
		int exerciseTimeIndex = exerciseTimes.getTimeIndex(exerciseTime);
		//remember that, in the ArrayList, we first enter the vector at maturity and so on. So we invert.
		return waitValues.get(arrayListSize-1-exerciseTimeIndex);
	}
	
	
	

}
