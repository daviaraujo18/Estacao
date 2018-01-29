package view;

import javafx.scene.media.AudioClip;
import utils.Log;

/**
 * Created by Danilo on 07/02/14.
 */
public class SoundService {

    private String AUDIO_OK = getClass().getResource("/resources/beep/ok.mp3").toString();
    private String AUDIO_ERROR = getClass().getResource("/resources/beep/erro.mp3").toString();

    private AudioClip audioOk;
    private AudioClip audioError;

    public void init() {

        audioOk = new AudioClip(AUDIO_OK);
        audioError = new AudioClip(AUDIO_ERROR);
    }

    public void playOK(){
        if (audioOk.isPlaying()){
            Log.i("//Estação: o som de OK já está tocando.");
        }else{
            Log.i("//Estação: o som de OK vai tocar agora.");
        }
        audioOk.play();
        
    }
    public void playError(){
        if (audioOk.isPlaying()) {
            Log.i("//Estação: o som de ERRO já está tocando.");
        }else{
            Log.i("//Estação: o som de ERRO vai tocar agora.");
        }
        audioError.play();
    };
}
