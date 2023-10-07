package it.univr.trees.assetderivativevaluation.products;

import java.util.function.DoubleUnaryOperator;

import it.univr.trees.approximatingmodels.ApproximatingBinomialModel;
import it.univr.usefulmethodsarrays.UsefulMethodsForArrays;

/**
 * This class implements the valuation of an European option non path dependent (that is, which pays only
 * according to the value of the underlying at maturity) via an approximation of a Black-Scholes process
 * with a Binomial model.
 * 
 * @author Andrea Mazzon
 *
 */
public class EuropeanNonPathDependentOption {

	private double maturity;
	private DoubleUnaryOperator payoffFunction;

	/**
	 * It constructs an object which represents the implementation of the European, non path dependent option.
	 * @param maturity, the maturity of the option
	 * @param payoffFunction, the funtion which identifies the payoff. The payoff is f(S_T) for payoffFunction
	 * f and underlying value S_T at maturity. The payoffFunction is represented by a DoubleUnaryOperator.
	 */
	public EuropeanNonPathDependentOption(double maturity, DoubleUnaryOperator payoffFunction) {
		this.maturity = maturity;
		this.payoffFunction = payoffFunction;
	}

	/**
	 * It returns the discounted value of the option written on the Black-Scholes model approximated by
	 * the object of type ApproximatingBinomialModel given in input. The value of the option is computed
	 * as the discounted expectation of the possible values at maturity. This expectation is computed by going backward
	 * from maturity to initial time and computing the iterative conditional expectation, see slides.
	 * 
	 * @param approximatingBinomialModel, the underlying
	 * @return the value of the option written on the underlying
	 */
	public double getValue(ApproximatingBinomialModel approximatingBinomialModel) {
		//the vector representing all the possible values of the payoff at maturity
		double[] optionValues = approximatingBinomialModel.getTransformedValuesAtGivenTime(maturity, payoffFunction);
		int numberOfTimes = (int) Math.round(maturity/approximatingBinomialModel.getTimeStep());
		//we go backward and for any timeIndex we compute the conditional expectation of the value of the option at timeIndex + 1
		for (int timeIndex = numberOfTimes - 1; timeIndex >= 0; timeIndex--) {
			//delegation to approximatingBinomialModel!
        	double[] conditionalExpectation = approximatingBinomialModel.getConditionalExpectation(optionValues, timeIndex);
            optionValues = conditionalExpectation;   
        }
		return optionValues[0];
	}
	
	/**
	 * It returns the discounted value of the option written on the Black-Scholes model approximated by
	 * the object of type ApproximatingBinomialModel given in input. The value of the option is computed
	 * as the discounted expectation of the possible values at maturity. This expectation is computed as the scalar product
	 * of the vector of the possible payoff values and the one of their probabilities. 
	 * 
	 * @param approximatingBinomialModel, the underlying
	 * @return the value of the option written on the underlying
	 */
	public double getValueDirect(ApproximatingBinomialModel approximatingBinomialModel) {
		//the values of the payoffs..
		double[] payoffValues = approximatingBinomialModel.getTransformedValuesAtGivenTime(maturity, payoffFunction);
		//and the corresponding probabilities
		double[] valuesProbabailities = approximatingBinomialModel.getValuesProbabilitiesAtGivenTime(maturity);
		//the we compute the weighted sum..
		double nonDiscountedValue = UsefulMethodsForArrays.getScalarProductTwoArrays(payoffValues, valuesProbabailities);
		double riskFreeRate = approximatingBinomialModel.getRiskFreeRate();
		//and discount
		return Math.exp(-riskFreeRate*maturity)*nonDiscountedValue;
	}
}
