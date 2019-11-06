package controllers;

import async.PreProcessandoService;
import async.ThreadRelogio;
import core.RegistroWindows;
import core.leitura.LeitorDigital;
import exception.BiometricException;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
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
import utils.ConexaoIntranetService;
import utils.LogAplicacao;
import utils.The;
import view.TelaPonto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;

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
    public String prediosIds;

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
        The.inserirJavascript(tela.getWebEngine(), "changeInfoDigital('warning','Cadastrando novas digitais');");
        try {
            String digitaisHash = getLeitorDigital().enroll();

            if (digitaisHash != null || !digitaisHash.equals("null")) {
                The.inserirJavascript(tela.getWebEngine(), "jQuery('#digitaisHash').val('" + digitaisHash + "');");
                The.inserirJavascript(tela.getWebEngine(), "jQuery('#digitaisHash').val('" + digitaisHash + "');");
                The.inserirJavascript(tela.getWebEngine(), "changeInfoDigital('success','Digitais identificadas!');");
            }
        } catch (BiometricException ex) {
            LogAplicacao.e(ex.getMessage());
            The.inserirJavascript(tela.getWebEngine(), "changeInfoDigital('error','" + ex.getMessage() + "');");
        }
    }

    public PreProcessandoService getCds() {
        return cds;
    }

    public void inicializarLeitor() {
        cds = new PreProcessandoService();
        cds.setOnSucceeded( new PreProcessandoHandler());
        LogAplicacao.i("PreProcessandoService iniciado");
    }

    public LeitorDigital getLeitorDigital() {
        return this.cds.getLeitor();
    }

    /*
     * Recupera a thread do relogio
     * @return ThreadRelogio
     */
    public ThreadRelogio getThreadRelogio() {
        return threadRelogio;
    }

    /*
     * Cria a thread que controla o relogio da estacao
     * @param Calendar - data do servidor ao iniciar a estacao
     *
     */
    public void criarThreadRelogio(Calendar dtServidor) {
        threadRelogio = new ThreadRelogio(dtServidor);
        threadRelogio.start();
    }

    /*
     * Atualiza o horario atual e sincroniza os registros de ponto, caso tenha chegado o momento.
     * @param String - Horario no formato HH:MM
     */
    public void atualizarHorario(String horario) {
//        LogAplicacao.i("Inicia atualizacao de horario: "+horario);//#flag
        //String minutos = horario.split(":")[1];
        if (threadRelogio.fazerSincronizacao() ) { // fazerSincronizacao() - retorna true caso tenha chegado o horario de fazer sincronizacao
            final ConexaoIntranetService ci = new ConexaoIntranetService();
            ci.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent workerStateEvent) {
                    Long resposta = ci.getValue();
                    if (resposta != ConexaoIntranetService.NAO_CONECTADO) {
                        try {
                            iniciarSincronizacao();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            ci.start();
        }
        The.inserirJavascript(this.tela.getWebEngine(), "atualizaRelogioLocal('" + horario + "')");
    }

    public void reiniciarCapturaDigital(){
        LogAplicacao.i("Reinicia captura de digital.");//#flag
        if(this.getCds().getState().equals(Worker.State.SUCCEEDED)){
            this.inicializarLeitor();
        }
        this.INSTANCE.getCds().start();
    }

    /*
     * Apaga todos os registros do arquivo
     */
    public void apagarRegistrosBatimentos() throws IOException {
        LogAplicacao.i("Apagando registro de arquivos.");//#flag
        ArquivoRegistros.limparArquivo();
//        threadRelogio.desativarSincronizacao();
    }

    public void iniciarSincronizacao() throws FileNotFoundException, IOException
    {
        String dados = ArquivoRegistros.lerArquivoSincronizado();
        if(!dados.isEmpty()){
            LogAplicacao.i("Iniciando sincronizacao. Data: "+threadRelogio.getDataServidorAtual().getTime());
            The.inserirJavascript(this.tela.getWebEngine(), "sincronizaPonto('" + dados + "','"+RegistroWindows.getCodigoAtivacaoRegistro()+"')");
        }
        threadRelogio.setUltimaSincronizacao((Calendar) threadRelogio.getDataServidorAtual().clone());
        //threadRelogio.desativarSincronizacao();
    }
    public void addUploadFile(int size){
        String codAtivacao = RegistroWindows.getCodigoAtivacaoRegistro();

        The.inserirJavascript(this.tela.getWebEngine(), "adicionaUpload('"+codAtivacao+"','"+nomeLog+"',"+size+")");
    }

    public void doUploadParte()
    {   String codAtivacao = RegistroWindows.getCodigoAtivacaoRegistro();

        for (int i = 0; i<arr.length;i++)
        {
            String parte =  arr[i].replace("\\", "\\\\"); //  replace \ por \\
            parte =  parte.replace("\'", "\\\'");//  replace ' por \'
            String js="adicionaParte('" + codAtivacao + "','"+nomeLog+"','"+parte+"',"+i+")";
            The.inserirJavascript(this.tela.getWebEngine(), js);
        }
        LogAplicacao.i("fim do envio do arquivo "+nomeLog);
    }
}
