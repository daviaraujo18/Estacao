package core;


import controllers.MainController;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import utils.ArquivoUtils;
import utils.LogAplicacao;
import utils.The;
import view.TelaPonto;

import static utils.ArquivoUtils.readMapOnFile;
import static utils.ArquivoUtils.saveMapOnFile;

/**
 * Created by Danilo on 10/02/14.
 */
public class DadosFrequentadores  {

    private static DadosFrequentadores INSTANCE;

    private String[] arrayFrequentadores;
    private HashMap<String, String> hashFrequentadores;
    private Map<Integer,String> frequentadores;
    private Map<Integer,String> administradores;
    private Map<Integer,String> mapaIdFotosFrequentadores;
    private String data;

    public static DadosFrequentadores getInstance(){
        if(INSTANCE == null){
            INSTANCE = new DadosFrequentadores();
        }
        return INSTANCE;
    }

    private DadosFrequentadores(){

    }

    public void init(final String data){
        LogAplicacao.i("Estruturando dados dos frequentadores");
        this.data = data;

        /**
         * criar mapFrequentadores
         * criar mapAdminsitradores
         * criar mapFotos
         */
        if (data == null || data.isEmpty()) {
            try {
                frequentadores = readMapOnFile("f");
                administradores = readMapOnFile("a");
                mapaIdFotosFrequentadores = readMapOnFile("fotos");
            } catch (Exception e) {
                LogAplicacao.e("Nao foi possivel carregar dados offline");
                // remover hash
                File file = new File(LocalPaths.PATH_DATA,"hash");

                if(file.delete()){
                    LogAplicacao.i("Removido hash file");
                }else{
                    LogAplicacao.e("Não consegui remover hash");
                }
            }

        } else {

            setArrayFrequentadores(((String) DadosFrequentadores.getInstance().getData()).split("'"));

            String[] frequentadores = getArrayFrequentadores();

            hashFrequentadores = new HashMap();
            setFrequentadores(new HashMap<Integer, String>());
            setAdministradores(new HashMap<Integer, String>());
            setMapaIdFotosFrequentadores(new HashMap<Integer, String>());
            int total = 0;
            if (frequentadores.length > 0 && !frequentadores[0].isEmpty()) {
                for (int i = 0; i < frequentadores.length; i++) {
                    // id;matricula;nome;digital;seEhAdminDaEstacao;sexo;foto
                    String[] dados = frequentadores[i].trim().split(";");
                    String id = dados[0];
                    String hashDigital = dados[3];
                    String isAdmin = dados[5];


                    String foto = dados[4];
                    String sexo = dados[6];

                    //matricula, nome, digital
//                        String dadosF = dados[1] + ";" + dados[2] + ";" + dados[4] + ";" + dados[6];// matricula;nome;foto;sexo
                    String dadosF = dados[1] + ";" + dados[2] + ";" + dados[4] + ";" + dados[6] + ";" + dados[7] + ";";// matricula;nome;foto;sexo;localTrabalho;
                    getFrequentadores().put(Integer.parseInt(id), dadosF);
                    if (isAdmin.equals("true")) {
                        getAdministradores().put(Integer.parseInt(id), dadosF);
                    }
                    hashFrequentadores.put(id, hashDigital);
                    getmapaIdFotosFrequentadores().put(Integer.parseInt(id), foto);
                    total = i;
                }
            }


            try {
                ArquivoUtils.saveMapOnFile(getFrequentadores(), "f");
//                saveMapOnFile(getAdministradores(), LocalPaths.PATH_DATA + "a");
                ArquivoUtils.saveMapOnFile(new HashMap<Integer, String>(), "a");
                ArquivoUtils.saveMapOnFile(getmapaIdFotosFrequentadores(), "fotos");
            } catch (Exception e) {
                LogAplicacao.e("Não foi possível salvar os dados");
            }
            LogAplicacao.i("Finalizado. Total de frequentadores: " + total);
        }


        Task task;
        task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                // Adiciona os dados ao NBio_SearchIndex
                try {
                    if (data == null || data.isEmpty()) {
                        LogAplicacao.i("data: " +data);
                        MainController.INSTANCE.getLeitorDigital().loadDB();
                    } else {
                        MainController.INSTANCE.getLeitorDigital().addDigitalToIndexSearch(hashFrequentadores);
                    }
                } catch (Exception e) {
                    LogAplicacao.e(e.getMessage());
                    LogAplicacao.e("Não foi possível adicionar dados ao IndexSearch");
                }
                return null;
            }
        };

        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
                CacheDownloadService downloads = new CacheDownloadService(getmapaIdFotosFrequentadores());
                Thread novo = new Thread(downloads);
                downloads.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

                    @Override
                    public void handle(WorkerStateEvent t) {
                        The.inserirJavascript(TelaPonto.INSTANCE.getWebEngine(), "aguardarDigital();");
                        MainController.INSTANCE.reiniciarCapturaDigital();
                        TelaPonto.INSTANCE.getSplitPanel().getDividers().get(1).setPosition(0.999);
                        TelaPonto.INSTANCE.getBotaoCadastrarDigital().setVisible(false);
                        TelaPonto.INSTANCE.getBotaoAtualizarDigital().setVisible(false);
                        TelaPonto.INSTANCE.getProgressBar().setVisible(false);
                        TelaPonto.INSTANCE.getLabelProgressBar().setVisible(false);
                    }

                });
                TelaPonto.INSTANCE.getSplitPanel().getDividers().get(1).setPosition(0.5);
                TelaPonto.INSTANCE.getBotaoCadastrarDigital().setVisible(false);
                TelaPonto.INSTANCE.getBotaoAtualizarDigital().setVisible(false);
                TelaPonto.INSTANCE.getProgressBar().setVisible(true);
                TelaPonto.INSTANCE.getLabelProgressBar().setVisible(true);

                novo.start();
                TelaPonto.INSTANCE.getProgressBar().progressProperty().bind(downloads.progressProperty());
                TelaPonto.INSTANCE.getLabelProgressBar().textProperty().bind(downloads.messageProperty());
            }

        });

        new Thread(task).start();
    }


    /*
     * Recupera o array com as informacoes dos frequentadores
     * @return String[] - array com as informacoes dos frequentadores ("id;matricula;nome;digital;foto")
     */
    public String[] getArrayFrequentadores() {
        return arrayFrequentadores;
    }

    /*
     * Altera o array com os dados dos frequemtadores ("id;matricula;nome;digital;foto")
     * @param String[] - array com os dados dos frequentadores
     */
    public void setArrayFrequentadores(String[] arrayFrequentadores) {
        this.arrayFrequentadores = arrayFrequentadores;
    }

    /*
     * Recupera o map com as informacoes dos frequentadores (id,"matricula;nome;foto")
     * @return Map<Integer, String> - map com as informacoes dos frequentadores
     */
    public Map<Integer, String> getFrequentadores() {
        return frequentadores;
    }

    /*
     * Altera o map com as informacoes dos frequentadores - (nome, matricula, digital...)
     * @param Map<Integer, String> - map com as informacoes dos frequentadores
     */
    public void setFrequentadores(Map<Integer, String> mapaIdInfoFrequentadores) {
        this.frequentadores = mapaIdInfoFrequentadores;
    }


    public Map<Integer,String> getAdministradores(){return this.administradores;}

    public void setAdministradores(HashMap<Integer,String> administradores) {
        this.administradores = administradores;
    }

    private Map<Integer, String> getmapaIdFotosFrequentadores() {
        return mapaIdFotosFrequentadores;
    }

    private void setMapaIdFotosFrequentadores(Map<Integer, String> mapaIdFotosFrequentadores) {
        this.mapaIdFotosFrequentadores = mapaIdFotosFrequentadores;
    }
    public String getData(){
        return data;
    }

}