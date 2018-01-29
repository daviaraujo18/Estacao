package core;


import javafx.concurrent.Service;
import javafx.concurrent.Task;
import listeners.Operacao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class PrediosPermitidosService extends Service<String>  {

	String urlString = Configuracoes.base_intranet_url.get() + "/presenca/PrediosPermitidos/";

	// http://www.tjpi.jus.br/intranet/presenca/PrediosPermitidos/?codAtivacao=qEuuw66sOg6vPDq535tOXA
	@Override
	protected Task<String> createTask() {
		return new Task<String>() {
			@Override
			protected String call() {

				String codAtivacao = RegistroWindows.getCodigoAtivacaoRegistro();
				try {

					String urlParameters = "?codAtivacao=" + codAtivacao;

					urlString = urlString + urlParameters;

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

					String prediosPermitidos = response.toString();

					return prediosPermitidos;
				} catch (Exception e) {
					e.printStackTrace();
					return "";
				}
			}
		};
	}
}
