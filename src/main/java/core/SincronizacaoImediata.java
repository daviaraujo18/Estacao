package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static utils.Constantes.HTTP_MAX_TIMEOUT;

/**
 * Mesmo endpoint/formato usado por ValidarBatidaManualService.sincronizar()
 * (POST registros=userId-dd:MM:yyyy:HH:mm:ss&codAtivacao=...), extraído pra
 * ser reutilizado também pelo reconhecimento biométrico — assim a batida por
 * digital chega no servidor de forma imediata e síncrona, igual ao login
 * manual, em vez de esperar o ciclo periódico de sincronização.
 */
public class SincronizacaoImediata {

    private static final TimeZone FUSO_BRASILIA = TimeZone.getTimeZone("America/Sao_Paulo");

    /**
     * Momento atual formatado no fuso fixo America/Sao_Paulo (mesmo fuso
     * configurado no Rails, config.time_zone). NÃO usar
     * "new SimpleDateFormat(...).format(new Date())" sem fuso explícito —
     * isso herda o fuso padrão do Windows onde a Estação está instalada, e
     * se essa máquina não estiver configurada corretamente pra Brasília
     * (comum em instalação nova, que geralmente vem em UTC), o horário da
     * batida sai errado mesmo com o relógio da tela mostrando a hora
     * certa — o relógio da tela vem de outro caminho (ThreadRelogio,
     * campos copiados diretamente do servidor, imune a essa configuração).
     */
    public static String momentoAtual() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy:HH:mm:ss");
        sdf.setTimeZone(FUSO_BRASILIA);
        return sdf.format(new Date());
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
