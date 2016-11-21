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


public class VivoOuMortoService extends Service<Boolean>  {

	String urlString = Configuracoes.base_intranet_url.get() + "/presenca/AdicioneEstacao";

	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() {

				try {

					String codAtivacao = RegistroWindows.getCodigoAtivacaoRegistro();
					String arquivosDeLog = getNameLogs();
					String estacaoEstacao = "FUNCIONANDO";

					URL url = new URL(urlString);
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					//add reuqest header
					con.setRequestMethod("POST");

					// codAtivacao
					// arquivosDeLog
					// estadoEstacao
					String urlParameters = "codAtivacao=" + codAtivacao + "&arquivosDeLog=" + arquivosDeLog + "&estadoEstacao=" + estacaoEstacao;

					// Send post request
					con.setDoOutput(true);
					DataOutputStream wr = new DataOutputStream(con.getOutputStream());
					wr.writeBytes(urlParameters);
					wr.flush();
					wr.close();

					int responseCode = con.getResponseCode();
//					System.out.println("\nSending 'POST' request to URL : " + url);
//					System.out.println("Post parameters : " + urlParameters);
//					System.out.println("Response Code : " + responseCode);

					BufferedReader in = new BufferedReader(
							new InputStreamReader(con.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();

					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();

					//print result
//					System.out.println(response.toString());

					return true;
				} catch (Exception e) {
//        			e.printStackTrace();
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
