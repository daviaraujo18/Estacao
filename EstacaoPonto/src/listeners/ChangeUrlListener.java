package listeners;

import controllers.MainController;
import core.IntranetURLs;
import core.RegistroWindows;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import utils.Log;
import view.TelaPonto;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe que verifica toda vez que ocorre uma mudança de página e faz as
 * devidas modificaçoes dependendo da url atual
 *
 * @author Anderson Soares
 */
public class ChangeUrlListener implements ChangeListener<Object> {

    private TelaPonto tela;

    public ChangeUrlListener(TelaPonto tela) {
        this.tela = tela;
    }

    @Override
    public void changed(ObservableValue<? extends Object> ov, Object t, Object t1) {

        if (ov.getValue().equals(Worker.State.SCHEDULED)) {
            Log.i("Carregando pagina: " + tela.getWebEngine().getLocation());
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
      
                Log.i("Pagina carregada: " + tela.getWebEngine().getLocation());
                if (urlAtualContem("EstacaoPonto?type=create")) {
                    Log.i("Injetando codigos no formulário via JavaScript");
                    setarInputCodigos();
                    boolean ret = false;
                    try {
                        ret = criarArquivoBatimentos();
                    } catch (IOException ex) {
                        Logger.getLogger(ChangeUrlListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (urlAtualContem("presenca/IniciarPonto")) {
                    Log.i("Entrei no metodo");
                    String codigos = IntranetURLs.getCodigos();
                    Log.i("codigos: " + codigos) ;
                    String url = IntranetURLs.INICIALIZAR_PONTO + codigos;
                    Log.i(url);
                    mudarUrlAtualPara(url);

                }
            }
        }
    }

    private void setarInputCodigos() {
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

    private boolean criarArquivoBatimentos() throws IOException {
        String codUnic = RegistroWindows.getCodigoUnicoMaquina().substring(2, 10);
        java.io.File diretorio = new java.io.File("C:\\Estacao");
        java.io.File arquivo = new java.io.File(diretorio, codUnic + ".txt");
        try {
            boolean statusDir = diretorio.mkdir();
            boolean statusArq = arquivo.createNewFile();
            Log.i("criou o diretorio : " + statusDir);
            Log.i(statusArq);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }
}
