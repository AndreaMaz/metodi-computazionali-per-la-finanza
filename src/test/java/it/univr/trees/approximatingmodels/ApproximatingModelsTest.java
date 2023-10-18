package it.univr.trees.approximatingmodels;


import it.univr.usefulmethodsarrays.UsefulMethodsForArrays;


public class ApproximatingModelsTest {


	public static void main(String[] strings) throws Exception {

		double spotPrice = 100;
		double riskFreeRate = 0.0;
		double volatility = 0.2;
		double lastTime = 1.0;
		int numberOfTimes = 3;

		double strike = 100;
		
		//primo test di Cox-Ross-Rubinstein
		CoxRossRubinsteinModel firstApproximatingModel = new CoxRossRubinsteinModel(
				spotPrice, riskFreeRate, volatility,  lastTime, numberOfTimes);
		
		double[] valuesAtFinalTime =
				firstApproximatingModel.getValuesAtGivenTime(lastTime);
		
		double[] probabilitiesOfValuesAtFinalTime =
				firstApproximatingModel.getValuesProbabilitiesAtGivenTime(lastTime);

		double expectedValue =
				UsefulMethodsForArrays.getScalarProductTwoArrays(valuesAtFinalTime, probabilitiesOfValuesAtFinalTime);

		System.out.println("The expected value at final time for CRB is: " + expectedValue);
		
		
		//primo test di Leisen-Reimer
		LeisenReimerModel secondApproximatingModel = new LeisenReimerModel(
				spotPrice, riskFreeRate, volatility,  lastTime, numberOfTimes, strike);
		
		double[] valuesAtFinalTimeLeisenReimer =
				secondApproximatingModel.getValuesAtGivenTime(lastTime);
		
		double[] probabilitiesOfValuesAtFinalTimeLeisenReimer =
				secondApproximatingModel.getValuesProbabilitiesAtGivenTime(lastTime);

		double expectedValueLeisenReimer =
				UsefulMethodsForArrays.getScalarProductTwoArrays(valuesAtFinalTimeLeisenReimer, probabilitiesOfValuesAtFinalTimeLeisenReimer);

		System.out.println("The expected value at final time for LR is: " + expectedValueLeisenReimer);
		
		//primo test di Jarrow-Rudd
		JarrowRuddModel thirdApproximatingModel = new JarrowRuddModel(
				spotPrice, riskFreeRate, volatility,  lastTime, numberOfTimes);
		
		double[] valuesAtFinalTimeJarrowRudd =
				thirdApproximatingModel.getValuesAtGivenTime(lastTime);
		
		double[] probabilitiesOfValuesAtFinalTimeJarrowRudd =
				thirdApproximatingModel.getValuesProbabilitiesAtGivenTime(lastTime);

		double expectedValueJarrowRudd =
				UsefulMethodsForArrays.getScalarProductTwoArrays(valuesAtFinalTimeJarrowRudd, probabilitiesOfValuesAtFinalTimeJarrowRudd);

		System.out.println("The expected value at final time for JR is: " + expectedValueJarrowRudd);
		
	}
}
