package core;

import controllers.MainController;
import core.leitura.EventoLeitura;
import core.leitura.Leitura;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import utils.CryptoUtils;
import utils.LogEstacao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ValidarBatidaManualService extends Service<Leitura> {

    String urlString = Configuracoes.base_intranet_url.get() + "/presenca/ValidarFrequentador";
    String login;
    String senha;

    public ValidarBatidaManualService(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }

    @Override
    protected Task<Leitura> createTask() {
        return new Task<Leitura>() {
            @Override
            protected Leitura call() throws Exception {
                try {

                    URL url = new URL(urlString+"?loginAccessKey="+CryptoUtils.encryptDES("cryp:gpf", login)+"&plainPassword="+CryptoUtils.encryptDES("cryp:gpf", senha)+"&codAtivacao="+RegistroWindows.getCodigoAtivacaoRegistro());
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
                    if(dataFixed.contains("error")){
                        throw new Exception(dataFixed);
                    }

                    return new Leitura(EventoLeitura.DIGITAL_RECONHECIDA, null, String.valueOf(dataFixed), MainController.INSTANCE.getThreadRelogio().getMomentoAtual());
                } catch (Exception e) {
                    EventoLeitura evento = null;
                    if(e.getMessage().contains("USUARIO_SEM_PERMISSAO_MANUAL")) {
                        evento = EventoLeitura.USUARIO_SEM_PERMISSAO_MANUAL;
                    } else if (e.getMessage().contains("ESTACAO_SEM_PERMISSAO_PARA_BATIDA_MANUAL")) {
                        evento = EventoLeitura.ESTACAO_SEM_PERMISSAO_PARA_BATIDA_MANUAL;
                    } else {
                        evento = EventoLeitura.USUARIO_SENHA_INVALIDOS;
                    }

                    return new Leitura(evento, null, null, MainController.INSTANCE.getThreadRelogio().getMomentoAtual());
                }
            }
        };
    }
}
