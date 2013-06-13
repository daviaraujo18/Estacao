/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Classe principal da EstacaoPonto
 * Nela sao definidos metodos que carregam a aplicacao em si
 * 
 * @author Anderson Soares
 */
public class EstacaoPonto extends Application {

	/**
	 * Metodo que eh chamado antes de iniciar a aplicacao em si
	 * @throws Exception 
	 */
	@Override
	public void init() throws Exception {
		System.out.println("INITIALIZING");
		super.init();
	}

	/**
	 * Metodo que eh chamado quando a aplicacao eh finalizada
	 * 
	 * @throws Exception 
	 */
	@Override
	public void stop() throws Exception {
		System.out.println("STOPPING");
		super.stop();
	}

	@Override
    public void start(Stage stage) throws Exception {
        
	    Parent root = FXMLLoader.load(getClass().getResource("/resources/Main.fxml"));
        
        Scene scene = new Scene(root);
        
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
//        stage.setFullScreen(true);
        
        
        stage.show();

    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
		
		
			launch(args);
		
    }
}

