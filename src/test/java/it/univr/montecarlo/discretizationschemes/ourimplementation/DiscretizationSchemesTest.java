package it.univr.montecarlo.discretizationschemes.ourimplementation;

import java.util.Random;

import it.univr.usefulmethodsarrays.UsefulMethodsForArrays;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

public class DiscretizationSchemesTest {

	public static void main(String[] args) {
		double initialValue = 100.0;
		double volatility = 0.3;
		double muDrift = 0.0;
		double finalTime = 2.0;
		double timeStep = 0.05;
		int numberOfTimesSteps = (int) (finalTime/timeStep);
		TimeDiscretization times = new TimeDiscretizationFromArray(0.0, numberOfTimesSteps, timeStep);

		
		int numberOfSimulatedPaths = 100000;
		
		int numberOfTests = 100;
		
		double[] expectedValuesEulerMaruyama = new double[numberOfTests];
		double[] expectedValuesMilstein = new double[numberOfTests];
		double[] expectedValuesEulerMaruyamaForLogarithm = new double[numberOfTests];

		

	}

}
