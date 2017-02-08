/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.util.Iterator;
import java.util.Map;
import javafx.concurrent.Task;
import utils.CacheManipulation;
import utils.Log;

/**
 *
 * @author Daniel Leite TJPI
 */
public class CacheDownloadService extends Task<Void> {

	Map<Integer, String> mapaIdFotosFrequentadores;

	CacheDownloadService(Map<Integer, String> mapaIdFotosFrequentadores) {
		this.mapaIdFotosFrequentadores = mapaIdFotosFrequentadores;
	}

	private void downloadAndCacheFotos() {
		if (Configuracoes.baixa_foto.getBooleanValue()) {
			int numTotal = mapaIdFotosFrequentadores.size();
			Iterator it = mapaIdFotosFrequentadores.entrySet().iterator();
			Log.i("Iniciando download das fotos...");
			int numAtual = 1;
			int progress = 0;

			String message;
			while (it.hasNext()) {
				progress = (int) (((double) numAtual) / ((double) numTotal) * 100);
				message = "Download fotos: " + progress + "%";
				updateMessage(message);
				Map.Entry pairs = (Map.Entry) it.next();
				String enderecoWeb = pairs.getValue().toString();
//            System.out.println("Endereço Web: "+enderecoWeb);
//            System.out.print("Baixando "+numAtual+" de "+numTotal+". ");

				updateProgress(numAtual, numTotal);

				if (!CacheManipulation.searchAndEdit(enderecoWeb)) {
//					CacheManipulation.insert(enderecoWeb);
				}

				it.remove(); // avoids a ConcurrentModificationException
				numAtual++;

			}
		}
	}

	@Override
	protected Void call() throws Exception {
		downloadAndCacheFotos();

		return null;
	}

}
