package core;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import utils.Log;
import utils.ScriptsBat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFrequentadoresService extends Service<String> {

    String urlString = Configuracoes.base_intranet_url.get() + "/presenca/DynRecuperarFrequentadores/";

    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                try {

                    URL url = new URL(urlString);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    //add reuqest header
                    con.setRequestMethod("GET");
                    con.setRequestProperty("User-Agent", "JavaFX");

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream(), "UTF-8"));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    String dataFixed = response.toString().replace("\n", "");
                    return dataFixed;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("Erro ao baixar digitais, reiniciando aplicacao...");
                    ScriptsBat.restartAplicacao(true);
                    Platform.exit();
                    System.exit(0);
                    return "";
                }
            }
        };
    }
}
