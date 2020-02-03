package listeners;

import controllers.MainController;
import core.IntranetURLs;
import core.LocalPaths;
import core.RegistroWindows;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import utils.LogAplicacao;
import utils.ScriptsBat;
import view.TelaPonto;

/**
 * Classe que verifica toda vez que ocorre uma mudanca de pagina e faz as
 * devidas modificacoes dependendo da url atual
 */
public class ChangeUrlListener implements ChangeListener<Object> {

    private TelaPonto tela;

    public ChangeUrlListener(TelaPonto tela) {
        this.tela = tela;
    }

    @Override
    public void changed(ObservableValue<? extends Object> ov, Object t, Object t1) {

        if (ov.getValue().equals(Worker.State.SCHEDULED)) {
//            LogAplicacao.i("Carregando pagina: " + tela.getWebEngine().getLocation());
            if (urlAtualContem("Frequentador?type=create")) {
                //if (urlAtualContem("presenca/Frequentador")) {
                tela.getSplitPanel().getDividers().get(1).setPosition(0.5);
                tela.getBotaoCadastrarDigital().setVisible(true);
                tela.getBotaoAtualizarDigital().setVisible(false);
            }
            else if(urlAtualContem("Frequentador?type=update")){
                tela.getSplitPanel().getDividers().get(1).setPosition(0.5);
                tela.getBotaoCadastrarDigital().setVisible(false);
                tela.getBotaoAtualizarDigital().setVisible(true);
            }else {
                tela.getSplitPanel().getDividers().get(1).setPosition(0.999);
            }
        } else {

            if (ov.getValue().equals(Worker.State.SUCCEEDED)) {


                //Document doc = tela.getWebEngine().getDocument();
                //Element el = doc.getElementById("relogio");


//                LogAplicacao.i("Pagina carregada: " + tela.getWebEngine().getLocation());
                if (urlAtualContem("EstacaoPonto?type=create")) {
                    LogAplicacao.i("Injetando codigos no formulario via JavaScript");
                    boolean ret = false;
                    try {
                        setarInputCodigos();
//                        ret = criarArquivoBatimentos();
                    } catch (IOException ex) {
                        LogAplicacao.e(ex);
                        Logger.getLogger(ChangeUrlListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (urlAtualContem("presenca/IniciarPonto")) {
//                    LogAplicacao.i("Entrei no metodo");
                    String codigos = null;
                    try {
                        codigos = IntranetURLs.getCodigos();
                        String url = IntranetURLs.INICIALIZAR_PONTO + codigos;
                        mudarUrlAtualPara(url);
                        MainController.INSTANCE.getCds().parar(false);
                    } catch (IOException e) {
                        LogAplicacao.e("Nao foi possivel recuperar codigos");
                        e.printStackTrace();
                    }

                }
                else
                {
                    if(urlAtualContem("exception.jsp")||urlAtualContem("500.jsp")||urlAtualContem("404.jsp"))
                    {
                        //EstacaoPonto.getInstance().setTitle("econtrado");
                        //((EventTarget) el).addEventListener("click", tela.listener, false);
                        LogAplicacao.i("Shutdown.  D: ");
                        //mudarUrlAtualPara(IntranetURLs.BATIMENTO_PONTO);
                        try
                        {

                            try {

                                LogAplicacao.i("Tentando executar: "+LocalPaths.APP_DIR+ScriptsBat.restartFileName);
                                ScriptsBat.restartAplicacao();
                            }
                            catch (Exception ex) {
                                Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                                LogAplicacao.i("Problema ao tentar restartar a aplicação");
                                LogAplicacao.e(ex);
                            }
//                            System.out.println("Iniciou");

                            Platform.exit();
                            System.exit(0);
                        }
                        catch (Exception ex)
                        {
                            Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                            LogAplicacao.e(ex);
                        }
                    }
                }
            }
        }
    }

    private void setarInputCodigos() throws IOException {
        WebEngine webEngine = tela.getWebEngine();

        String codigoAtivacao = RegistroWindows.gerarCodigoAtivacao();
        String codigoUnicoMaquina = RegistroWindows.getCodigoUnicoMaquina();
        webEngine.executeScript(""
                + "jQuery('#codigoUnicoMaquina').val('" + codigoUnicoMaquina + "');"
                + "jQuery('#codigoAtivacao').val('" + codigoAtivacao + "');");
    }

    private boolean urlAtualContem(String texto) {
        return tela.getWebEngine().getLocation().contains(texto);
    }

    private void mudarUrlAtualPara(String novaURL) {
        tela.getWebEngine().load(novaURL);
    }

}