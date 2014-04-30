package controllers;

import async.PreProcessandoService;
import async.ThreadRelogio;
import core.IntranetURLs;
import core.LocalPaths;
import core.RegistroWindows;
import core.leitura.LeitorDigital;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import utils.ArquivoRegistros;
import utils.Log;
import utils.The;
import utils.VerificaConexao;
import view.TelaPonto;

/**
 * @author aers
 */
public class MainController implements Initializable {

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
    private ProgressBar progressBar;
    @FXML
    private Label labelProgressBar;
    @FXML
    private Button botaoAtualizarDigital;
    @FXML //  fx:id="webView"
    private WebView webView; // Value injected by FXMLLoader

    private ThreadRelogio threadRelogio;

    private PreProcessandoService cds;
    public TelaPonto tela;
    public String nomeLog;
    public String[] arr;

    public static MainController INSTANCE;

    public MainController() {
        INSTANCE = this;
    }

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        initTela();
        inicializarLeitor();
    }

    private void initTela() {
        tela = TelaPonto.INSTANCE;
        tela.imageView = imageView;
        tela.labelSemConexao = labelSemConexao;
        tela.mainAnchorPane = mainAnchorPane;
        tela.anchorBaixo = anchorBaixo;
        tela.splitPanel = splitPanel;
        tela.botaoCadastrarDigital = botaoCadastrarDigital;
        tela.progressBar = progressBar;
        tela.labelProgressBar = labelProgressBar;
        tela.botaoAtualizarDigital = botaoAtualizarDigital;
        tela.webView = webView;
        tela.init();
    }

    @FXML
    void cadastrarDigital(MouseEvent event) {
        try {
            String digitaisHash = getLeitorDigital().enroll();
            int id = getLeitorDigital().searchDigitalOnIndexSearchEngine(digitaisHash);
            The.inserirJavascript(tela.getWebEngine(), "jQuery('#digitaisHash').val('" + digitaisHash + "');");
            The.inserirJavascript(tela.getWebEngine(), "jQuery('#digitaisHash').val('" + digitaisHash + "');");
            The.inserirJavascript(tela.getWebEngine(), "changeInfoDigital('success','Digitais identificadas!');");
        } catch (Exception ex) {
//            The.inserirJavascript(webEngine, "changeInfoDigital('error','" + ex.getMessage() + "');");
        }
    }

    public PreProcessandoService getCds() {
        return cds;
    }

    public void inicializarLeitor() {
        cds = new PreProcessandoService();
        cds.setOnSucceeded( new PreProcessandoHandler());
    }

    public LeitorDigital getLeitorDigital() {
        return this.cds.getLeitor();
    }

    /*
     * Recupera a thread do relógio 
     * @return ThreadRelogio
     */
    public ThreadRelogio getThreadRelogio() {
        return threadRelogio;
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
                iniciarSincronizacao();
            }
        }
        The.inserirJavascript(this.tela.getWebEngine(), "atualizaRelogioLocal('" + horario + "')");
    }

    public void reiniciarCapturaDigital(){
        if(this.getCds().isRunning()){
            this.inicializarLeitor();
        }
        MainController.INSTANCE.getCds().start();
    }

    /*
     * Apaga todos os registros do arquivo
     */
    public void apagarRegistrosBatimentos() throws IOException {
        ArquivoRegistros.limparArquivo();
//        threadRelogio.desativarSincronizacao();
    }

    public void iniciarSincronizacao() throws FileNotFoundException, IOException
    {
        String dados = ArquivoRegistros.lerArquivo();
        The.inserirJavascript(this.tela.getWebEngine(), "sincronizaPonto('" + dados + "','"+RegistroWindows.getCodigoAtivacaoRegistro()+"')");
        threadRelogio.setUltimaSincronizacao(Calendar.getInstance());
        //threadRelogio.desativarSincronizacao();
    }
public void iAmStillAlive(){
        
        
        String codAtivacao = RegistroWindows.getCodigoAtivacaoRegistro();
        The.inserirJavascript(this.tela.getWebEngine(), "iAmStillAlive('" + codAtivacao + "','"+getNameLogs()+"')");
        
    }
    public void addUploadFile(int size){
        String codAtivacao = RegistroWindows.getCodigoAtivacaoRegistro();
        
        The.inserirJavascript(this.tela.getWebEngine(), "adicionaUpload('"+codAtivacao+"','"+nomeLog+"',"+size+")");
    }   
    public void doUploadParte()
    {   String codAtivacao = RegistroWindows.getCodigoAtivacaoRegistro();
        for (int i = 0; i<arr.length;i++)
        {
            String parte =  arr[i].replace("\'", "\\\'");//arr[i].replace(')', ' ');
            String js="adicionaParte('" + codAtivacao + "','"+nomeLog+"','"+parte+"',"+i+")";
            The.inserirJavascript(this.tela.getWebEngine(), js);
        }
    }
    private String getNameLogs()
    {
        String logsNames="";
        File folder = new File(LocalPaths.PATH_LOG);
        
        if (folder.exists())
        {
            File[] listFiles = folder.listFiles();
            for (File file:listFiles)
            {
                if (file.getName().startsWith(Log.LOG_NAME_BEGIN))
                {
                    logsNames= file.getName()+" / "+logsNames;
                }
            }
        }
        System.out.println(logsNames);
        return logsNames;
    }
}
