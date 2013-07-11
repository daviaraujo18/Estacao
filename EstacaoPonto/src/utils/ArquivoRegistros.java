package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Jainilene
 */
public class ArquivoRegistros {

    private static File arquivo = new File( "C:\\RegistroBatimentos\\registros.txt");
    public static boolean escrever(String registro) throws IOException {
        try {
            FileWriter fileWriter = new FileWriter(arquivo, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(registro);
            printWriter.flush();
            printWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
