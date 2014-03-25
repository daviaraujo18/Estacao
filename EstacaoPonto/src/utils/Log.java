/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author Anderson Soares
 */
public class Log {

    public static void i(Object msg) {
        System.out.println("[LOG-INFO] "+msg.toString());
    }

    public static void e(Object msg) {
        System.out.println("[LOG-ERROR] "+msg.toString());
    }

}
