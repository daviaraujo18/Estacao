/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jus.tjpi.listeners;

import br.jus.tjpi.MainController;
import br.jus.tjpi.system.utils.EstacaoPontoUtils;
import br.jus.tjpi.utils.Log;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.web.WebEngine;

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
            if(mainController.getWebEngine().getLocation().contains("global")) {
                mainController.getSplitPanel().getDividers().get(1).setPosition(0.5);
            } else {
                mainController.getSplitPanel().getDividers().get(1).setPosition(0.999);
            }
        } else {
        
            if(ov.getValue().equals(Worker.State.SUCCEEDED)) {

                if(mainController.getWebEngine().getLocation().contains("EstacaoPonto?type=create")) {
                    Log.i("Injetando codigos no formulário via JavaScript");
                    setarInputCodigos();
                }
                Log.i("Pagina '"+mainController.getWebEngine().getLocation()+"' carregada");

            }
        }
    }
    
    
    private void setarInputCodigos() {
        WebEngine webEngine = mainController.getWebEngine();
        
        String codigoAtivacao = EstacaoPontoUtils.gerarCodigoAtivacao();
        String codigoUnicoMaquina = EstacaoPontoUtils.getCodigoUnicoMaquina();
        webEngine.executeScript(""
                + "jQuery('#codigoUnicoMaquina').val('"+codigoUnicoMaquina+"');"
                + "jQuery('#codigoAtivacao').val('"+codigoAtivacao+"');");
    }
    
}
