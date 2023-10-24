package it.univr.usefulmethodsarrays;

import java.util.function.DoubleUnaryOperator;

public class UsefulMethodsForArrays {
	
	
	/**
	 * It returns the sum of the elements of an array
	 * 
	 * @param array
	 * @return the sum of the elements of the array
	 */
	public static double getSum(double array[]) {
		double sum = 0.0;
		for(double value : array) {
			sum += value;
		}
		return sum;
	}
	
		/**
		 * It returns the product of two arrays
		 *
		 * @param firstArray
		 * @param secondArray
		 * @return an array representing the element-wise product of the two arrays
		 */
		public static double[] multArrays(double[] firstArray, double[] secondArray) {
			int firstLength = firstArray.length;
			if (firstLength != secondArray.length) {
				throw new IllegalArgumentException("Error: the two arrays must have same length!");
			}
			double[] product = new double[firstLength];
			for (int i = 0; i < firstLength; i++) {
				product[i] = firstArray[i] * secondArray[i];
			}
			return product;
		}

		/**
		 * It returns the sum of two arrays
		 *
		 * @param firstArray
		 * @param secondArray
		 * @return an array representing the element-wise sum of the two arrays
		 */
		public static double[] sumArrays(double[] firstArray, double[] secondArray) {
			int firstLength = firstArray.length;
			if (firstLength != secondArray.length) {
				throw new IllegalArgumentException("Error: the two arrays must have same length!");
			}

			double[] difference = new double[firstLength];
			for (int i = 0; i < firstLength; i++) {
				difference[i] = firstArray[i] + secondArray[i];
			}
			return difference;
		}
		
		/**
		 * It returns the difference of two arrays
		 *
		 * @param firstArray
		 * @param secondArray
		 * @return an array representing the element-wise difference of the two arrays
		 */
		public static double[] diffArrays(double[] firstArray, double[] secondArray) {
			int firstLength = firstArray.length;
			if (firstLength != secondArray.length) {
				throw new IllegalArgumentException("Error: the two arrays must have same length!");
			}

			double[] difference = new double[firstLength];
			for (int i = 0; i < firstLength; i++) {
				difference[i] = firstArray[i] - secondArray[i];
			}
			return difference;
		}

		/**
		 * It returns the element-wise ratio of two arrays
		 *
		 * @param firstArray
		 * @param secondArray
		 * @return an array representing the element-wise ratio of the two arrays
		 */
		public static double[] ratioArrays(double[] firstArray, double[] secondArray) {
			int firstLength = firstArray.length;
			if (firstLength != secondArray.length) {
				throw new IllegalArgumentException("Error: the two arrays must have same length!");
			}
			double[] ratio = new double[firstLength];
			for (int i = 0; i < firstLength; i++) {
				ratio[i] = firstArray[i] / secondArray[i];
			}
			return ratio;
		}

		/**
		 * It returns an array transformedArray where transformedArray[i] = f(originalArray[i]), with
		 * f given in input as a DoubleUnaryOperator.
		 *  
		 * @param originalArray, 
		 * @param transformFunction, the funtion f above
		 * @return  an array transformedArray where transformedArray[i] = f(originalArray[i])
		 */
		public static double[] applyFunctionToArray(double[] originalArray, DoubleUnaryOperator transformFunction) {
			int lengthOriginalArray = originalArray.length;
			double[] transformedArray = new double[lengthOriginalArray];
			for (int i = 0; i<lengthOriginalArray; i++) {
				//this is how DoubleUnaryOperator work: applyAsDouble is the method of the interface they implement
				//transformedArray[i] = f(originalArray[i) where f is the function represented by transformFunction
				transformedArray[i] = transformFunction.applyAsDouble(originalArray[i]);
			}
			return transformedArray;
		}
		
		
		/**
		 * It returns the scalar product of two arrays
		 *
		 * @param firstArray
		 * @param secondArray
		 * @return an array representing the element-wise product of the two arrays
		 */
		public static double getScalarProductTwoArrays(double[] firstArray, double[] secondArray) {
			double[] productOfArrays = multArrays(firstArray, secondArray);
			return getSum(productOfArrays);
		}
		
		
}
