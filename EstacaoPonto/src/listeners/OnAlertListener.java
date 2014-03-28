/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;

import controllers.MainController;
import core.DadosFrequentadores;
import core.IntranetURLs;
import core.RegistroWindows;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import utils.Log;
import view.TelaPonto;

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

   

    /*
        REFACTOR TO CHAIN OF RESPONSABILITY
     */
    @Override
    public void handle(Event t) {

        WebEngine webEngine = MainController.INSTANCE.tela.getWebEngine();

        if (t instanceof WebEvent) {
            WebEvent event = (WebEvent) t;
            String metodoAlerta = event.getData().toString();
            if (metodoAlerta.equals("callRecuperarFrequentadores")
                    && webEngine.getLocation().contains("tjpi/presenca/PontoDePresenca")) {       
                Log.i("Iniciando download dos dados dos Frequentadores");
                double inicioDownload = System.currentTimeMillis();
                Object data = webEngine.executeScript("window.bdFrequencia");

                String dataFixed = (String) data.toString().replace("\n", "");
                //System.out.println("dados Baixados: " + dataFixed);
                Log.i("Montando dados");
                    DadosFrequentadores.getInstance().init(dataFixed);
                double fimDownload = System.currentTimeMillis();            
                Log.i("Montagem finalizada");
                Log.i("Time elapsed: " + (fimDownload - inicioDownload) + " ms");
                String horario = MainController.INSTANCE.getThreadRelogio().atualizarRelogio();
                try {
                    MainController.INSTANCE.atualizarHorario(horario);
                } catch (IOException ex) {
                    Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            } else if (metodoAlerta.equals("recuperarCodigoAtivacao")
                    && webEngine.getLocation().contains("tjpi/presenca/RecuperarCodigoAtivacao")) {
                Log.i("Recuperando CodigoDeAtivacao e setando no Registro do Windows");

                Object data = webEngine.executeScript("jQuery('#codigoAtivacao').val();");

                RegistroWindows.registrarCodigoAtivacao(data.toString());

                webEngine.load(IntranetURLs.INICIALIZAR_PONTO + IntranetURLs.getCodigos());
            } else if (metodoAlerta.contains("horarioServidorAtual")) {
                String[] horario = metodoAlerta.split(":");

                int dia = Integer.parseInt(horario[1]);
                int mes = Integer.parseInt(horario[2]);
                int ano = Integer.parseInt(horario[3]);
                int hora = Integer.parseInt(horario[4]);
                int minutos = Integer.parseInt(horario[5]);
                Calendar dataServidor = Calendar.getInstance();
                dataServidor.set(ano, mes, dia, hora, minutos);
                MainController.INSTANCE.criarThreadRelogio(dataServidor);
            } else if (metodoAlerta.equals("atualizarRelogioLocal")) {
                System.out.println("Recebendo requisição para atualizar horário na página");
                if (MainController.INSTANCE.getThreadRelogio() != null) {
                    String horario = MainController.INSTANCE.getThreadRelogio().atualizarRelogio();
                    try {
                        System.out.println("Atualizando...");
                        MainController.INSTANCE.atualizarHorario(horario);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else if (metodoAlerta.equals("limparRegistosBatimentos")) {
                try {
                    MainController.INSTANCE.apagarRegistrosBatimentos();
                } catch (IOException ex) {
                    Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (metodoAlerta.equals("Sincronizando")) {
                System.out.println("ALERT Sincronizando...");
            }
            else if (metodoAlerta.contains("Sincronizar Agora"))
            {
                System.out.println("Sincronizando...");
                try {
                    MainController.INSTANCE.iniciarSincronizacao();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }
}
