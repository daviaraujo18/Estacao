package async;

import core.LeitorDigital;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * Classe que cria um service assincrono(uma nova thread em execucao) no javafx
 * para evitar que a Captura de Digital deixe o sistema travado
 * 
 * Quando chamada, retorna o hash da digital do usuário. Caso não tenha conseguido
 * realizar a leitura, retorna null
 *
 * @author Anderson Soares < aersandersonsoares@gmail.com >
 */
public class CapturarDigitalService extends Service<String> {

	private LeitorDigital ld;
	
	public CapturarDigitalService(LeitorDigital ld) {
		this.ld = ld;
	}
	
	@Override
	protected Task<String> createTask() {
		return new Task<String> () {

			@Override
			protected String call() {
				try {
					System.out.println("Lendo digital!");
					return ld.capturarDigital();
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
					return null;
				}
			}
			
		};
	}
	
}
