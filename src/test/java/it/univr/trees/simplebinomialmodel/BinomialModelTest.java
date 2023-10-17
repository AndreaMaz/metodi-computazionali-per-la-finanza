package it.univr.trees.simplebinomialmodel;

import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

import it.univr.usefulmethodsarrays.UsefulMethodsForArrays;

public class BinomialModelTest {

	
	public static void main(String[] strings) {

		double upFactor = 2;
		double downFactor = 0.5;
		int numberOfTimes = 8;
		double initialValue = 100;
		double riskFreeFactor = 0.0;
		
		BinomialModel myBinomialModel = new BinomialModel(upFactor,  downFactor, riskFreeFactor, initialValue,  numberOfTimes);
		
		
		double[] values = myBinomialModel.getValuesAtGivenTimeIndex(2);
		System.out.println(Arrays.toString(values));
		
		for (int timeIndex = 0; timeIndex < numberOfTimes; timeIndex ++) {
			values = myBinomialModel.getValuesAtGivenTimeIndex(timeIndex);
			System.out.println(Arrays.toString(values));
		}
//		System.out.println();
//		for (int timeIndex = 0; timeIndex < numberOfTimes; timeIndex ++) {
//			double[] valuesProbabilities = myBinomialModel.getValuesProbabilitiesAtGivenTimeIndex(timeIndex);
//			System.out.println(Arrays.toString(valuesProbabilities));
//		}
		
		//now we want to compute the expected value of a binomial (or digital) option: the payoff is 1 if S(T)>K, 0 if not
		
		int lastTimeIndex = numberOfTimes - 1;
		
		double strike = initialValue;
		
		//ternary operator: (booleanExpression ? vlueIfBooleanTrue : vlueIfBooleanFalse)
		DoubleUnaryOperator payoffFunction = (x) -> (x>strike ? 1.0 : 0.0);//digital
		//DoubleUnaryOperator payoffFunction = (x) -> (x);//this would have been the identity: what expected value can we expect?

		//call would have been:
		//DoubleUnaryOperator payoffFunction = (x) -> (x>strike ? x-strike : 0.0 );
		
		System.out.println();
		System.out.println("payoffFuntion(104) = " + payoffFunction.applyAsDouble(104));
		System.out.println("payoffFuntion(99) = " + payoffFunction.applyAsDouble(99));

		double[] possiblePayoffsAtFinalTimeIndex = 
				myBinomialModel.getTransformedValuesAtGivenTimeIndex(lastTimeIndex, payoffFunction);
		
		System.out.println(Arrays.toString(possiblePayoffsAtFinalTimeIndex));

		double[] probabilitiesOfTheValuesAtFinalTimes =
				myBinomialModel.getValuesProbabilitiesAtGivenTimeIndex(lastTimeIndex);
		
		double expectedValue = UsefulMethodsForArrays.getScalarProductTwoArrays(possiblePayoffsAtFinalTimeIndex, probabilitiesOfTheValuesAtFinalTimes);
		
		System.out.println();
		System.out.println("Expected value = " + expectedValue);

				
		//now we compute the expected value as Payoff(S_0u^nd^0)*Q(S_0u^nd^0)+Payoff(S_0u^(n-1)d^1)*Q(S_0u^(n-1)d^1)+...+Payoff(S_0u^(n-1)d^1)*Q(S_0u^0d^n)
		



	}
}
