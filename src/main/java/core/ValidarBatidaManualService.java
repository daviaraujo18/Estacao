package core;

import controllers.MainController;
import core.leitura.EventoLeitura;
import core.leitura.Leitura;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import utils.CryptoUtils;
import utils.LogAplicacao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import static utils.Constantes.HTTP_MAX_TIMEOUT;

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
                String momento = MainController.INSTANCE.getThreadRelogio().getMomentoAtual();
                try {
                    URL url = new URL(urlString + "?loginAccessKey=" + CryptoUtils.encryptDES("cryp:gpf", login)
                            + "&plainPassword=" + CryptoUtils.encryptDES("cryp:gpf", senha)
                            + "&codAtivacao=" + RegistroWindows.getCodigoAtivacaoRegistro());
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("User-Agent", "JavaFX");
                    con.setConnectTimeout(HTTP_MAX_TIMEOUT);
                    con.setReadTimeout(HTTP_MAX_TIMEOUT);

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream(), "UTF-8"));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    String dataFixed = response.toString().replace("\n", "").trim();

                    Long userId = null;
                    try {
                        userId = Long.parseLong(dataFixed);
                    } catch (NumberFormatException nfe) {
                        // Resposta não numérica -> credenciais inválidas ou estação/usuário sem permissão
                    }

                    if (userId == null) {
                        EventoLeitura evento;
                        if (dataFixed.contains("USUARIO_SEM_PERMISSAO_MANUAL")) {
                            evento = EventoLeitura.USUARIO_SEM_PERMISSAO_MANUAL;
                        } else if (dataFixed.contains("ESTACAO_SEM_PERMISSAO_PARA_BATIDA_MANUAL")) {
                            evento = EventoLeitura.ESTACAO_SEM_PERMISSAO_PARA_BATIDA_MANUAL;
                        } else {
                            evento = EventoLeitura.USUARIO_SENHA_INVALIDOS;
                        }
                        return new Leitura(evento, null, null, momento);
                    }

                    // Autenticado: sincroniza igual ao botão "Simular digital"
                    // (mesmo endpoint, mesmo formato, mesmos critérios de
                    // entrada/saída via PunchTypeService) — só muda o
                    // authenticationMode=manual.
                    if (!sincronizar(userId)) {
                        return new Leitura(EventoLeitura.USUARIO_SENHA_INVALIDOS, null, null, momento);
                    }

                    return new Leitura(EventoLeitura.LOGIN_MANUAL_SUCESSO, null, String.valueOf(userId), momento);

                } catch (SocketTimeoutException e) {
                    return new Leitura(EventoLeitura.SEM_CONEXAO_TIMEOUT, null, null, momento);
                } catch (Exception e) {
                    LogAplicacao.e(e);
                    return new Leitura(EventoLeitura.USUARIO_SENHA_INVALIDOS, null, null, momento);
                }
            }

            /**
             * Mesmo formato/endpoint usado por simularDigitalTeste() no JS:
             * POST registros=userId-dd:MM:yyyy:HH:mm:ss&codAtivacao=...,
             * resposta em texto puro "sincronizado" (sem confirmacaoVisual).
             * Reaproveitado por SincronizacaoImediata, também usado pelo
             * reconhecimento biométrico.
             */
            private boolean sincronizar(long userId) throws IOException {
                return SincronizacaoImediata.sincronizar(userId, SincronizacaoImediata.momentoAtual(), "manual");
            }
        };
    }
}
