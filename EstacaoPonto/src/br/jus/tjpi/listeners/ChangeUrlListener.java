/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jus.tjpi.listeners;

import br.jus.tjpi.MainController;
import br.jus.tjpi.utils.Log;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;

/**
 * Classe que verifica toda vez que ocorre uma mudança de página
 * e faz as devidas modificaçoes dependendo da url atual
 * 
 * @author Anderson Soares
 */
public class ChangeUrlListener implements ChangeListener<Object> {

    private MainController mainController;
    
    public ChangeUrlListener(MainController mainController) {
        this.mainController = mainController;
    }
    
    @Override
    public void changed(ObservableValue<? extends Object> ov, Object t, Object t1) {
        
        if(ov.getValue().equals(Worker.State.SCHEDULED)) {
            if(mainController.getWebEngine().getLocation().contains("tjpi/presenca/cadastrar")) {
                mainController.getSplitPanel().setDividerPosition(1, 0);
            }
        } else {
        
            if(ov.getValue().equals(Worker.State.SUCCEEDED)) {

                Log.i("Pagina '"+mainController.getWebEngine().getLocation()+"' carregada");

            }
        }
    }
    
}
