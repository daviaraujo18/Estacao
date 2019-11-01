package utils;

import core.Configuracoes;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

public class ConexaoIntranetService extends Service<Long> {

    public static final Long NAO_CONECTADO = -1L;
    public static final int MAX_TIMEOUT = 10000; // 10seg

    @Override
    protected Task<Long> createTask() {
        return new Task<Long>() {

            @Override
            protected Long call() {
                try {
                    LogAplicacao.i("Checando conexao com Intranet...");
                    String urlString = Configuracoes.base_intranet_url.get() + "/presenca/CarregaRelogioAtual";

                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(MAX_TIMEOUT);
                    conn.setReadTimeout(MAX_TIMEOUT);
                    conn.setRequestMethod("GET");
                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));


                    String line = rd.readLine();
                    rd.close();

                    long horarioEmMillis = Long.parseLong(line);
                    LogAplicacao.i(horarioEmMillis);
                    return horarioEmMillis;
                } catch (SocketTimeoutException e) {
                    LogAplicacao.e("Não foi possível comunicação com Intranet");
                    return NAO_CONECTADO;
                } catch (IOException e) {
                    LogAplicacao.e("Não foi possível comunicação com Intranet");
                    return NAO_CONECTADO;
                }
            }
        };
    }

    public static boolean isConectado() {
        try {
            LogAplicacao.i("Checando conexao com Intranet...");
            String urlString = Configuracoes.base_intranet_url.get() + "/presenca/CarregaRelogioAtual";

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(MAX_TIMEOUT);
            conn.setReadTimeout(MAX_TIMEOUT);
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));


            String line = rd.readLine();
            rd.close();

            long horarioEmMillis = Long.parseLong(line);
            LogAplicacao.i(horarioEmMillis);
            return true;
        } catch (SocketTimeoutException e) {
            LogAplicacao.e("Não foi possível comunicação com Intranet");
            return false;
        } catch (IOException e) {
            LogAplicacao.e("Não foi possível comunicação com Intranet");
            return false;
        }
    }
}
