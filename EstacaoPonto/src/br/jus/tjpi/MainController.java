package br.jus.tjpi;

import br.jus.tjpi.listeners.ChangeUrlListener;
import br.jus.tjpi.listeners.OnAlertListener;
import br.jus.tjpi.system.utils.LeitorDigital;
import br.jus.tjpi.utils.Log;
import br.jus.tjpi.utils.The;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
		imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent t) {
				webEngine.load(IntranetURLsConstants.BASE_URL);
			}
		});

        
    }
    
	@FXML
    void cadastrarDigital(MouseEvent event) {
		try {
			Log.i("Cadastrando Digital");
			String digitaisHash = ld.enroll();
			The.inserirJavascript(webEngine, "jQuery('#digitaisHash').val('"+digitaisHash+"');");
			The.inserirJavascript(webEngine, "changeInfoDigital('success','Digitais identificadas!');");
		} catch (Exception ex) {
			The.inserirJavascript(webEngine, "changeInfoDigital('error','"+ex.getMessage()+"');");
		}
    }
	
	@FXML
	void changeComboBox(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ENTER)) {
			if (webEngine.getLocation().contains("tjpi/presenca/PontoDePresenca")) {
				The.inserirJavascript(webEngine, "changeRadioType()");
//				try {
//					LeitorDigital leitor = new LeitorDigital();
//					String digital = leitor.capturarDigital();
//					System.out.println(digital);
//				} catch (Exception e) {
//					System.out.println("--- "+e.getMessage());
//				}
			}
		}
	}
	
	public void capturarDigital() {
		The.inserirJavascript(webEngine, "changeMensagemStatus('<center>Coloque a digital no leitor</center>')");
//		Runnable a = new Runnable() {
//
//					 @Override
//					 public void run() {
//						 LeitorDigital ld = new LeitorDigital();
//						 try {
//							 System.out.println("Digital capturada: "+ld.capturarDigital());
//							 The.inserirJavascript(webEngine, "changeMensagemStatus('<center>Digital lida com sucesso!</center>')");
//						 } catch (Exception ex) {
//							 System.out.println(ex.getMessage());
//							 The.inserirJavascript(webEngine, "changeMensagemStatus('<center>Não foi possível ler a digital.</center>')");
//						 }
//					 }
//				 };
//		a.run();
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