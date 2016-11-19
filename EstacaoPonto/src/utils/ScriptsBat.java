package utils;


import controllers.MainController;
import core.LocalPaths;
import javafx.application.Platform;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

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
        bw.write("taskkill /f /im java.exe");
        bw.newLine();
        bw.write("taskkill /f /im javaw.exe");
        bw.newLine();
        bw.write("START C:\\Estacao\\EstacaoPonto\\EstacaoPonto.jar");
        bw.newLine();
        bw.write("exit 0");
        bw.newLine();

        bw.close();
    }

    private static void createOrUpdateRunReplace() throws IOException {
        File c_estacao_estacaoponto = new File(LocalPaths.realPath);
        File runReplace_bat = new File(c_estacao_estacaoponto, "runReplace.bat");

        FileWriter fw = new FileWriter(runReplace_bat.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("chcp 1252");
        bw.newLine();
        bw.write("del \"EstacaoPonto.jar.bak\"");
        bw.newLine();
        bw.write("timeout 3");
        bw.newLine();
        bw.write("rename \"EstacaoPonto.jar\" \"EstacaoPonto.jar.bak\"");
        bw.newLine();
        bw.write("copy \"C:\\Estacao\\imgs\\EstacaoPonto.jar\" \"C:\\Estacao\\EstacaoPonto\" /y");
        bw.newLine();
        bw.write("cd \"C:\\Estacao\\EstacaoPonto\\\"");
        bw.newLine();
        bw.write("START java -jar \"C:\\Estacao\\EstacaoPonto\\EstacaoPonto.jar\"");
        bw.newLine();
        bw.write("exit 0");
        bw.newLine();

        bw.close();
    }

    public static void restartAplicacao() throws IOException {
        Calendar dataServidorAtual = MainController.INSTANCE.getThreadRelogio().getDataServidorAtual();
        System.out.println("["+CalendarUtils.format(dataServidorAtual)+"] Reiniciando aplicacao: "+ LocalPaths.realPath+"\\runOpenUpdate.bat");
        Process p =  Runtime.getRuntime().exec("cmd.exe /c start C:\\Estacao\\EstacaoPonto\\runOpenUpdate.bat",
                null,
                new File(LocalPaths.realPath));
        Platform.exit();
        System.exit(0);
    }

    public static void updateAplicacao() throws IOException {
        Calendar dataServidorAtual = MainController.INSTANCE.getThreadRelogio().getDataServidorAtual();
        System.out.println("["+CalendarUtils.format(dataServidorAtual)+"] Update aplicacao: "+ LocalPaths.realPath+"\\runOpenUpdate.bat");
        Process p =  Runtime.getRuntime().exec("cmd.exe /c start C:\\Estacao\\EstacaoPonto\\runReplace.bat",
                null,
                new File(LocalPaths.realPath));

        System.out.println("Download finalizado. Abrindo nova versão.");
        Platform.exit();
        System.exit(0);
    }

}

