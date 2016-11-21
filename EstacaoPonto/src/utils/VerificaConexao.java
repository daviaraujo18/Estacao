package utils;

import core.Configuracoes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Jainilene
 */
public class VerificaConexao {

	public static long verificaConexao()  {

		try {
			String urlString = Configuracoes.base_intranet_url.get() + "/presenca/CarregaRelogioAtual";

			StringBuilder result = new StringBuilder();
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String line = rd.readLine();
			rd.close();

//			System.out.println(line);
			long horarioEmMillis = Long.parseLong(line);

			return horarioEmMillis;
		} catch (Exception e) {
//			e.printStackTrace();
			return -1;
		}
	}

}
