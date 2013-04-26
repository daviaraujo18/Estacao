/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import javafx.scene.web.WebEngine;

/**
 *
 * @author aers
 */
public class The {
	
	public static Object inserirJavascript(WebEngine webEngine, String script){
		return webEngine.executeScript(script);
	}
	
}
