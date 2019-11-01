package core;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import utils.ArquivoUtils;
import utils.LogAplicacao;
import utils.ScriptsBat;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFrequentadoresService extends Service<String> {

    String dynFrequentadoresEstacaoUrl = Configuracoes.base_intranet_url.get() + "/presenca/DynFrequentadoresEstacao/";
    String dynHashFrequentadoresEstacao = Configuracoes.base_intranet_url.get() + "/presenca/DynHashFrequentadoresEstacao/";


    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                try {
                    if (temNovasDigitais()) {
                        return downloadDigitais();
                    } else {
                        /*
                         Em DadosFrequentadores
                         se digitais == null pegar do data.db
                          */
                        return null;
                    }
                } catch (Exception e) {
                    LogAplicacao.e(e.getMessage());
                    LogAplicacao.e("Erro ao baixar digitais, reiniciando aplicacao...");

                    ScriptsBat.restartAplicacao(true);
                    Platform.exit();
                    System.exit(0);
                    return "";
                }
            }

            private boolean temNovasDigitais() throws Exception {

                LogAplicacao.i("Verificando se existem novas digitais");
                URL url = new URL(dynHashFrequentadoresEstacao);
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

                String hashDigitaisIntranet = response.toString().replace("\n", "");
                LogAplicacao.i("HASH INTRANET: \t"+hashDigitaisIntranet);
                if (hashDigitaisIntranet.isEmpty() || hashDigitaisIntranet.contains("EXCEPTION_MESSAGE")) {
                    throw new Exception(hashDigitaisIntranet);
//                    return true;
                }

                File arquivoHash = new File(LocalPaths.PATH_DATA,"hash");
                if (!arquivoHash.exists() && !arquivoHash.isDirectory()) {
                    ArquivoUtils.saveFile("hash", hashDigitaisIntranet);
                    return true;
                } else {

                    String myHash = ArquivoUtils.readFile("hash");
                    LogAplicacao.i("HASH LOCAL: \t\t" + myHash);
                    if (!myHash.equals(hashDigitaisIntranet)) {
                        ArquivoUtils.saveFile("hash", hashDigitaisIntranet);
                        LogAplicacao.i("Atualizando banco de digitais");
                        return true;
                    }
                    LogAplicacao.i("Não é necessário atualizar as digitais");
                    return false;
                }

            }

            private String downloadDigitais() throws Exception {
                URL url = new URL(dynFrequentadoresEstacaoUrl);
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

                String dadosDigitais = response.toString().replace("\n", "");
                if (dadosDigitais.isEmpty() || dadosDigitais.contains("EXCEPTION_MESSAGE")) {
                    throw new Exception(dadosDigitais);
                }

                LogAplicacao.i("Download finalizado");

                return dadosDigitais;
            }

        };
    }
}
