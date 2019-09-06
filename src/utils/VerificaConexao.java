package utils;

import core.Configuracoes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author Jainilene
 */
public class VerificaConexao {

	public static long verificaConexao()  {

		try {
			String urlString = Configuracoes.base_intranet_url.get() + "/presenca/CarregaRelogioAtual";

			LogAplicacao.i("Tentando conectar com : "+urlString);
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String line = rd.readLine();
			rd.close();

			long horarioEmMillis = Long.parseLong(line);

			return horarioEmMillis;
		} catch (Exception e) {
//			e.printStackTrace();
			LogAplicacao.e(e);
			return -1;
		}
	}

}
