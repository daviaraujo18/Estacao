/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import javafx.scene.web.WebEngine;

import java.util.Random;

/**
 *
 * @author aers
 */
public class The {
	
	public static Object inserirJavascript(WebEngine webEngine, String script){
		return webEngine.executeScript(script);
	}

	public static int getRandomNumberBetween(int inicio, int fim) {

		Random random = new Random();

		if (inicio > fim) {
			throw new IllegalArgumentException("Start cannot exceed End.");
		}

		long range = (long)fim - (long)inicio + 1;
		long fraction = (long)(range * random.nextDouble());

		int randomNumber =  (int)(fraction + inicio);

		return randomNumber;
	}

}
