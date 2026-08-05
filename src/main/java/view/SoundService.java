package view;

import javafx.scene.media.AudioClip;
import utils.LogAplicacao;

/**
 * Created by Danilo on 07/02/14.
 */
public class SoundService {

    private String AUDIO_OK = getClass().getResource("/beep/ok.mp3").toString();
    private String AUDIO_ERROR = getClass().getResource("/beep/erro.mp3").toString();

    private AudioClip audioOk;
    private AudioClip audioError;

    // NOTE: o player de mídia nativo do JavaFX (com.sun.media.jfxmediaimpl)
    // trava com EXCEPTION_ACCESS_VIOLATION (crash nativo, não capturável por
    // try/catch) em builds OpenJFX que não têm os codecs nativos completos
    // (ex: Zulu 8 FX no Windows). Som é um recurso cosmético — desativado
    // por completo aqui pra não arriscar derrubar o processo inteiro durante
    // o fluxo de biometria. Reativar exigiria trocar de runtime JavaFX
    // (ex: um build com suporte a mídia completo) e validar antes.
    public void init() {
    }

    public void playOK(){
    }
    public void playError(){
    };
}
