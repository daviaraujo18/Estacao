package utils;

import core.Configuracoes;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import sun.security.validator.ValidatorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import static utils.Constantes.HTTP_MAX_TIMEOUT;

public class ConexaoIntranetService extends Service<Long> {

    public static final Long NAO_CONECTADO = -1L;

    @Override
    protected Task<Long> createTask() {
        return new Task<Long>() {

            @Override
            protected Long call() {
                try {

                    return horarioIntranetInMillis();

                } catch (SocketTimeoutException e) {
                    LogAplicacao.e("Não foi possível comunicação com Intranet - TIMEOUT");
                    return NAO_CONECTADO;
                } catch (Exception e) {
                    LogAplicacao.e(e.getMessage());
                    LogAplicacao.e("Não foi possível comunicação com Intranet");
                    return NAO_CONECTADO;
                }
            }
        };
    }

    private static Long horarioIntranetInMillis() throws Exception {
        LogAplicacao.i("Checando conexao com Intranet...");
        String urlString = Configuracoes.base_intranet_url.get() + "/presenca/CarregaRelogioAtual";

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(HTTP_MAX_TIMEOUT);
        conn.setReadTimeout(HTTP_MAX_TIMEOUT);
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));


        String line = rd.readLine();
        rd.close();

        long horarioEmMillis = Long.parseLong(line);
        LogAplicacao.i(horarioEmMillis);
        return horarioEmMillis;
    }

    public static boolean isConectado() throws IOException {
        try {

            horarioIntranetInMillis();

            return true;
        } catch(SocketTimeoutException e) {
            LogAplicacao.e("Não foi possível comunicação com Intranet - TIMEOUT");
            return false;
        } catch (ValidatorException e) {
            e.printStackTrace();
            LogAplicacao.e("ERRO: REINICIANDO APLICAÇÃO!");
            ScriptsBat.restartAplicacao(true);
            Platform.exit();
            System.exit(0);
            return false;
        } catch (Exception e) {
            LogAplicacao.e("Não foi possível comunicação com Intranet");
            return false;
        }
    }
}
