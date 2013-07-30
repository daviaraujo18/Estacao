package controllers;

import async.CapturarDigitalService;
import async.ThreadRelogio;
import core.IntranetURLs;
import core.LeitorDigital;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import utils.Log;
import utils.The;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import listeners.ChangeUrlListener;
import listeners.OnAlertListener;
import utils.ArquivoRegistros;
import utils.VerificaConexao;

/**
 * @author aers
 */
public class MainController implements Initializable {

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        boolean con = false;
        try {
            ld = new LeitorDigital();
        } catch (Exception e) {
            Log.i("Leitor digital nao iniciado: " + e.getMessage());
        }

        webEngine = webView.getEngine();

        webEngine.getLoadWorker().stateProperty().addListener(new ChangeUrlListener(this));
        webEngine.setOnAlert(new OnAlertListener(this));
        try {
            con = VerificaConexao.verificaConexao(IntranetURLs.INICIAR_PONTO);
        } catch (MalformedURLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (con == false) {
            // mostrar uma mensagem informando que está sem conexão
            this.labelSemConexao.setVisible(true);
            return;
        }
        webEngine.load(IntranetURLs.INICIAR_PONTO);
//        webEngine.load("http://www.google.com");
        imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                webEngine.load(IntranetURLs.BASE_URL);
            }
        });
    }

    @FXML
    void cadastrarDigital(MouseEvent event) {
        try {
            Log.i("Cadastrando Digital");
            String digitaisHash = ld.enroll();
            The.inserirJavascript(webEngine, "jQuery('#digitaisHash').val('" + digitaisHash + "');");
            The.inserirJavascript(webEngine, "changeInfoDigital('success','Digitais identificadas!');");
        } catch (Exception ex) {
            The.inserirJavascript(webEngine, "changeInfoDigital('error','" + ex.getMessage() + "');");
        }
    }

    @FXML
    void changeComboBox(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER) && !LeitorDigital.ativo) {
            if (webEngine.getLocation().contains("tjpi/presenca/PontoDePresenca")) {
                boolean modalAtivo = Boolean.parseBoolean(The.inserirJavascript(webEngine, "isModalAtivo()").toString());
                if (!modalAtivo) {
                    The.inserirJavascript(webEngine, "changeRadioType()");
                } else {
                    The.inserirJavascript(webEngine, "modal(false)");
                }
            }
        }
    }

    public void capturarDigital() {

        The.inserirJavascript(webEngine, "changeMensagemStatus('<center>Coloque a digital no leitor</center>')");

        CapturarDigitalService cds = new CapturarDigitalService(ld);
        cds.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                String digitalHash = (String) t.getSource().getValue();
                try {

                    if (digitalHash != null && !digitalHash.isEmpty()) {
                        int id = ld.searchDigitalOnIndexSearchEngine(digitalHash);

                        if (id > 0) {
                            System.out.println("ID Founded: " + id);
                            //The.inserirJavascript(webEngine, "baterPonto("+id+")");
                            String tipoRegistroFrequencia = (String) The.inserirJavascript(webEngine, "getSelectedTipoRegistroFrequencia()");

                            boolean ret = ArquivoRegistros.escrever(id + "-" + tipoRegistroFrequencia + "-" + threadRelogio.getMomentoBatimento() + ";");
                            if(ret == true)
                            {
                                The.inserirJavascript(webEngine, "baterPontoLocal('"+ id +"','"+tipoRegistroFrequencia+"','"+threadRelogio.getMomentoBatimentoFrequentador()+"')");
                            }
                            

//							The.inserirJavascript(webEngine, "changeMensagemStatus('<center>Digital lida com sucesso! ID: "+id+"</center>')");
                        } else {
                            The.inserirJavascript(webEngine, "changeMensagemStatus('<center>DIGITAL NÃO ENCONTRADA!</center>')");
                        }
                    } else {
                        The.inserirJavascript(webEngine, "changeMensagemStatus('<center>Não foi possível ler a digital.</center>')");
                        //The.inserirJavascript(webEngine, "atualizaRelogioLocal('13:00')");
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
        });

        cds.start();
    }
    @FXML //  fx:id="imageView"
    private ImageView imageView; // Value injected by FXMLLoader
    @FXML //fx:id="label"
    private Label labelSemConexao;
    @FXML //  fx:id="mainAnchorPane"
    private AnchorPane mainAnchorPane; // Value injected by FXMLLoader
    @FXML
    private AnchorPane anchorBaixo;
    @FXML //  fx:id="splitPanel"
    private SplitPane splitPanel; // Value injected by FXMLLoader
    @FXML //  fx:id="webView"
    private WebView webView; // Value injected by FXMLLoader
    private WebEngine webEngine;
    private LeitorDigital ld;
    private ThreadRelogio threadRelogio;
    private Map<Integer, List> digitaisFrequentadores;
    private Map<Integer, List> dadosFrequentadores;

    public AnchorPane getMainAnchorPane() {
        return mainAnchorPane;
    }

    public LeitorDigital getLeitorDigital() {
        return ld;
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

    public ThreadRelogio getThreadRelogio() {
        return threadRelogio;
    }

    public void criarThreadRelogio(Calendar dtServidor) {
        threadRelogio = new ThreadRelogio(dtServidor);
        threadRelogio.start();
    }

    public void atualizarHorario(String horario) throws FileNotFoundException, IOException {
        System.out.println("\nMetodo atualizar do mainController...");
        String minutos = horario.split(":")[1];
        int min = Integer.parseInt(minutos);
        //faz a sincronizacao 2 min depois de iniciada a estacao ponto - teste
        if (min == (threadRelogio.getMinutosServidorInicial() + 2 )) {
            System.out.println("Vai ler arquivo...");
            if (VerificaConexao.verificaConexao(IntranetURLs.BASE_URL)) {
                System.out.println("lendo o arquivo..");
                String dados = ArquivoRegistros.ler();
                The.inserirJavascript(webEngine, "sincronizaPonto('" + dados + "')");
            }
            else
            {
                System.out.println("\nSem Conexao....");
            }

        }
        The.inserirJavascript(webEngine, "atualizaRelogioLocal('" + horario + "')");
    }

    public void apagarRegistrosBatimentos() throws IOException {
        ArquivoRegistros.limparArquivo();
    }
}