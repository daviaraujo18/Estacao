package core;


import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import utils.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class VivoOuMortoService extends Service<Boolean>  {

	String urlString = Configuracoes.base_intranet_url.get() + "/presenca/AdicioneEstacao";

	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() {

				DefaultHttpClient httpClient = new DefaultHttpClient();

				try {

					String codAtivacao = RegistroWindows.getCodigoAtivacaoRegistro();
					String arquivosDeLog = getNameLogs();
					String estacaoEstacao = "FUNCIONANDO";
					String versao = EstacaoPonto.versao;



					HttpPost httpPost = new HttpPost(urlString);

					List<NameValuePair> nvpList = new ArrayList<>();

					nvpList.add(new BasicNameValuePair("codAtivacao", codAtivacao));
					nvpList.add(new BasicNameValuePair("arquivosDeLog", arquivosDeLog));
					nvpList.add(new BasicNameValuePair("estadoEstacao", estacaoEstacao));
					nvpList.add(new BasicNameValuePair("versao", versao));

					httpPost.setEntity(new UrlEncodedFormEntity(nvpList, Charset.forName("UTF-8")));

					HttpResponse response = httpClient.execute(httpPost);

					HttpEntity entity = response.getEntity();

					System.out.println("Request handled?: " + response.getStatusLine());

					return true;

				} catch (IOException e) {
					e.printStackTrace();
					return false;
				} finally {
					httpClient.getConnectionManager().shutdown();
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
