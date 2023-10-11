package it.univr.trees.simplebinomialmodel;

import java.util.Arrays;

public class BinomialModelTest {

	
	public static void main(String[] strings) {

		double upFactor = 2;
		double downFactor = 0.5;
		int numberOfTimes = 8;
		double initialValue = 100;
		double riskFreeFactor = 0.2;
		
		BinomialModel myBinomialModel = new BinomialModel(upFactor,  downFactor, riskFreeFactor, initialValue,  numberOfTimes);
		
		
		double[] values = myBinomialModel.getValuesAtGivenTimeIndex(2);
		System.out.println(Arrays.toString(values));
		
//		for (int timeIndex = 0; timeIndex < numberOfTimes; timeIndex ++) {
//			double[] values = myBinomialModel.getValuesAtGivenTimeIndex(timeIndex);
//			System.out.println(Arrays.toString(values));
//		}
//		System.out.println();
//		for (int timeIndex = 0; timeIndex < numberOfTimes; timeIndex ++) {
//			double[] valuesProbabilities = myBinomialModel.getValuesProbabilitiesAtGivenTimeIndex(timeIndex);
//			System.out.println(Arrays.toString(valuesProbabilities));
//		}

	}
}
