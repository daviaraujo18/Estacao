package utils;


import core.LocalPaths;
import javafx.application.Platform;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ScriptsBat {

    public static void init() throws IOException {

        createOrUpdateRunOpenUpdate();
        createOrUpdateRunReplace();

    }

    private static void createOrUpdateRunOpenUpdate() throws IOException {
        File c_estacao_estacaoponto = new File(LocalPaths.realPath);
        File runOpenUpdate_bat = new File(c_estacao_estacaoponto, "runOpenUpdate.bat");

        FileWriter fw = new FileWriter(runOpenUpdate_bat.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(
                "taskkill /f /im java.exe\n" +
                "taskkill /f /im javaw.exe\n" +
                "START C:\\Estacao\\EstacaoPonto\\EstacaoPonto.jar\n" +
                "exit 0"
        );
        bw.close();
    }

    private static void createOrUpdateRunReplace() throws IOException {
        File c_estacao_estacaoponto = new File(LocalPaths.realPath);
        File runReplace_bat = new File(c_estacao_estacaoponto, "runReplace.bat");

        FileWriter fw = new FileWriter(runReplace_bat.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(
                "chcp 1252\n" +
                "del \"EstacaoPonto.jar.bak\"\n" +
                "timeout 3\n" +
                "rename \"EstacaoPonto.jar\" \"EstacaoPonto.jar.bak\"\n" +
                "copy \"C:\\Estacao\\imgs\\EstacaoPonto.jar\" \"C:\\Estacao\\EstacaoPonto\" /y\n" +
                "cd \"C:\\Estacao\\EstacaoPonto\\\"\n" +
                "START java -jar \"C:\\Estacao\\EstacaoPonto\\EstacaoPonto.jar\"\n" +
                "exit 0"
        );
        bw.close();
    }

    public static void restartAplicacao() throws IOException {
        System.out.println("acessando: "+ LocalPaths.realPath+"\\runOpenUpdate.bat");
        Process p =  Runtime.getRuntime().exec("cmd.exe /c start C:\\Estacao\\EstacaoPonto\\runOpenUpdate.bat",
                null,
                new File(LocalPaths.realPath));
        Platform.exit();
        System.exit(0);
    }

    public static void updateAplicacao() throws IOException {
        System.out.println("acessando: "+ LocalPaths.realPath+"\\runReplace.bat");
        Process p =  Runtime.getRuntime().exec("cmd.exe /c start C:\\Estacao\\EstacaoPonto\\runReplace.bat",
                null,
                new File(LocalPaths.realPath));

        System.out.println("Download finalizado. Abrindo nova versão.");
        Platform.exit();
        System.exit(0);
    }

}

