package async;

import core.leitura.LeitorDigital;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import utils.Log;

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

    private LeitorDigital leitor;

    public CapturarDigitalService() {
        try {
            leitor = new LeitorDigital();
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

                    while (true) {
                        getLeitor().abrirLeitor();

                        boolean b = getLeitor().temDedo();
                        if(b){
                            try {
                                String leitura = getLeitor().capturarDigital();
                                Thread.sleep(1000L);
                                return leitura;
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                return "";
                            }
                        }
                        getLeitor().fecharLeitor();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return "";
           }
        };
    }

    public LeitorDigital getLeitor() {
        return leitor;
    }
}

