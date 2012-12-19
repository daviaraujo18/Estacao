/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jus.tjpi.listeners;

import br.jus.tjpi.IntranetURLsConstants;
import br.jus.tjpi.MainController;
import br.jus.tjpi.utils.Log;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;

/**
 * 
 * Classe que escutara todo evento javascript 'alert('')' das paginas
 * abertas pelo browser integrado
 * 
 * Caso o conteudo do alert contenha 'recuperarFrequentadores',
 * o sistema ira guardar em memoria todos os frequentadores que estaram
 * guardados em uma variavel javascript dentro da pagina ( window.bdFrequencia )
 *
 * @author aers
 */
public class OnAlertListener implements EventHandler {
    
    
    private final MainController mainController;

    
    public OnAlertListener(MainController mainController) {
        this.mainController = mainController;
    }
    
    @Override
    public void handle(Event t) {
        
        WebEngine webEngine = mainController.getWebEngine();
        
        if(t instanceof WebEvent) {
            WebEvent event = (WebEvent) t;
            if(event.getData().toString().equals("recuperarFrequentadores") &&
                    webEngine.getLocation().contains("tjpi/presenca/InicializarPonto")) {
            
                Log.i("Iniciando download dos dados dos Frequentadores");
                
                double inicioDownload = System.currentTimeMillis();
                
                Object data = webEngine.executeScript("window.bdFrequencia");
                String dataFixed = (String) data.toString().replace("\n", "");
                Log.i("Montando dados");
                String[] array = ((String)dataFixed).split("'");
                String[] dados;
                
                // Adiciona os dados ao NBio_SearchIndex
                
                
                double fimDownload = System.currentTimeMillis();
                Log.i("Montagem finalizada");
                Log.i("Time elapsed: "+(fimDownload - inicioDownload)+" ms");
                
                webEngine.load(IntranetURLsConstants.BATIMENTO_PONTO_COM_CODIGOS);
            } else if(event.getData().toString().equals("recuperarCodigoAtivacao") &&
                    webEngine.getLocation().contains("tjpi/presenca/RecuperarCodigoAtivacao")) {
                Log.i("Recuperando CodigoDeAtivacao e setando no Registro do Windows");
                
                Object data = webEngine.executeScript("jQuery('#codigoAtivacao').val();");
                
                System.out.println("Codgio Ativacao: "+data.toString());
            }
        }
    }
    
}
