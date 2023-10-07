package it.univr.trees.assetderivativevaluation.products;

import java.util.function.DoubleUnaryOperator;

import it.univr.trees.approximatingmodels.ApproximatingBinomialModel;
import it.univr.usefulmethodsarrays.UsefulMethodsForArrays;

public class EuropeanBarrierOption {

	private double maturity;
	private DoubleUnaryOperator payoffFunction;
	private DoubleUnaryOperator barrierFunction;;
	


	public EuropeanBarrierOption(double maturity, DoubleUnaryOperator payoffFunction, double lowerBarrier,
			double upperBarrier) {
		this.maturity = maturity;
		this.payoffFunction = payoffFunction;
		barrierFunction = (x) -> (x>lowerBarrier & x < upperBarrier ? 1 : 0);
	}
	
	
	public double getValue(ApproximatingBinomialModel approximatingBinomialModel) {
		double[] optionValuesWithoutBarrier = approximatingBinomialModel.getTransformedValuesAtGivenTime(maturity, payoffFunction);
		
		double[] underlyingValues = approximatingBinomialModel.getValuesAtGivenTime(maturity);
		double[] areTheUnderlyingValuesInsideInterval = UsefulMethodsForArrays.applyFunctionToArray(underlyingValues, barrierFunction);
		 
		double[] optionValues = UsefulMethodsForArrays.multArrays(optionValuesWithoutBarrier, areTheUnderlyingValuesInsideInterval);

		int numberOfTimes = (int) Math.round(maturity/approximatingBinomialModel.getTimeStep());
		for (int timeIndex = numberOfTimes - 1; timeIndex >= 0; timeIndex--) {
        	double[] conditionalExpectation = approximatingBinomialModel.getConditionalExpectation(optionValues, timeIndex);
        	underlyingValues = approximatingBinomialModel.getValuesAtGivenTimeIndex(timeIndex);
        	areTheUnderlyingValuesInsideInterval = UsefulMethodsForArrays.applyFunctionToArray(underlyingValues, barrierFunction);
        	double[] transformedConditionalExpectation = UsefulMethodsForArrays.multArrays(conditionalExpectation, areTheUnderlyingValuesInsideInterval);
        	optionValues = transformedConditionalExpectation;  

        }
		return optionValues[0];
	}
}
