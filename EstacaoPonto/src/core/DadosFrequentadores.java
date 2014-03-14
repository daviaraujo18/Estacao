package core;

import async.DownloadFotos;
import com.sun.deploy.net.HttpResponse;
import controllers.MainController;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.client.methods.HttpGet;
import utils.Log;

/**
 * Created by Danilo on 10/02/14.
 */
public class DadosFrequentadores {

    private static DadosFrequentadores INSTANCE;

    private String[] arrayFrequentadores;
    private Map<Integer,String> mapaIdInfoFrequentadores;
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

        HashMap<String, String> mapaIdHashFrequentadores = new HashMap<String, String>();
        this.setMapaIdInfoFrequentadores(new HashMap<Integer, String>());
        this.setMapaIdFotosFrequentadores(new HashMap<Integer, String>());
        int total=0;
        if (frequentadores.length > 0 && !frequentadores[0].isEmpty()) {
            for (int i = 0; i < frequentadores.length; i++) {
                // id;matricula;nome;digital;foto
                String[] dados = frequentadores[i].split(";");
                String id = dados[0];
                String hashDigital = dados[3];
                
                int matricula = Integer.parseInt(dados[1]);
                String foto = dados[4];

                //matricula, nome, digital
                String dadosF = dados[1] + ";" + dados[2] + ";" + dados[4];// matricula;nome;foto

                this.getMapaIdInfoFrequentadores().put(Integer.parseInt(id), dadosF);
                this.getmapaIdFotosFrequentadores().put(matricula,foto);
                mapaIdHashFrequentadores.put(id, hashDigital);
                total = i;
            }
        }
        System.out.println("Total de frequentadores: "+total);
        // Adiciona os dados ao NBio_SearchIndex
        try {
            MainController.INSTANCE.getLeitorDigital().addDigitalToIndexSearch(mapaIdHashFrequentadores);
        } catch (Exception e) {
            Log.i("Leitor nao iniciado: " + e.getMessage());
        }
        
        String endereco = this.getmapaIdFotosFrequentadores().get(3690);
        System.out.println("Matrícula: 3690 Endereco: "+endereco);
        
       DownloadFotos servico = new DownloadFotos();
       
       servico.baixaFotos(endereco);
        
        
        
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
    public Map<Integer, String> getMapaIdInfoFrequentadores() {
        return mapaIdInfoFrequentadores;
    }

    /*
     * Altera o map com as informações dos frequentadores - (nome, matricula, digital...)
     * @param Map<Integer, String> - map com as informações dos frequentadores
     */
    public void setMapaIdInfoFrequentadores(Map<Integer, String> mapaIdInfoFrequentadores) {
        this.mapaIdInfoFrequentadores = mapaIdInfoFrequentadores;
    }

    private Map<Integer, String> getmapaIdFotosFrequentadores() {
        return mapaIdFotosFrequentadores;
    }

    private void setMapaIdFotosFrequentadores(Map<Integer, String> mapaIdFotosFrequentadores) {
        this.mapaIdFotosFrequentadores = mapaIdFotosFrequentadores;
    }


}
