package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
    public static String ler() throws FileNotFoundException, IOException
    {
        FileReader fileReader = new FileReader(arquivo);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String conteudo="";
        String linha = "";
        while ( ( linha = bufferedReader.readLine() ) != null) {
            conteudo+=linha;
        }
 
        //liberamos o fluxo dos objetos 
        // ou fechamos o arquivo
        fileReader.close();
        bufferedReader.close();
        return conteudo;
    }
    public static void limparArquivo() throws IOException
    {
            FileWriter fileWriter = new FileWriter(arquivo, false);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println("");
            printWriter.flush();
            printWriter.close();
    }
}
