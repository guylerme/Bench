package br.guylerme.bench.core.engine;

public class TestAccuracy {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AccuracyCalculator a = new AccuracyCalculator();
		
		double x = a.calculateAccuracy(100, 10, 5);

		System.out.print(x);
		
	}

}
