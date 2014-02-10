package view;

import controllers.MainController;
import core.IntranetURLs;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import listeners.ChangeUrlListener;
import listeners.OnAlertListener;
import utils.VerificaConexao;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Danilo on 07/02/14.
 */
public class TelaPonto {

    public ImageView imageView;
    public Label labelSemConexao;
    public AnchorPane mainAnchorPane;
    public AnchorPane anchorBaixo;
    public SplitPane splitPanel;
    public Button botaoCadastrarDigital;
    public Button botaoAtualizarDigital;
    public WebView webView;
    public WebEngine webEngine;

    public SoundService sound;

    public void init(){
        initWebEngine();
        initSoundService();
    }

    private void initSoundService(){
        this.sound = new SoundService();
        this.sound.init();
    }

    private boolean initWebEngine() {
        boolean con = false;
        webEngine = webView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeUrlListener(this));
        webEngine.setOnAlert(new OnAlertListener(MainController.INSTANCE));
        try {
            con = VerificaConexao.verificaConexao(IntranetURLs.INICIAR_PONTO);
        } catch (MalformedURLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        if (con == false) {
            // mostrar uma mensagem informando que está sem conexão
            this.labelSemConexao.setVisible(true);
            return true;
        }
        webEngine.load(IntranetURLs.INICIAR_PONTO);

        imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                webEngine.load(IntranetURLs.BASE_URL);
            }
        });
        return false;
    }

    public AnchorPane getMainAnchorPane() {
        return mainAnchorPane;
    }

    public SplitPane getSplitPanel() {
        return splitPanel;
    }

    public Button getBotaoCadastrarDigital() {
        return botaoCadastrarDigital;
    }

    public Button getBotaoAtualizarDigital() {
        return botaoAtualizarDigital;
    }

    public WebView getWebView() {
        return webView;
    }

    public WebEngine getWebEngine(){return webEngine;}


}
