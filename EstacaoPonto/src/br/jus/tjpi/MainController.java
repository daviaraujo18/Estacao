package br.jus.tjpi;

import br.jus.tjpi.listeners.ChangeUrlListener;
import br.jus.tjpi.system.utils.EstacaoPontoUtils;
import br.jus.tjpi.system.utils.LeitorDigital;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Screen;


public class MainController
    implements Initializable {

   	@FXML //  fx:id="imageView"
    private ImageView imageView; // Value injected by FXMLLoader

    @FXML //  fx:id="mainAnchorPane"
    private AnchorPane mainAnchorPane; // Value injected by FXMLLoader

    @FXML //  fx:id="splitPanel"
    private SplitPane splitPanel; // Value injected by FXMLLoader

    @FXML //  fx:id="webView"
    private WebView webView; // Value injected by FXMLLoader


    private WebEngine webEngine;
    
    private final LeitorDigital ld = new LeitorDigital();
    
    private Map<Integer,List> digitaisFrequentadores;
    private Map<Integer,List> dadosFrequentadores;

	
    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getBounds();
        
        /*
         * Setando splitPane para nao mostrar o painel de baixo
         * onde ficam os botoes para cadastro das digitais
         */
        
        splitPanel.setDividerPosition(1, 0);
//        splitPanel.setDividerPosition(0, 0.1);
        
        
        
//        Image image = new Image("/res/img/topoEstacaoPonto.png");

//        Image image = new Image(MainController.class.getResourceAsStream("topoEstacaoPonto.png"));
        
//        imageView = new ImageView(image);
        imageView.setFitWidth(bounds.getWidth());
        webEngine = webView.getEngine();
        
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeUrlListener(this));
        webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
           

            @Override
            public void handle(WebEvent<String> t) {
                
                if(webEngine.getLocation().contains("tjpi/presenca/InicializarPonto")) {
                    // Log.i("Recuperando dados dos frequentadores");
                    //                     
                    //                     double timeInicio = System.currentTimeMillis();
                    //                     
                    //                     Object data = webEngine.executeScript("window.bdFrequencia");
                    // 
                    //                     String dataFixed = (String) data.toString().replace("\n", "");
                    // 
                    //                     Log.i("Montando dados");
                    //                     String[] array = ((String)dataFixed).split("'");
                    //                     String[] dados;
                    //                     digitaisFrequentadores = new HashMap<>();
                    //                     dadosFrequentadores = new HashMap<>();
                    //                     List digitais;
                    //                     List dadosFreq; 
                    //                     for(int i=0;i<array.length;i++) {
                    //                          digitais = new ArrayList<>(4);
                    //                          dadosFreq = new ArrayList<>(2);
                    //                          dados = array[i].split(";");
                    //                          
                    //                          dadosFreq.add(dados[1]);
                    //                          dadosFreq.add(dados[2]);
                    //                          
                    //                          digitais.add(dados[3]);
                    //                          digitais.add(dados[4]);
                    //                          digitais.add(dados[5]);
                    //                          digitais.add(dados[6]);
                    //                          digitaisFrequentadores.put(Integer.parseInt(dados[0]), digitais);
                    //                          dadosFrequentadores.put(Integer.parseInt(dados[0]), dadosFreq);
                    //                     }
                    // 
                    //                     double timeFim = System.currentTimeMillis();
                    //                     Log.i("Montagem finalizada");
                    //                     Log.i("Time elapsed: "+(timeFim - timeInicio)+" ms");
                    //                     Log.i("Total frequentadores carregados: "+digitaisFrequentadores.size());
                    //                     
                    //                     Collection<List> a = dadosFrequentadores.values();
                    //                     Iterator it = a.iterator();
                    //                     while(it.hasNext()) {
                    //                         ArrayList b = (ArrayList) it.next();
                    //                         System.out.println("Matricula: "+b.get(0)+" Nome: "+b.get(1));
                    //                     }
                    
                    //Redireciona para a pagina de bater ponto!
                    webEngine.load("http://www.google.com");
                }
                
            }
        });
        
        String codigoUnicoMaquina = EstacaoPontoUtils.getCodigoUnicoMaquina();
        String codigoAtivacao = EstacaoPontoUtils.getCodigoAtivacaoRegistro();
        String params = "?codigoAtivacao="+codigoAtivacao+
                "&codigoUnicoMaquina="+codigoUnicoMaquina;
        
        System.out.println("CodigoUnicoMaquina: "+codigoUnicoMaquina);
        
        
        
        webEngine.load("http://192.168.1.114:8080/intranet/tjpi/presenca/InicializarPonto"+params);
//        webEngine.load("http://www.google.com");
        
        System.out.println("Terminou Inicializar");
        
    }

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