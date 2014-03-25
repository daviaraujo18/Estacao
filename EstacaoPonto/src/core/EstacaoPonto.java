package core;

import controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import utils.KeyHook;
import view.BloqueioTela;


/**
 * Classe principal da core.EstacaoPonto
 * Nela sao definidos metodos que carregam a aplicacao em si
 *
 * @author Anderson Soares
 */
public class EstacaoPonto extends Application{

    private Stage stage;
    private static EstacaoPonto INSTANCE;

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
    public void init() throws Exception {
        System.out.println("INITIALIZING");
        super.init();
        KeyHook.getInstance().blockWindowsKey();
    }

    /**
     * Metodo que eh chamado quando a aplicacao eh finalizada
     *
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {

        System.out.println("STOPPING");
        KeyHook.getInstance().unblockWindowsKey();
        super.stop();
    }

    @Override
    public void start(Stage palco) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/resources/Main.fxml"));

        this.stage = palco;
        Scene scene = new Scene(root);
        KeyHook.getInstance().blockWindowsKey();

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
        //stage.setFullScreen(true);

        BloqueioTela.getInstance().addClickTarget(root, MainController.INSTANCE.tela.webView);

        stage.show();
    }

    public static void main(String... args){
        launch(args);
    }


}

