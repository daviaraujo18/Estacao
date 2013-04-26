/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;

import core.IntranetURLs;
import controllers.MainController;
import core.RegistroWindows;
import core.LeitorDigital;
import utils.Log;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
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
            Log.i("Carregando pagina: "+mainController.getWebEngine().getLocation());
            if(urlAtualContem("presenca/Frequentador")) {
                mainController.getSplitPanel().getDividers().get(1).setPosition(0.5);
            } else {
                mainController.getSplitPanel().getDividers().get(1).setPosition(0.999);
            }
        } else {
        
            if(ov.getValue().equals(Worker.State.SUCCEEDED)) {
				Log.i("Pagina carregada: "+mainController.getWebEngine().getLocation());
                if(urlAtualContem("EstacaoPonto?type=create")) {
                    Log.i("Injetando codigos no formulário via JavaScript");
                    setarInputCodigos();
                } else if(urlAtualContem("presenca/IniciarPonto")) {
					Log.i("Entrei no metodo");
                    mudarUrlAtualPara(IntranetURLs.INICIALIZAR_PONTO+IntranetURLs.getCodigos());
                }
            }
        }
    }
    
    
    private void setarInputCodigos() {
        WebEngine webEngine = mainController.getWebEngine();
        
        String codigoAtivacao = RegistroWindows.gerarCodigoAtivacao();
        String codigoUnicoMaquina = RegistroWindows.getCodigoUnicoMaquina();
        webEngine.executeScript(""
                + "jQuery('#codigoUnicoMaquina').val('"+codigoUnicoMaquina+"');"
                + "jQuery('#codigoAtivacao').val('"+codigoAtivacao+"');");
    }
	
	private boolean urlAtualContem(String texto) {
		return mainController.getWebEngine().getLocation().contains(texto);
	}

	private void mudarUrlAtualPara(String novaURL) {
		mainController.getWebEngine().load(novaURL);
	}
    
}
