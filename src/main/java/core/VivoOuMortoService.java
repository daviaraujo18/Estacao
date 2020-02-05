package core;


import javafx.concurrent.Service;
import javafx.concurrent.Task;
import utils.LogAplicacao;
import utils.LogEstacao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static utils.Constantes.HTTP_MAX_TIMEOUT;


public class VivoOuMortoService extends Service<Boolean>  {

	String urlString = Configuracoes.base_intranet_url.get() + "/presenca/AdicioneEstacao";

	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() {

				String codAtivacao = RegistroWindows.getCodigoAtivacaoRegistro();
				String versao = EstacaoPonto.versao;
				String estadoEstacao = "FUNCIONANDO";

				try {

					String codAtivacaoEncoded = URLEncoder.encode(codAtivacao,  java.nio.charset.StandardCharsets.UTF_8.toString());
					String estadoEstacaoEncoded = URLEncoder.encode(estadoEstacao,  java.nio.charset.StandardCharsets.UTF_8.toString());
					String versaoEncoded = URLEncoder.encode(versao,  java.nio.charset.StandardCharsets.UTF_8.toString());

					String urlParameters = "?codAtivacao=" + codAtivacaoEncoded + "&versao=" + versaoEncoded + "&estadoEstacao=" + estadoEstacaoEncoded;

					urlString = urlString + urlParameters;

					URL url = new URL(urlString);
					HttpURLConnection con = (HttpURLConnection) url.openConnection();

					con.setConnectTimeout(HTTP_MAX_TIMEOUT);
					con.setReadTimeout(HTTP_MAX_TIMEOUT);
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
					LogAplicacao.i(response);
					in.close();

					return true;
				} catch(SocketTimeoutException e) {
					LogEstacao.e(e.getMessage());
					LogEstacao.e("VivoOuMorto não conseguiu conexão - TIMEOUT");
					return false;
				} catch (Exception e) {
					LogEstacao.e(e.getMessage());
					LogEstacao.e("VivoOuMorto não conseguiu conexão");
					return false;
				}

			}
		};
	}

}
