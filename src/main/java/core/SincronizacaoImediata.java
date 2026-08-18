package core;

import controllers.MainController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static utils.Constantes.HTTP_MAX_TIMEOUT;

/**
 * Mesmo endpoint/formato usado por ValidarBatidaManualService.sincronizar()
 * (POST registros=userId-dd:MM:yyyy:HH:mm:ss&codAtivacao=...), extraído pra
 * ser reutilizado também pelo reconhecimento biométrico — assim a batida por
 * digital chega no servidor de forma imediata e síncrona, igual ao login
 * manual, em vez de esperar o ciclo periódico de sincronização.
 */
public class SincronizacaoImediata {

    /**
     * Momento atual usando o relógio do SERVIDOR (ThreadRelogio), não o
     * relógio físico da máquina Windows onde a Estação está instalada.
     * ThreadRelogio é sincronizado do Rails (Time.current) na carga da
     * página e depois avança por tempo decorrido monotônico
     * (System.nanoTime(), imune a fuso mal configurado e a ajustes manuais
     * no relógio do Windows) — é a mesma fonte que já alimenta o relógio
     * principal da tela (atualizaRelogioLocal). Antes, esse método usava
     * "new Date()" (relógio local da máquina), que ficava sujeito tanto a
     * fuso mal configurado quanto ao relógio da máquina estar fisicamente
     * errado — o texto de confirmação de ponto podia sair com um horário
     * diferente do relógio principal da tela por causa disso.
     */
    public static String momentoAtual() {
        return MainController.INSTANCE.getThreadRelogio().getMomentoAtual();
    }

    public static boolean sincronizar(long userId, String momento, String authenticationMode) throws IOException {
        String registro = userId + "-" + momento;
        String codAtivacao = RegistroWindows.getCodigoAtivacaoRegistro();

        String corpo = "registros=" + URLEncoder.encode(registro, "UTF-8")
                + "&codAtivacao=" + URLEncoder.encode(codAtivacao, "UTF-8")
                + "&authenticationMode=" + URLEncoder.encode(authenticationMode, "UTF-8");

        URL url = new URL(Configuracoes.base_intranet_url.get() + "/presenca/ajax/SincronizarRegistrosPonto");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setConnectTimeout(HTTP_MAX_TIMEOUT);
        con.setReadTimeout(HTTP_MAX_TIMEOUT);
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            os.write(corpo.getBytes(StandardCharsets.UTF_8));
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuffer resposta = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            resposta.append(inputLine);
        }
        in.close();

        return "sincronizado".equals(resposta.toString().trim());
    }
}
