package view;

import controllers.MainController;
import core.IntranetURLs;
import core.LocalPaths;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import listeners.ChangeUrlListener;
import listeners.OnAlertListener;
import utils.The;
import utils.VerificaConexao;

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
    public ProgressBar progressBar;
    public Label labelProgressBar;
    public WebView webView;
    private WebEngine webEngine;

    public SoundService sound;
    public boolean semConexao = true;
    public static TelaPonto INSTANCE = new TelaPonto();

    public static TelaPonto getInstance() {
        return INSTANCE;
    }

    public void init(){
        initWebEngine();
        initSoundService();
    }

    private void initSoundService(){
        this.sound = new SoundService();
        this.sound.init();
    }

    public boolean initWebEngine() {
        boolean con = false;
        webEngine = webView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeUrlListener(this));
        webEngine.setOnAlert(new OnAlertListener());
            // mostrar uma mensagem informando que está sem permissão de escrita
        if (LocalPaths.getParticao()==null || LocalPaths.getParticao().isEmpty())
        {
            this.labelSemConexao.setText("Sem permissão de escrita.");
            this.labelSemConexao.setPrefWidth(600);
            this.labelSemConexao.setLayoutX(380);
            this.labelSemConexao.setVisible(true);
            return true;
        } 
        
        // mostrar uma mensagem informando que está sem conexão
        Task task = new Task() {

            @Override
            protected Object call() throws Exception {
                System.out.println("Entrando no loop de teste de conexão.");
                while (semConexao){

                    boolean con = false;
                    
                    con = VerificaConexao.verificaConexao(IntranetURLs.INICIAR_PONTO);
                    semConexao = !con;
                    if (semConexao)
                    {
                        labelSemConexao.setVisible(true);
                        Thread.sleep(1000);
                    }
                    else
                    {
                        labelSemConexao.setVisible(false);
                    }
                   
                    
                }
                System.out.println("Fim do teste de conexão");
                return null;
            }
        };
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>()
        {
            @Override
            public void handle(WorkerStateEvent t) 
            {
                
                webEngine.load(IntranetURLs.INICIAR_PONTO);
                imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        if(!BloqueioTela.getInstance().isBloqueada()){
                            webEngine.load(IntranetURLs.BASE_URL);
                            MainController.INSTANCE.getCds().setUsarLeitor(true);
                        }
                    }
                });

            }
        });
        Thread nova = new Thread(task);
        nova.start();
        
        return false;
    }

    public void onTopClicked(){
        webEngine.load(IntranetURLs.BASE_URL);
        MainController.INSTANCE.getCds().setUsarLeitor(true);
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
    
    public ProgressBar getProgressBar() {
        return progressBar;
    }
    public Label getLabelProgressBar() {
        return labelProgressBar;
    }
    public Button getBotaoAtualizarDigital() {
        return botaoAtualizarDigital;
    }

    public WebView getWebView() {
        return webView;
    }

    public WebEngine getWebEngine(){return webEngine;}

    public void lock(){
        The.inserirJavascript(this.webEngine, "lock()");
    }
    public void unlock(){
        The.inserirJavascript(this.webEngine, "unlock()");
    }

    
}
