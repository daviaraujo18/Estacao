package core;


import controllers.MainController;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import utils.Log;
import utils.The;
import view.TelaPonto;

/**
 * Created by Danilo on 10/02/14.
 */
public class DadosFrequentadores  {

    private static DadosFrequentadores INSTANCE;

    private String[] arrayFrequentadores;
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

    public void init(String data){
        this.data = data;
        Task task;
        task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                setArrayFrequentadores(((String) DadosFrequentadores.getInstance().getData()).split("'"));

                String[] frequentadores = getArrayFrequentadores();

                HashMap<String, String> hashFrequentadores = new HashMap();
                setFrequentadores(new HashMap<Integer, String>());
                setAdministradores(new HashMap<Integer, String>());
                setMapaIdFotosFrequentadores(new HashMap<Integer, String>());
                int total=0;
                if (frequentadores.length > 0 && !frequentadores[0].isEmpty()) {
                    for (int i = 0; i < frequentadores.length; i++) {
                        // id;matricula;nome;digital;seEhAdminDaEstacao;sexo;foto
                        String[] dados = frequentadores[i].split(";");
                        String id = dados[0];
                        String hashDigital = dados[3];
                        String isAdmin = dados[5];


                        String foto = dados[4];
                        String sexo = dados[6];
                        
                        //matricula, nome, digital
//                        String dadosF = dados[1] + ";" + dados[2] + ";" + dados[4] + ";" + dados[6];// matricula;nome;foto;sexo
                        String dadosF = dados[1] + ";" + dados[2] + ";" + dados[4] + ";" + dados[6]+";"+dados[7]+";";// matricula;nome;foto;sexo;localTrabalho;
                        getFrequentadores().put(Integer.parseInt(id), dadosF);
                        if(isAdmin.equals("true")){
                            getAdministradores().put(Integer.parseInt(id),dadosF);
                        }
                        hashFrequentadores.put(id, hashDigital);
                        getmapaIdFotosFrequentadores().put(Integer.parseInt(id),foto);
                        total = i;
                    }
                }
                System.out.println("Total de frequentadores: "+total);
                // Adiciona os dados ao NBio_SearchIndex
                try {
                    MainController.INSTANCE.getLeitorDigital().addDigitalToIndexSearch(hashFrequentadores);
                } catch (Exception e) {
                    Log.e("Leitor nao iniciado: ");
					Log.e(e);
                }
                return null;
            }
        };

        task.setOnSucceeded(new EventHandler<WorkerStateEvent>(){

            @Override
            public void handle(WorkerStateEvent t) {
                CacheDownloadService downloads = new CacheDownloadService(getmapaIdFotosFrequentadores());
                Thread novo=new Thread(downloads);
                downloads.setOnSucceeded(new EventHandler<WorkerStateEvent>(){

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
        System.out.println("----Fim.");
    }


    /*
 * Recupera o array com as informa誽畫s dos frequentadores
 * @return String[] - array com as informa誽畫s dos frequentadores ("id;matricula;nome;digital;foto")
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
     * Recupera o map com as informa誽畫s dos frequentadores (id,"matricula;nome;foto")
     * @return Map<Integer, String> - map com as informa誽畫s dos frequentadores
     */
    public Map<Integer, String> getFrequentadores() {
        return frequentadores;
    }

    /*
     * Altera o map com as informa誽畫s dos frequentadores - (nome, matricula, digital...)
     * @param Map<Integer, String> - map com as informa誽畫s dos frequentadores
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