package core;


import javafx.concurrent.Service;
import javafx.concurrent.Task;
import utils.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class VivoOuMortoService extends Service<Boolean>  {

	String urlString = Configuracoes.base_intranet_url.get() + "/presenca/AdicioneEstacao";

	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() {

				String codAtivacao = RegistroWindows.getCodigoAtivacaoRegistro();
				String versao = EstacaoPonto.versao;
				String arquivosDeLog = getNameLogs();
				String estadoEstacao = "FUNCIONANDO";

				try {

					String codAtivacaoEncoded = URLEncoder.encode(codAtivacao,  java.nio.charset.StandardCharsets.UTF_8.toString());
					String arquivosDeLogEncoded = URLEncoder.encode(arquivosDeLog,  java.nio.charset.StandardCharsets.UTF_8.toString());
					String estadoEstacaoEncoded = URLEncoder.encode(estadoEstacao,  java.nio.charset.StandardCharsets.UTF_8.toString());
					String versaoEncoded = URLEncoder.encode(versao,  java.nio.charset.StandardCharsets.UTF_8.toString());

					String urlParameters = "?codAtivacao=" + codAtivacaoEncoded + "&versao=" + versaoEncoded + "&estadoEstacao=" + estadoEstacaoEncoded + "&arquivosDeLog=" + arquivosDeLogEncoded;
					System.out.println("URLParameters: " + urlParameters);

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

					System.out.println("VivoOuMorto -> " + response.toString());

					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}

			}
		};
	}

	private String getNameLogs()
	{
		String logsNames="";
		File folder = new File(LocalPaths.PATH_LOG);

		if (folder.exists())
		{
			File[] listFiles = folder.listFiles();
			for (File file:listFiles)
			{
				if (file.getName().startsWith(Log.LOG_NAME_BEGIN))
				{
					logsNames= file.getName()+" / "+logsNames;
				}
			}
		}
		return logsNames;
	}
}
