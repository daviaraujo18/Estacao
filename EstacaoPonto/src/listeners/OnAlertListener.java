/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;

import controllers.MainController;
import core.IntranetURLs;
import core.RegistroWindows;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import utils.Log;

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

    private final MainController mainController;

    public OnAlertListener(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void handle(Event t) {

        WebEngine webEngine = mainController.getWebEngine();

        if (t instanceof WebEvent) {
            WebEvent event = (WebEvent) t;
            if (event.getData().toString().equals("callRecuperarFrequentadores")
                    && webEngine.getLocation().contains("tjpi/presenca/PontoDePresenca")) {

                Log.i("Iniciando download dos dados dos Frequentadores");

                double inicioDownload = System.currentTimeMillis();

                Object data = webEngine.executeScript("window.bdFrequencia");
                // id;matricula;nome;digital;foto
                String dataFixed = (String) data.toString().replace("\n", "");

//				System.out.println("DATA RECEBIDA: "+dataFixed);
                Log.i("Montando dados");


                mainController.setArrayFrequentadores(((String) dataFixed).split("'"));
                String[] frequentadores = mainController.getArrayFrequentadores();
                HashMap<String, String> mapaIdHashFrequentadores = new HashMap<>();
                mainController.setMapaIdInfoFrequentadores(new HashMap<Integer, String>());



//				mapaIdHashFrequentadores.putAll(new TesteDigitaisCVS().lerDigitaisCVS());



                System.out.println("----Frequentadores recebidos: ");
                if (frequentadores.length > 0 && !frequentadores[0].isEmpty()) {
                    for (int i = 0; i < frequentadores.length; i++) {
                        String[] dados = frequentadores[i].split(";");
                        String id = dados[0];
                        String hashDigital = dados[3];
                        String dadosF = dados[1] + ";" + dados[2] + ";" + dados[4];// matricula;nome;foto
                        System.out.println("Inserindo dados: " + dadosF);
                        mainController.getMapaIdInfoFrequentadores().put(Integer.parseInt(id), dadosF);
                        mapaIdHashFrequentadores.put(id, hashDigital);
                    }
                }
                try {
                    mainController.getLeitorDigital().addDigitalToIndexSearch(mapaIdHashFrequentadores);
                } catch (Exception e) {
                    Log.i("Leitor nao iniciado: " + e.getMessage());
                }
                System.out.println("----Fim.");


                // Adiciona os dados ao NBio_SearchIndex


                double fimDownload = System.currentTimeMillis();
                Log.i("Montagem finalizada");
                Log.i("Time elapsed: " + (fimDownload - inicioDownload) + " ms");

//                webEngine.load(IntranetURLsConstants.BATIMENTO_PONTO_COM_CODIGOS);
            } else if (event.getData().toString().equals("recuperarCodigoAtivacao")
                    && webEngine.getLocation().contains("tjpi/presenca/RecuperarCodigoAtivacao")) {
                Log.i("Recuperando CodigoDeAtivacao e setando no Registro do Windows");

                Object data = webEngine.executeScript("jQuery('#codigoAtivacao').val();");

                RegistroWindows.registrarCodigoAtivacao(data.toString());

                webEngine.load(IntranetURLs.INICIALIZAR_PONTO + IntranetURLs.getCodigos());
            } else if (event.getData().toString().equals("callLeitorDigital")) {
                mainController.capturarDigital();
                
            } else if (event.getData().toString().contains("horarioServidorAtual")) {
                String[] horario = event.getData().toString().split(":");

                int dia = Integer.parseInt(horario[1]);
                int mes = Integer.parseInt(horario[2]);
                int ano = Integer.parseInt(horario[3]);
                int hora = Integer.parseInt(horario[4]);
                int minutos = Integer.parseInt(horario[5]);
                Calendar dataServidor = Calendar.getInstance();
                dataServidor.set(ano, mes, dia, hora, minutos);
                mainController.criarThreadRelogio(dataServidor);
            } else if (event.getData().toString().equals("atualizarRelogioLocal")) {
                if (mainController.getThreadRelogio() != null) {
                    String horario = mainController.getThreadRelogio().atualizarRelogio();
                    try {
                        System.out.println("Atualizando...");
                        mainController.atualizarHorario(horario);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else if (event.getData().toString().equals("limparRegistosBatimentos")) {
                try {
                    mainController.apagarRegistrosBatimentos();
                } catch (IOException ex) {
                    Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (event.getData().toString().equals("Sincronizando")) {
                System.out.println("ALERT Sincronizando...");
            } 
            else if (event.getData().toString().contains("pontoAtivo"))
            {
                System.out.println("pontoAtivo");
            }
            else if (event.getData().toString().contains("Esperar"))
            {
                System.out.println("Esperar");
            }
            else if (event.getData().toString().contains("Sincronizar Agora"))
            {
                System.out.println("Sincronizando...");
                try {
                    mainController.iniciarSincronizacao();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }
}
