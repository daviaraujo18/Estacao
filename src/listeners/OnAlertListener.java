/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;

import controllers.MainController;
import core.LocalPaths;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import utils.AtualizarEstacao;
import utils.Log;
import utils.ScriptsBat;

/**
 *
 * Classe que escutara todo evento javascript 'alert('')' das paginas abertas
 * pelo browser integrado
 *
 * Caso o conteudo do alert contenha 'recuperarFrequentadores', o sistema ira
 * guardar em memoria todos os frequentadores que estaram guardados em uma
 * variavel javascript dentro da pagina ( window.bdFrequencia )
 */
public class OnAlertListener implements EventHandler {

    @Override
    public void handle(Event t) {

        WebEngine webEngine = MainController.INSTANCE.tela.getWebEngine();

        if (t instanceof WebEvent) {
            WebEvent event = (WebEvent) t;
            String metodo = event.getData().toString();
            boolean eventoTratado = false;
//            Log.i("alerta: "+metodo);
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
                System.out.println("Comando estacao: " + comando.toString());
            }
            

            if (!comando.toString().equals("undefined") && !comando.toString().equals("")) {
                if (comando.toString().equals("FECHAR")) {
                    try {
                        System.out.println("Fechou");
                        System.out.println("acessando: "+LocalPaths.realPath+"\\runOpenUpdate.bat");

                        ScriptsBat.restartAplicacao();
                    } catch (Exception ex) {
						Log.e(ex.getMessage());
                        Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else{
                if (comando.toString().startsWith("LOG")) {
                    
                    MainController.INSTANCE.nomeLog=comando.toString();
                    String pathS = LocalPaths.PATH_LOG+comando.toString();
                    Path path = Paths.get(pathS);
                    System.out.println("pathS: "+pathS);
                    //List<String> lines=null;
                    List<String> result = new ArrayList<>();
                    try {
                        //lines = Files.readAllLines(path, Charset.forName("ISO-8859-1"));
                        LineNumberReader  lnr = new LineNumberReader(new FileReader(new File(pathS)));
                        lnr.skip(Long.MAX_VALUE);
//                        System.out.println(lnr.getLineNumber());
                        int tamanhoInicial = lnr.getLineNumber();
                        lnr.close();
                        int limite = 50000;
                        int tam = lnr.getLineNumber();//101k
                        
                        int inicio = 1;
                        if (tam>limite)
                        {
                            inicio= tam + 1 - limite;
                            tam=limite;
                        }
//                        System.out.println("tam: "+tam+" inicio: "+inicio);
                        
                            BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"));
                            
                            int linha=1;
                            for (;;) {
                                String line ="";
                                try{
                                    line = reader.readLine();
                                }
                                catch(Exception ex)
                                {
                                    Log.e(ex);
//									ex.printStackTrace();
                                }
                                if (line == null )  {
                                    break;
                                }
                                if (linha>=inicio)
                                {
                                    result.add(line);
                                    
                                }
                                linha++;
                                if (linha>tamanhoInicial)
                                {
                                    break;
                                }
                            }
                        
                
                    }catch (IOException ex) {
							Log.e(ex.getMessage());
                        Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    MainController.INSTANCE.arr = result.toArray(new String[result.size()]);
//                    System.out.println("tamanho: "+MainController.INSTANCE.arr.length);
                    MainController.INSTANCE.addUploadFile(MainController.INSTANCE.arr.length);
                 
                
                }else{
                    if (comando.toString().equals("doUpload")) {
//                        System.out.println("adicionando partes");
                        MainController.INSTANCE.doUploadParte();

                    }
                    else{
                if (comando.toString().equals("INICIAR")) {
//                    try 
//                    {
//                        try {
//                            String path = new File("..").getCanonicalPath();
//                            System.out.println("Tentando executar: "+path+"\\OUA\\runEstacao.bat");
////                            Process p =  Runtime.getRuntime().exec("cmd.exe /c start runEstacao.bat",null,new File(path+"\\OUA") );
//                        } 
//                        catch (IOException ex) {
//                            Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                        System.out.println("Iniciou");
////                        Platform.exit();
////                        System.exit(0);
////                    }
////                    catch (Exception ex) 
////                    {
////                        Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
////                    }
                }
                else
                {
                    if (comando.toString().equals("ATUALIZARESTACAO"))
                    {
              
                        AtualizarEstacao.downloadNovaVersao(null);
                    }
                }
                    }}}
            }
        }
}
