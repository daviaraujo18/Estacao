/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jus.tjpi.models;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Anderson Soares
 */
public class Frequentador extends HashMap {
    
    private int id;
    private List<String> digitais;
    
    public Frequentador(int id, List digitais) {
        this.id = id;
        this.digitais = digitais;
    }

  
    
    
}
