package core;


import javafx.concurrent.Service;
import javafx.concurrent.Task;
import utils.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class PrediosPermitidosService extends Service<String>  {

	String urlString = Configuracoes.base_intranet_url.get() + "/presenca/PrediosPermitidos/";

	@Override
	protected Task<String> createTask() {
		return new Task<String>() {
			@Override
			protected String call() {

				try {

					String codAtivacao = RegistroWindows.getCodigoAtivacaoRegistro();

					System.out.println("CodigoAtivacao: " + codAtivacao);

					String urlParameters = "?codAtivacao=" + codAtivacao;

					urlString = urlString + urlParameters;

					URL url = new URL(urlString);
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					//add reuqest header
					con.setRequestMethod("GET");

					BufferedReader in = new BufferedReader(
							new InputStreamReader(con.getInputStream(), "UTF-8"));
					String inputLine;
					StringBuffer response = new StringBuffer();

					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();

					//print result
					System.out.println("Predios Permitidos: " + response.toString());

					return response.toString();
				} catch (Exception e) {
        			e.printStackTrace();
					return "";
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
