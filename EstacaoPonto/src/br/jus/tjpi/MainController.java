package br.jus.tjpi;

import br.jus.tjpi.listeners.ChangeUrlListener;
import br.jus.tjpi.listeners.OnAlertListener;
import br.jus.tjpi.system.utils.LeitorDigital;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


public class MainController implements Initializable {

    @Override 
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        
        webEngine = webView.getEngine();
        
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeUrlListener(this));
        webEngine.setOnAlert(new OnAlertListener(this));
        
        webEngine.load(IntranetURLsConstants.INICIAR_PONTO);
//        webEngine.load("http://www.google.com");

        
    }
    
    
    @FXML //  fx:id="imageView"
    private ImageView imageView; // Value injected by FXMLLoader

    @FXML //  fx:id="mainAnchorPane"
    private AnchorPane mainAnchorPane; // Value injected by FXMLLoader
    
    @FXML
    private AnchorPane anchorBaixo;

    @FXML //  fx:id="splitPanel"
    private SplitPane splitPanel; // Value injected by FXMLLoader

    @FXML //  fx:id="webView"
    private WebView webView; // Value injected by FXMLLoader


    private WebEngine webEngine;
    
    private final LeitorDigital ld = new LeitorDigital();
    
    private Map<Integer,List> digitaisFrequentadores;
    private Map<Integer,List> dadosFrequentadores;

    public AnchorPane getMainAnchorPane() {
        return mainAnchorPane;
    }

    public SplitPane getSplitPanel() {
        return splitPanel;
    }

    public WebView getWebView() {
        return webView;
    }

    public WebEngine getWebEngine() {
        return webEngine;
    }


    public Map<Integer, List> getDigitaisFrequentadores() {
        return digitaisFrequentadores;
    }

    public void setFrequentadores(Map<Integer, List> digitaisFrequentadores) {
        this.digitaisFrequentadores = digitaisFrequentadores;
    }

    public Map<Integer, List> getDadosFrequentadores() {
        return dadosFrequentadores;
    }

    public void setDadosFrequentadores(Map<Integer, List> dadosFrequentadores) {
        this.dadosFrequentadores = dadosFrequentadores;
    }


    
    
}