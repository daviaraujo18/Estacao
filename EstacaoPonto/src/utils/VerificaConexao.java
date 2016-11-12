package utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

			urlConnect.setConnectTimeout(10000);
			urlConnect.setReadTimeout(10000);
               // tenta buscar conteúdo da URL
			// se não tiver conexão, essa linha irá falhar
			Object objData = urlConnect.getContent();
		}
		catch (MalformedURLException e) {
//			Log.e(e);
			Log.e("VerificarConexao falhou, url mal formada");
			return false;
		}
		catch (IOException e) {
//			Log.e(e);
			Log.e("VerificarConexao falhou, intranet fora do ar? ou Eu estou fora do ar?");
			return false;
		}
		return true;
	}

}
