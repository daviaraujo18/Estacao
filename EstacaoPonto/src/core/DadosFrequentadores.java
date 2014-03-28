package core;


import controllers.MainController;
import java.util.HashMap;
import java.util.Map;
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

    public static DadosFrequentadores getInstance(){
        if(INSTANCE == null){
            INSTANCE = new DadosFrequentadores();
        }
        return INSTANCE;
    }
    
    private DadosFrequentadores(){

    }

    public void init(String data){

            this.setArrayFrequentadores(((String) data).split("'"));
            String[] frequentadores = this.getArrayFrequentadores();

            HashMap<String, String> hashFrequentadores = new HashMap();
            this.setFrequentadores(new HashMap<Integer, String>());
            this.setAdministradores(new HashMap<Integer, String>());
            this.setMapaIdFotosFrequentadores(new HashMap<Integer, String>());
            int total=0;
            if (frequentadores.length > 0 && !frequentadores[0].isEmpty()) {
                for (int i = 0; i < frequentadores.length; i++) {
                    // id;matricula;nome;digital;foto
                    String[] dados = frequentadores[i].split(";");
                    String id = dados[0];
                    String hashDigital = dados[3];
                    String isAdmin = dados[5];


                    String foto = dados[4];
                    String sexo = dados[6];

                    //matricula, nome, digital
                    String dadosF = dados[1] + ";" + dados[2] + ";" + dados[4] + ";" + dados[6];// matricula;nome;foto;sexo
                    this.getFrequentadores().put(Integer.parseInt(id), dadosF);
                    if(isAdmin.equals("true")){
                        this.getAdministradores().put(Integer.parseInt(id),dadosF);
                    }
                    hashFrequentadores.put(id, hashDigital);
                    this.getmapaIdFotosFrequentadores().put(Integer.parseInt(id),foto);
                    total = i;
                }
            }

        System.out.println("Total de frequentadores: "+total);
        // Adiciona os dados ao NBio_SearchIndex
        try {
            MainController.INSTANCE.getLeitorDigital().addDigitalToIndexSearch(hashFrequentadores);
        } catch (Exception e) {
            Log.i("Leitor nao iniciado: " + e.getMessage());
        }
        CacheDownloadService downloads = new CacheDownloadService(this.getmapaIdFotosFrequentadores());
        Thread novo=new Thread(downloads);
        
        downloads.setOnSucceeded(new EventHandler<WorkerStateEvent>(){

            @Override
            public void handle(WorkerStateEvent t) {
                 The.inserirJavascript(TelaPonto.INSTANCE.getWebEngine(), "aguardarDigital();");
                if(MainController.INSTANCE.getCds().isRunning())
                {
                    MainController.INSTANCE.getCds().setUsarLeitor(false);
                }
                else
                {
                    MainController.INSTANCE.getCds().start();
                }
               TelaPonto.INSTANCE.getSplitPanel().getDividers().get(1).setPosition(0.999);   
                TelaPonto.INSTANCE.getBotaoCadastrarDigital().setVisible(false);
                TelaPonto.INSTANCE.getBotaoAtualizarDigital().setVisible(false);
                TelaPonto.INSTANCE.getProgressBar().setVisible(false);
            }
            
        });
        novo.start();
        TelaPonto.INSTANCE.getProgressBar().progressProperty().bind(downloads.progressProperty());
        
        

        System.out.println("----Fim.");
    }


    /*
 * Recupera o array com as informações dos frequentadores
 * @return String[] - array com as informações dos frequentadores ("id;matricula;nome;digital;foto")
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
     * Recupera o map com as informações dos frequentadores (id,"matricula;nome;foto")
     * @return Map<Integer, String> - map com as informações dos frequentadores
     */
    public Map<Integer, String> getFrequentadores() {
        return frequentadores;
    }

    /*
     * Altera o map com as informações dos frequentadores - (nome, matricula, digital...)
     * @param Map<Integer, String> - map com as informações dos frequentadores
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

}