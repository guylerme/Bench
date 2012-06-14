package br.guylerme.bench.core.engine;

/**
 * @author Guylerme Figueiredo
 * 
 */
public class AccuracyCalculator {

	/**
	 * Metodo para calcular a precisao de um algoritmo de alinhamento.
	 * 
	 * @param n
	 *            numero de similaridades encontradas pelo algoritmo
	 * @param m
	 *            numero de similaridades esperadas
	 * @param c
	 *            numero de similaridades encontradas corretas
	 * @return valor da precisao calculada
	 */
	public double calculateAccuracy(int n, int m, int c) {

		return 1 - ((n - c) + (m - c)) / m;
	}
}
