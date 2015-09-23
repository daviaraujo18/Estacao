package utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 *
 * @author Jainilene
 */
public class VerificaConexao {

	public static boolean verificaConexao(String sUrl) {

		try {

			URL url = new URL(sUrl);
			Log.i("URL acessada: "+url.toString());
					// abre a conexão
					HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();

			urlConnect.setConnectTimeout(5000);
			urlConnect.setReadTimeout(5000);
               // tenta buscar conteúdo da URL
			// se não tiver conexão, essa linha irá falhar
			Object objData = urlConnect.getContent();
		}
		catch (MalformedURLException e) {
			Log.e("url:"+sUrl+" >>"+e.getMessage());
			return false;
		}
		catch (IOException e) {
			Log.e(e.getMessage());
			return false;
		}
		return true;
	}

}
