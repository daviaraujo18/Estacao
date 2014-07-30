/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;

import controllers.MainController;
import core.LocalPaths;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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

 private void processarComando(WebEngine webEngine) 
{           

            Object comando = webEngine.executeScript("window.comando");
            if (!("NADA".equals(comando.toString())))
            {
                System.out.println("Comando estação: " + comando.toString());
            }
            

            if (!comando.toString().equals("undefined") && !comando.toString().equals("")) {
                if (comando.toString().equals("FECHAR")) {
                    try {
                        System.out.println("Fechou");
                        Platform.exit();
                        System.exit(0);
                    } catch (Exception ex) {
                        Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else{
                if (comando.toString().startsWith("LOG")) {
                    
                    MainController.INSTANCE.nomeLog=comando.toString();
                    Path path = Paths.get(LocalPaths.PATH_LOG+comando.toString());

                    List<String> lines=null;
                    try {
                        lines = Files.readAllLines(path, Charset.forName("ISO-8859-1"));
                    } catch (IOException ex) {
                        Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    MainController.INSTANCE.arr = lines.toArray(new String[lines.size()]);
                    System.out.println("tamanho: "+MainController.INSTANCE.arr.length);
                    MainController.INSTANCE.addUploadFile(MainController.INSTANCE.arr.length);
                 
                
                }else{
                    if (comando.toString().equals("doUpload")) {
                        System.out.println("adicionando partes");
                        MainController.INSTANCE.doUploadParte();

                    }
                    else{
                if (comando.toString().equals("INICIAR")) {
                    try 
                    {
                        try {
                            String path = new File("..").getCanonicalPath();
                            System.out.println("Tentando executar: "+path+"\\OUA\\runEstacao.bat");
//                            Process p =  Runtime.getRuntime().exec("cmd.exe /c start runEstacao.bat",null,new File(path+"\\OUA") );
                        } 
                        catch (IOException ex) {
                            Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.out.println("Iniciou");
//                        Platform.exit();
//                        System.exit(0);
                    }
                    catch (Exception ex) 
                    {
                        Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }}}}
            }
        }
}
