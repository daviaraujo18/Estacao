package utils;

import core.LocalPaths;
import core.RegistroWindows;
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

    private static File arquivo = new File( LocalPaths.PATH_REGISTROS+RegistroWindows.getCodigoUnicoMaquina().substring(2, 10)+".txt");
    
    public static boolean escrever(String registro) throws IOException {
        if(registro == null)
        {
            return false;
        }
        try {
            String dadosArquivo = ler();
            System.out.println("ARQUIVO LIDO: " + dadosArquivo);
            String dadosDescriptografados=CryptoUtils.decryptDES("cryp:gpf", dadosArquivo);
            if(dadosDescriptografados == null)
            {
                dadosDescriptografados="";
            }
            System.out.println("ARQUIVO LIDO DESCRIPTOGRAFADO: " + dadosDescriptografados);
            dadosDescriptografados = dadosDescriptografados + registro;
            FileWriter fileWriter = new FileWriter(arquivo, false);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            String registroCriptografado = CryptoUtils.encryptDES("cryp:gpf", dadosDescriptografados);
            System.out.println("REGISRO: " + registro);
            //System.out.println("REGISRO CRIPTOGRAFADO: " + registroCriptografado);
            printWriter.println(registroCriptografado);
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
        if(conteudo!=null && !conteudo.isEmpty())
        {
            System.out.println("CONTEUDO DO ARQUIVO: " + conteudo);
            String conteudoDes = CryptoUtils.decryptDES("cryp:gpf", conteudo);
            System.out.println("CONTEUDO DESCRIPTOGRAFADO DO ARQUIVO: " + conteudoDes);
        }
        else
        {
            conteudo = "";
        }
        return conteudo;
    }
    
    public static String lerArquivo() throws FileNotFoundException, IOException {
        String separador = ";";
        FileReader fileReader = new FileReader(arquivo);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String conteudo = "";
        String linha = "";
        while ((linha = bufferedReader.readLine()) != null) {
            if(linha!=null && !linha.isEmpty())
            {
                conteudo+=linha;
                conteudo+=separador;
            }
        }
        if(!conteudo.isEmpty())
        {
            conteudo = conteudo.substring(0, conteudo.length()-1);
            System.out.println("\n -- Dados arquivo: " + conteudo);
        }
        return conteudo;
    }
    
    public static boolean escreverRegistro(String registro)
    {
        if(registro == null)
        {
            return false;
        }
        try {
            
            FileWriter fileWriter = new FileWriter(arquivo, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            String registroCriptografado = CryptoUtils.encryptDES("cryp:gpf", registro);
            printWriter.println(registroCriptografado);
            printWriter.flush();
            printWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }        
    }
    
    public static void limparArquivo() throws IOException
    {
            FileWriter fileWriter = new FileWriter(arquivo, false);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            //printWriter.println("");
            printWriter.flush();
            printWriter.close();
    }
    }
