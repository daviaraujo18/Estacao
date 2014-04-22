/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;

import controllers.MainController;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;

/**
 *
 * Classe que escutara todo evento javascript 'alert('')' das paginas abertas
 * pelo browser integrado
 *
 * Caso o conteudo do alert contenha 'recuperarFrequentadores', o sistema ira
 * guardar em memoria todos os frequentadores que estaram guardados em uma
 * variavel javascript dentro da pagina ( window.bdFrequencia )
 *
 * @author aers
 */
public class OnAlertListener implements EventHandler {

    @Override
    public void handle(Event t) {

        WebEngine webEngine = MainController.INSTANCE.tela.getWebEngine();

        if (t instanceof WebEvent) {
            WebEvent event = (WebEvent) t;
            String metodo = event.getData().toString();
            boolean eventoTratado = false;
            for(Operacao operacao : Operacao.values()){
                if(operacao.verificarAplicabilidade(metodo, webEngine)){
                    eventoTratado = true;
                    operacao.execute(metodo, webEngine);
                }
            }
            if(!eventoTratado && metodo.contains("comando")){
                processarComando(webEngine);
            }
        }
    }

    private void processarComando(WebEngine webEngine) {
        Object comando = webEngine.executeScript("window.comando");
        System.out.println("Comando estação: " + comando.toString());
        if (!comando.toString().equals("undefined") && !comando.toString().equals("")) {
            if (comando.toString().equals("FECHAR")) {
                try {
                    System.out.println("Fechou");

                    String path = new File("..").getCanonicalPath();

                    System.out.println("Tentando executar: "+path+"\\EstacaoPonto\\runOpenUpdate.bat");
                    Process p =  Runtime.getRuntime().exec("cmd.exe /c start runOpenUpdate.bat",null,new File(path+"\\EstacaoPonto") );

                    Platform.exit();
                    System.exit(0);
                } catch (Exception ex) {
                    Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
