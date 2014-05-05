package async;

import core.leitura.LeitorDigital;
import core.leitura.Operacao;
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
public class PreProcessandoService extends Service<PreProcessandoService.Result> implements Runnable{

    private boolean parar = false;
    public boolean clickDesbloqueioTela;

    @Override
    protected Task<Result> createTask() {
        return new Task<Result>() {
            @Override
            protected Result call() {
                return getResult();
            }
        };
    }

    private Result getResult() {
        try {
            getLeitor().abrirLeitor();
            while (!parar) {
                boolean b = getLeitor().temDedo();
                if (b && !clickDesbloqueioTela) {
                    String digital =  getLeitor().capturarDigital();
                    return new Result(Operacao.REGISTRO_FREQUENCIA, digital);
                }
                if(clickDesbloqueioTela){
                    String digital = getLeitor().capturarDigital_popup();
                    clickDesbloqueioTela = false;
                    return new Result(Operacao.DESBLOQUEIO, digital);
                }
            }
        } catch (Exception e) {
            PreProcessandoService.this.restart();
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void run() {
        this.getResult();
    }


    public class Result{
        Operacao operacao;
        String digital;

        public Result(Operacao op, String dig){
            this.digital = dig;
            this.operacao = op;
        }

        public void process() {
            this.operacao.execute(digital);
        }
    }

    public LeitorDigital getLeitor() {
        return LeitorDigital.getInstance();
    }

    public void parar(boolean b) {
        this.parar  = b;
    }

}
