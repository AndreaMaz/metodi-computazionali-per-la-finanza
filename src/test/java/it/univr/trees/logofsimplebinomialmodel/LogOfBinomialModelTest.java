package it.univr.trees.logofsimplebinomialmodel;


import it.univr.usefulmethodsarrays.UsefulMethodsForArrays;


public class LogOfBinomialModelTest {
	public static void main(String[] strings) {

		//you can change the parameters, of course
		double upFactor = 1.2;
		double downFactor = 0.8;
		
		int numberOfTimes = 10;
		double initialValue = 2;
		
		LogOfBinomialModel myLogOfBinomialModel = new LogOfBinomialModel(upFactor, downFactor, initialValue,  numberOfTimes);
		
		
			
		int lastTimeIndex = numberOfTimes - 1;	


		double[] possibleValuesAtFinalTimeIndex = myLogOfBinomialModel.getValuesAtGivenTimeIndex(lastTimeIndex);
		

		double[] probabilitiesOfTheValuesAtFinalTimes =
				myLogOfBinomialModel.getValuesProbabilitiesAtGivenTimeIndex(lastTimeIndex);
		
		double expectedValue = UsefulMethodsForArrays.getScalarProductTwoArrays(possibleValuesAtFinalTimeIndex, probabilitiesOfTheValuesAtFinalTimes);
		
		System.out.println("Expected value = " + expectedValue);
		System.out.println("Initial value = " + initialValue);
	}
}
