/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;


public class LogEstacao {

    static final Logger logger = LogManager.getLogger(LogEstacao.class.getName());

    public static void i(Object msg) {
        logger.info(msg);
    }
    public static void w(Object msg) {
        logger.warn(msg);
    }
    public static void e(Object msg) {
        logger.error(msg);
    }
    public static void e(Exception ex) {
		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));
        logger.error(errors.toString());
    }
}
