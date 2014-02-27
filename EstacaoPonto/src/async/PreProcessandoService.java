package async;

import core.leitura.LeitorDigital;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import utils.Log;

/**
 * Classe que cria um service assincrono(uma nova thread em execucao) no javafx
 * para evitar que a Captura de Digital deixe o sistema travado
 *
 * Quando chamada, retorna o hash da digital do usuário. Caso não tenha
 * conseguido realizar a leitura, retorna null
 *
 * @author Anderson Soares < aersandersonsoares@gmail.com >
 */
public class PreProcessandoService extends Service<String> {

    private LeitorDigital leitor;
    private boolean usarLeitor = false;

    public PreProcessandoService() {
        try {
            leitor = LeitorDigital.getInstance();
        } catch (Exception e) {
            Log.i("Leitor digital nao iniciado: " + e.getMessage());
        }
    }

    @Override
    protected Task<String> createTask() {
        return new Task<String>() {

            @Override
            protected String call() {
                try {
                    long anterior = System.currentTimeMillis();
                    getLeitor().abrirLeitor();
                    while (true) {
                        if(!usarLeitor) {
                            long zero = System.currentTimeMillis();
                            boolean b = getLeitor().temDedo();
                            if (b) {
                                LeitorDigital leitor = LeitorDigital.getInstance();// new LeitorDigital();
                                String leitura  = leitor.capturarDigital();
                                return leitura;
                            }
                          }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }
        };
    }

    public LeitorDigital getLeitor() {
        return LeitorDigital.getInstance();
    }
    public void setUsarLeitor(boolean usarLeitor) {
        this.usarLeitor = usarLeitor;
    }
}
