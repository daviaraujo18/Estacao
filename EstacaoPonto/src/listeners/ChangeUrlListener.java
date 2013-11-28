/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;

import controllers.MainController;
import core.IntranetURLs;
import core.RegistroWindows;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import utils.Log;

/**
 * Classe que verifica toda vez que ocorre uma mudança de página e faz as
 * devidas modificaçoes dependendo da url atual
 *
 * @author Anderson Soares
 */
public class ChangeUrlListener implements ChangeListener<Object> {

    private MainController mainController;

    public ChangeUrlListener(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void changed(ObservableValue<? extends Object> ov, Object t, Object t1) {

        if (ov.getValue().equals(Worker.State.SCHEDULED)) {
            Log.i("Carregando pagina: " + mainController.getWebEngine().getLocation());
            if (urlAtualContem("Frequentador?type=create")) {
            //if (urlAtualContem("presenca/Frequentador")) {
                mainController.getSplitPanel().getDividers().get(1).setPosition(0.5);
                mainController.getBotaoCadastrarDigital().setVisible(true);
                mainController.getBotaoAtualizarDigital().setVisible(false);
            }
            else if(urlAtualContem("Frequentador?type=update")){
                mainController.getSplitPanel().getDividers().get(1).setPosition(0.5);
                mainController.getBotaoCadastrarDigital().setVisible(false);
                mainController.getBotaoAtualizarDigital().setVisible(true);
            }else {
                mainController.getSplitPanel().getDividers().get(1).setPosition(0.999);
            }
        } else {

            if (ov.getValue().equals(Worker.State.SUCCEEDED)) {
                Log.i("Pagina carregada: " + mainController.getWebEngine().getLocation());
                if (urlAtualContem("EstacaoPonto?type=create")) {
                    Log.i("Injetando codigos no formulário via JavaScript");
                    setarInputCodigos();
                    boolean ret = false;
                    try {
                        ret = criarArquivoBatimentos();
                    } catch (IOException ex) {
                        Logger.getLogger(ChangeUrlListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("Criou o registro: " + ret);
                } else if (urlAtualContem("presenca/IniciarPonto")) {
                    Log.i("Entrei no metodo");
                    mudarUrlAtualPara(IntranetURLs.INICIALIZAR_PONTO + IntranetURLs.getCodigos());
                }
            }
        }
    }

    private void setarInputCodigos() {
        WebEngine webEngine = mainController.getWebEngine();

        String codigoAtivacao = RegistroWindows.gerarCodigoAtivacao();
        String codigoUnicoMaquina = RegistroWindows.getCodigoUnicoMaquina();
        webEngine.executeScript(""
                + "jQuery('#codigoUnicoMaquina').val('" + codigoUnicoMaquina + "');"
                + "jQuery('#codigoAtivacao').val('" + codigoAtivacao + "');");
    }

    private boolean urlAtualContem(String texto) {
        return mainController.getWebEngine().getLocation().contains(texto);
    }

    private void mudarUrlAtualPara(String novaURL) {
        mainController.getWebEngine().load(novaURL);
    }

    private boolean criarArquivoBatimentos() throws IOException {
        String codUnic = RegistroWindows.getCodigoUnicoMaquina().substring(2, 10);
        java.io.File diretorio = new java.io.File("C:\\Estacao");
        java.io.File arquivo = new java.io.File(diretorio, codUnic + ".txt");
        try {
            boolean statusDir = diretorio.mkdir();
            boolean statusArq = arquivo.createNewFile();
            System.out.println("criou o diretorio : " + statusDir);
            System.out.print(statusArq);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }
}
