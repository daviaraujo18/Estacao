package core;


import com.sun.corba.se.spi.ior.IORTemplate;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import utils.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class PrediosPermitidosService extends Service<String>  {

	String urlString = Configuracoes.base_intranet_url.get() + "/presenca/PrediosPermitidos/";

	// http://www.tjpi.jus.br/intranet/presenca/PrediosPermitidos/?codAtivacao=qEuuw66sOg6vPDq535tOXA
	@Override
	protected Task<String> createTask() {
		return new Task<String>() {
			@Override
			protected String call() {

				String codAtivacao = RegistroWindows.getCodigoAtivacaoRegistro();

				DefaultHttpClient httpClient = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(urlString);

				List<NameValuePair> nvpList = new ArrayList<>();

				nvpList.add(new BasicNameValuePair("codAtivacao", codAtivacao));

				httpPost.setEntity(new UrlEncodedFormEntity(nvpList, Charset.forName("UTF-8")));

				try {
					HttpResponse response = httpClient.execute(httpPost);

					HttpEntity entity = response.getEntity();

					System.out.println("Request handled?: " + response.getStatusLine());

					InputStream inputStream = entity.getContent();
					String result = IOUtils.toString(inputStream, Charset.forName("UTF-8"));

					System.out.println("PrediosPermitidos: " + result);

					IOUtils.closeQuietly(inputStream);


					return result;

				} catch (IOException e) {
					e.printStackTrace();
					return "";
				} finally {
					httpClient.getConnectionManager().shutdown();
				}
			}
		};
	}
}
