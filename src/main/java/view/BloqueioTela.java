package view;

import controllers.MainController;
import core.Configuracoes;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import utils.KeyHook;

/**
 * Created by Danilo on 14/03/14.
 */
public class BloqueioTela {

    private static BloqueioTela instance = new BloqueioTela();
    private boolean bloqueada = false;
//    EventHandler<MouseEvent> clickHandler;

    private BloqueioTela(){
        final EventHandler<MouseEvent> handler = mouseEvent -> {
            if(bloqueada){
                MainController.INSTANCE.getCds().clickDesbloqueioTela = true;
            }
        };
    }

    public void addClickTarget(Node... nodes){
        for (Node node : nodes){
//            node.setOnMouseClicked(clickHandler);
            onKeyPressed(node);
        }
    }
    public static BloqueioTela getInstance() {
        return instance;
    }

    public void init(Parent root) {
        onKeyPressed(root);
    }

    public void bloquear(){
        if(Configuracoes.bloqueio_tela.getBooleanValue()){
            this.bloquearTeclas();
            TelaPonto.getInstance().lock();
            bloqueada = true;
        }
    }

    public void desbloquear(){
        this.desbloquearTeclas();
        TelaPonto.getInstance().unlock();
        bloqueada = false;
    }

    private void onMouseClicked(Scene root) {
        final EventHandler<MouseEvent> handler = mouseEvent -> {
            if(bloqueada){
                MainController.INSTANCE.getCds().clickDesbloqueioTela = true;
            }
        };

        root.setOnMouseClicked(handler);
    }

    private void onKeyPressed(Node root) {
        final EventHandler<KeyEvent> keyEventHandler = keyEvent -> {
            if(keyEvent.getCode() == KeyCode.A){
                if(bloqueada){
                    MainController.INSTANCE.getCds().clickDesbloqueioTela = true;
                }else{
                    bloquear();
                }
            }
        };
        root.setOnKeyPressed(keyEventHandler);
    }

    public boolean isBloqueada() {
        return bloqueada;
    }

    public void desbloquearTeclas() {
        KeyHook.getInstance().unblockWindowsKey();
    }

    public void bloquearTeclas() {
        if(Configuracoes.bloqueio_tela.getBooleanValue()){
            KeyHook.getInstance().blockWindowsKey();
        }
    }
}
