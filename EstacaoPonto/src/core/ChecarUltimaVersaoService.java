package core;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;


public class ChecarUltimaVersaoService extends Service<String>  {

	String urlString = Configuracoes.base_intranet_url.get() + "/presenca/AtualizarEstacoes/";

	@Override
	protected Task<String> createTask() {
		return new Task<String>() {
			@Override
			protected String call() {
				try {

					URL url = new URL(urlString);
					BufferedReader rd = new BufferedReader(new InputStreamReader(url.openStream()));
					String result = rd.readLine();
					rd.close();

					return result;

				} catch (Exception e) {
//        			e.printStackTrace();
					return EstacaoPonto.versao;
				}
			}
		};
	}
}
