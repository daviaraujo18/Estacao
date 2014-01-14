package controllers;

import async.CapturarDigitalService;
import async.ThreadRelogio;
import core.IntranetURLs;
import core.LeitorDigital;
import core.RegistroWindows;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import listeners.ChangeUrlListener;
import listeners.OnAlertListener;
import utils.ArquivoRegistros;
import utils.Log;
import utils.The;
import utils.VerificaConexao;

/**
 * @author aers
 */
public class MainController implements Initializable {
    //private String AUDIO_OK = getClass().getResource("/resources/beep/ok.mp3").toString();
    //private String AUDIO_ERRO = getClass().getResource("/resources/beep/erro.mp3").toString();
    //private AudioClip audioOk;
    //private AudioClip audioErro;
    private boolean erroLeituraDigital = false;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        boolean con = false;
        //audioOk = new AudioClip(AUDIO_OK);
        //audioErro = new AudioClip(AUDIO_ERRO);
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
            int id = ld.searchDigitalOnIndexSearchEngine(digitaisHash);
            System.out.println("ID DA DIGITAL ::: " + id);
            The.inserirJavascript(webEngine, "jQuery('#digitaisHash').val('" + digitaisHash + "');");
            The.inserirJavascript(webEngine, "changeInfoDigital('success','Digitais identificadas!');");
        } catch (Exception ex) {
            The.inserirJavascript(webEngine, "changeInfoDigital('error','" + ex.getMessage() + "');");
        }
    }
    
    @FXML
    void atualizarDigital(MouseEvent event) {
        try {
            Log.i("Atualizando Digital");
            String digitaisHash = ld.enroll();
            int id = ld.searchDigitalOnIndexSearchEngine(digitaisHash);
            System.out.println("Até a linha 107 Ok.");
            int idF = Integer.parseInt(The.inserirJavascript(webEngine, "getIdFrequentador()").toString());
            System.out.println("ID: " + idF);
            if(id == idF)
            {
                System.out.println("Digitais Inseridas! ");
                The.inserirJavascript(webEngine, "jQuery('#digitaisHash').val('" + digitaisHash + "');");
                The.inserirJavascript(webEngine, "changeInfoDigital('success','Digitais inseridas!');");
            }
            else
            {
                System.out.println("ID = 0 ou de outro frequentador!");
                The.inserirJavascript(webEngine, "changeInfoDigital('error','Digitais já existem!');");
            }
        } catch (Exception ex) {
            The.inserirJavascript(webEngine, "changeInfoDigital('error','" + ex.getMessage() + "');");
        }
    }

    @FXML
    void changeComboBox(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER))
        {
               boolean modalAtivo = Boolean.parseBoolean(The.inserirJavascript(webEngine, "isModalAtivo()").toString());
               if (modalAtivo) {
                   The.inserirJavascript(webEngine, "modal(false)");
               }    
        }
        if (event.getCode().equals(KeyCode.ENTER) && !LeitorDigital.ativo ) {
            if (webEngine.getLocation().contains("tjpi/presenca/PontoDePresenca")) {
                boolean modalAtivo = Boolean.parseBoolean(The.inserirJavascript(webEngine, "isModalAtivo()").toString());
                if (!modalAtivo && !ThreadRelogio.sincronizacaoAtiva) {
                    The.inserirJavascript(webEngine, "changeRadioType()");
                } else {
                    The.inserirJavascript(webEngine, "modal(false)");
                }
            }
        }
    }

    public void capturarDigital() {
        
        The.inserirJavascript(webEngine, "changeMensagemStatus('<center>Coloque a digital no leitor</center>')");
        //colocar aqui o audio de 'esperando digital'
        CapturarDigitalService cds = new CapturarDigitalService(ld);
        cds.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                String digitalHash = (String) t.getSource().getValue();
                try {
                    //audioDigital.play();
                    if (digitalHash != null && !digitalHash.isEmpty()) {
                        int id = ld.searchDigitalOnIndexSearchEngine(digitalHash);
                        if (id > 0) {
                            erroLeituraDigital = false;
                            //audioSucesso.play();
                            System.out.println("ID Founded: " + id);
                            System.out.println("Dados Freq: " + mapaIdInfoFrequentadores.get(id));
                            String[] dadosF = mapaIdInfoFrequentadores.get(id).split(";");
                            //The.inserirJavascript(webEngine, "baterPonto("+id+")");
                            String tipoRegistroFrequencia = (String) The.inserirJavascript(webEngine, "getSelectedTipoRegistroFrequencia()");

                            boolean ret = ArquivoRegistros.escreverRegistro(id + "-" + tipoRegistroFrequencia + "-" + threadRelogio.getMomentoBatimento() );
                            if(ret == true)
                            {
                                System.out.println("\n ::: Url Foto: " + dadosF[2]);
                                The.inserirJavascript(webEngine, "baterPontoLocal('"+ id +"','"+tipoRegistroFrequencia+"','"+threadRelogio.getMomentoBatimentoFrequentador()+ "','"+ dadosF[0] + "','" +  dadosF[1] + "','" +  dadosF[2]+"')");
                                
                            }

			The.inserirJavascript(webEngine, "changeMensagemStatus('<center>Tecle 'ENTER' para escolher tipo de registro</center>')");
                        } else if(erroLeituraDigital == false)
                        {
                             //audioErro.play();
                             erroLeituraDigital = true;
                             //The.inserirJavascript(webEngine, "ativarCronometroSelect()");   
                            // The.inserirJavascript(webEngine, "changeMensagemStatus('<center>Digital Não Encontrada!</center>')");
                        }
                            
                        } else {
                        //audioErro.play();
                        The.inserirJavascript(webEngine, "changeMensagemStatus('<center>Não foi possível ler a digital.</center>')");
                        //The.inserirJavascript(webEngine, "atualizaRelogioLocal('13:00')");
                        }
                    The.inserirJavascript(webEngine, "desligarLeitorDigital()");
                    boolean liberarSincronizacao = Boolean.parseBoolean(The.inserirJavascript(webEngine, "isSincronizacaoSolicitada").toString());
                    if(liberarSincronizacao)
                    {
                        iniciarSincronizacao();
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
    @FXML
    private Button botaoCadastrarDigital;
    @FXML
    private Button botaoAtualizarDigital;
    @FXML //  fx:id="webView"
    private WebView webView; // Value injected by FXMLLoader
    private WebEngine webEngine;
    private LeitorDigital ld;
    private ThreadRelogio threadRelogio;
    private Map<Integer, List> digitaisFrequentadores;
    private String[] arrayFrequentadores;
    private Map<Integer,String> mapaIdInfoFrequentadores;

    public AnchorPane getMainAnchorPane() {
        return mainAnchorPane;
    }

    public LeitorDigital getLeitorDigital() {
        return ld;
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

    public WebEngine getWebEngine() {
        return webEngine;
    }

    public Map<Integer, List> getDigitaisFrequentadores() {
        return digitaisFrequentadores;
    }

    public void setFrequentadores(Map<Integer, List> digitaisFrequentadores) {
        this.digitaisFrequentadores = digitaisFrequentadores;
    }
    
    /*
     * Recupera a thread do relógio 
     * @return ThreadRelogio
     */
    public ThreadRelogio getThreadRelogio() {
        return threadRelogio;
    }

    /*
     * Recupera o array com as informações dos frequentadores
     * @return String[] - array com as informações dos frequentadores ("id;matricula;nome;digital;foto")
     */
    public String[] getArrayFrequentadores() {
        return arrayFrequentadores;
    }

    /*
     * Altera o array com os dados dos frequemtadores ("id;matricula;nome;digital;foto")
     * @param String[] - array com os dados dos frequentadores
     */
    public void setArrayFrequentadores(String[] arrayFrequentadores) {
        this.arrayFrequentadores = arrayFrequentadores;
    }

    /*
     * Recupera o map com as informações dos frequentadores (id,"matricula;nome;foto")
     * @return Map<Integer, String> - map com as informações dos frequentadores
     */
    public Map<Integer, String> getMapaIdInfoFrequentadores() {
        return mapaIdInfoFrequentadores;
    }
    /*
     * Altera o map com as informações dos frequentadores - (nome, matricula, digital...)
     * @param Map<Integer, String> - map com as informações dos frequentadores
     */
    public void setMapaIdInfoFrequentadores(Map<Integer, String> mapaIdInfoFrequentadores) {
        this.mapaIdInfoFrequentadores = mapaIdInfoFrequentadores;
    }

    /*
     * Cria a thread que controla o relógio da estação
     * @param Calendar - data do servidor ao iniciar a estação
     * 
     */
    public void criarThreadRelogio(Calendar dtServidor) {
        threadRelogio = new ThreadRelogio(dtServidor);
        threadRelogio.start();
    }
    
    /*
     * Atualiza o horário atual e sincroniza os registros de ponto, caso tenha chegado o momento.
     * @param String - Horário no formato HH:MM
     */
    public void atualizarHorario(String horario) throws IOException{
        //String minutos = horario.split(":")[1];
        //faz a sincronizacao 1 h depois de iniciada a estacao ponto - teste
        if (threadRelogio.fazerSincronizacao() ) { // fazerSincronizacao() - retorna true caso tenha chegado o horario de fazer sincronizacao
            if (VerificaConexao.verificaConexao(IntranetURLs.BASE_URL)) {
                threadRelogio.ativarSincronizacao(); 
                The.inserirJavascript(webEngine, "ativaSincronizacao(true)");
            }
        }
        The.inserirJavascript(webEngine, "atualizaRelogioLocal('" + horario + "')");
    }
    
    /*
     * Apaga todos os registros do arquivo
     */
    public void apagarRegistrosBatimentos() throws IOException {
        ArquivoRegistros.limparArquivo();
        threadRelogio.desativarSincronizacao();
        The.inserirJavascript(webEngine, "ativaSincronizacao(false)");
        The.inserirJavascript(webEngine, "changeMensagemStatus('<center>Tecle enter para escolher tipo de registro</center>')");
    }
    
    public void iniciarSincronizacao() throws FileNotFoundException, IOException
    {
        System.out.println("Iniciando Sincronizacao.");
        The.inserirJavascript(webEngine, "changeMensagemStatus('<center>Aguardando Término da Sincronização</center>')");   
        String dados = ArquivoRegistros.lerArquivo();
        The.inserirJavascript(webEngine, "sincronizaPonto('" + dados + "','"+RegistroWindows.getCodigoAtivacaoRegistro()+"')");
        threadRelogio.setUltimaSincronizacao(Calendar.getInstance());
        //threadRelogio.desativarSincronizacao();
    }
}