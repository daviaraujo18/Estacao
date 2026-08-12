package async;

import core.leitura.LeitorDigital;
import core.leitura.Operacao;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import utils.LogAplicacao;

/**
 * Classe que cria um service assincrono(uma nova thread em execucao) no javafx
 * para evitar que a Captura de Digital deixe o sistema travado
 *
 * Quando chamada, retorna o hash da digital do usu�rio. Caso n�o tenha
 * conseguido realizar a leitura, retorna null
 *
 */
public class PreProcessandoService extends Service<PreProcessandoService.Result> implements Runnable{

    private boolean parar = false;
    public boolean clickDesbloqueioTela;
    public boolean loginManual = false;

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
            LogAplicacao.i("Loop de captura pronto (leitor aberto), aguardando dedo...");
            while (!parar) {
                if(loginManual){
                    getLeitor().fecharLeitor();
                    return new Result(Operacao.LOGINMANUAL, "");
                }
                if(clickDesbloqueioTela){
                    String digital = getLeitor().capturarDigital_popup();
                    clickDesbloqueioTela = false;
                    return new Result(Operacao.DESBLOQUEIO, digital);
                }
                // NOTE: antes fazia temDedo() (CheckFinger) primeiro e só
                // chamava capturarDigital() quando retornasse true — mas
                // CheckFinger() se mostrou pouco confiável sob passthrough
                // USB (nunca detectava o dedo, mesmo com o dedo encostado),
                // enquanto Capture() (usado aqui e também por Enroll(), que
                // sempre funcionou) espera o dedo internamente sem precisar
                // desse pré-check. Chamando direto, do mesmo jeito que o
                // cadastro de digital já fazia com sucesso.
                String digital = getLeitor().capturarDigital();
                if (digital != null && !digital.isEmpty()) {
                    return new Result(Operacao.REGISTRO_FREQUENCIA, digital);
                }
            }
        } catch (Exception e) {
            java.io.StringWriter sw = new java.io.StringWriter();
            e.printStackTrace(new java.io.PrintWriter(sw));
            LogAplicacao.e("DIAG exceção no loop de captura: " + e.getClass().getName() + " - " + e.getMessage() + "\n" + sw.toString());
            PreProcessandoService.this.restart();
        }
        return null;
    }

    @Override
    public void run() {
        this.getResult();
    }

    public LeitorDigital getLeitor() {
        return LeitorDigital.getInstance();
    }

    public void parar(boolean b) {
        this.parar  = b;
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
}
