package utils;


import controllers.MainController;
import core.LocalPaths;
import core.OSVerifier;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class ScriptsBat {

    public static final String restartFileName = "restart.bat";
    public static void init() throws IOException {

        createOrUpdateRunOpenUpdate();

    }

    private static void createOrUpdateRunOpenUpdate() throws IOException {

        // O script de restart (.bat) so faz sentido em Windows. Em Linux a
        // aplicacao nao e empacotada como .exe e o restart via cmd.exe nao se
        // aplica.
        if (!OSVerifier.isWindows()) {
            return;
        }

        File restartBatScript = new File(LocalPaths.APP_DIR, restartFileName);

        FileWriter fw = new FileWriter(restartBatScript.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("taskkill /f /im EstacaoPonto.exe");
        bw.newLine();
        bw.write("cd \""+LocalPaths.INSTALL_DIR+"\"");
        bw.newLine();
        bw.write("start /b EstacaoPonto.exe");
        bw.newLine();
        bw.write("exit 0");
        bw.newLine();

        bw.close();
    }

    public static void restartAplicacao(boolean forcar) throws IOException {

        if (forcar) {
            restartAplicacao();
        }

    }

    public static void restartAplicacao() throws IOException {

        final ConexaoIntranetService ci = new ConexaoIntranetService();
        ci.setOnSucceeded(workerStateEvent -> {
            Long resposta = ci.getValue();
            if (resposta != ConexaoIntranetService.NAO_CONECTADO) {
                try {
                    if (OSVerifier.isWindows()) {
                        Runtime.getRuntime().exec("cmd.exe /c start "+restartFileName,
                                null,
                                new File(LocalPaths.APP_DIR));
                    } else {
                        // Em Linux nao ha script .bat; apenas encerra a aplicacao.
                        // O reinicio pode ser feito externamente (ex.: script do SO).
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Platform.exit();
                    System.exit(0);
                }
            }
        });
        ci.start();
    }

}

