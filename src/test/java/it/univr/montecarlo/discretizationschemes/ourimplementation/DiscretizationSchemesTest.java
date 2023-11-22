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
		double finalTime = 1.0;
		double timeStep = 0.05;
		int numberOfTimesSteps = (int) (finalTime/timeStep);
		TimeDiscretization times = new TimeDiscretizationFromArray(0.0, numberOfTimesSteps, timeStep);

		
		int numberOfSimulatedPaths = 10000;
		
		int numberOfTests = 100;
		
		double[] expectedValuesEulerMaruyama = new double[numberOfTests];
		double[] expectedValuesMilstein = new double[numberOfTests];
		double[] expectedValuesEulerMaruyamaForLogarithm = new double[numberOfTests];
		
		Random seedGenerator = new Random();
		
		for (int i = 0; i<numberOfTests; i++) {
			int seed = seedGenerator.nextInt();
			AbstractProcessSimulation simulatorEulerMaruyama = new EulerSchemeForBlackScholes(
					volatility, muDrift, initialValue, numberOfSimulatedPaths, seed, times);
			AbstractProcessSimulation simulatorMilstein = new MilsteinSchemeForBlackScholes(
					volatility, muDrift, initialValue, numberOfSimulatedPaths, seed, times);
			AbstractProcessSimulation simulatorLogEuler = new LogEulerSchemeForBlackScholes(
					volatility, muDrift, initialValue, numberOfSimulatedPaths, seed, times);
			
			expectedValuesEulerMaruyama[i]=  simulatorEulerMaruyama.getFinalValue().getAverage();
			expectedValuesMilstein[i]=  simulatorMilstein.getFinalValue().getAverage();
			expectedValuesEulerMaruyamaForLogarithm[i]=  simulatorLogEuler.getFinalValue().getAverage();
		}
		
		System.out.println("Average Euler Maruyama: = " + UsefulMethodsForArrays.getAverage(expectedValuesEulerMaruyama));
		System.out.println("Average Milstein: = " + UsefulMethodsForArrays.getAverage(expectedValuesMilstein));
		System.out.println("Average Log Euler Maruyama: = " + UsefulMethodsForArrays.getAverage(expectedValuesEulerMaruyamaForLogarithm));
		
	}
}
