package core;

import controllers.MainController;
import core.leitura.LeitorDigital;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import utils.LogAplicacao;
import utils.LogEstacao;
import utils.ScriptsBat;
import view.BloqueioTela;

import java.io.File;


/**
 * Classe principal da core.EstacaoPonto
 * Nela sao definidos metodos que carregam a aplicacao em si
 *
 */
public class EstacaoPonto extends Application{

    private Stage stage;
    private static EstacaoPonto INSTANCE;
    public final static String versao = "1.0";
    //   public final static String ambiente = "desenvolvimento"; //local
//    public final static String ambiente = "teste"; //3.6 Descomentar para apontar para a base de teste e modificar o config.properties
    public final static String ambiente = "producao"; //0.6

    public EstacaoPonto(){
        super();
        this.INSTANCE = this;
    }

    public static EstacaoPonto getInstance(){
        return INSTANCE;
    }

    /**
     * Metodo que eh chamado antes de iniciar a aplicacao em si
     * @throws Exception
     */
    @Override
    public void init() {
        try {

            LocalPaths.createDirs();

            LocalPaths.checarArquivos();

            LocalPaths.moverDiretorioAntigo();

            IntranetURLs.init();

            ScriptsBat.init();

//            String base_intranet_url = Configuracoes.base_intranet_url.get();
//            if (!base_intranet_url.equals("http://www.tjpi.jus.br/intranet")) {
//                GerarConfig.init();
//                ScriptsBat.restartAplicacao(false);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        BloqueioTela.getInstance().bloquearTeclas();
    }

    /**
     * Metodo que eh chamado quando a aplicacao eh finalizada
     *
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {

        LogEstacao.i("STOPPING");
        BloqueioTela.getInstance().desbloquearTeclas();
        super.stop();
    }

    @Override
    public void start(Stage palco) throws Exception {

        LeitorDigital.getInstance().check();

        Parent root = FXMLLoader.load(getClass().getResource("/Main.fxml"));

        this.stage = palco;
        Scene scene = new Scene(root);
        BloqueioTela.getInstance().bloquearTeclas();


        stage.setScene(scene);
        stage.setTitle("TJPI - Estação Ponto de Presença");

        // Setando stage para maximized
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getBounds();

        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        // -40 por causa da barra do Menu iniciar do windows
        stage.setHeight(bounds.getHeight()-40);
        // Setando tela cheia ON

        stage.setFullScreen(Configuracoes.tela_cheia.getBooleanValue());

        BloqueioTela.getInstance().addClickTarget(root, MainController.INSTANCE.tela.webView);

        stage.setOnCloseRequest(we -> {
            LogEstacao.i("Estação fechada");
            Platform.exit();
            System.exit(0);
        });

        stage.show();

        LogEstacao.i("Estação iniciada");
    }

    public static void main(String... args){
        launch(args);
    }

    static {
        try {
            // Setando variavel de sistema para log4j salvar os arquivos de log em LOCALAPPDATA
            System.setProperty("app.root", LocalPaths.APP_DIR);
            System.loadLibrary("NBioBSP");
            System.loadLibrary("NBioBSPCOM");
            System.loadLibrary("NBioBSPJNI");
            System.loadLibrary("ICE_JNIRegistry");
        } catch (UnsatisfiedLinkError  e) {
            System.out.println(e.getMessage());
            System.out.println("Não foi possível encontrar as DLLs compatíveis com o sistema operacional");
            System.exit(0);
        }
    }

}

