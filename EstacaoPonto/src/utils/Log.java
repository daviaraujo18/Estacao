/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import controllers.MainController;
import core.Configuracoes;
import core.LocalPaths;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Calendar;

/**
 *
 * @author Anderson Soares
 */
public class Log {
    public static final String LOG_NAME_BEGIN = "LOG_";
    private static String sData;
    public static boolean saidaEmArquivo;

    
    public static void i(Object msg) {
        System.out.println("[LOG-INFO] "+msg.toString());
    }

    public static void e(Object msg) {
        System.out.println("[LOG-ERROR] "+msg.toString());
    }
    
    public static void saidaEmArquivo()
    {
        //saída em arquivo
        if (saidaEmArquivo)
        {
            //utilizando data local para nome de log enquanto não carrega a página PontoDePresenca.jsp
            Calendar data = Calendar.getInstance();
            sData = "_localTime"+buildFileSimpleName(data);
            criaArquivoSetaSaida();

        }
    }
    public static void criaArquivoSetaSaida()
    {
            File saida = new File(LocalPaths.PATH_LOG+LOG_NAME_BEGIN+ Configuracoes.app_name.get() + sData);
            File dir  = saida.getParentFile();
            dir.mkdirs();
            PrintStream psSaida;
            try {
                saida.createNewFile();

                OutputStream outStream = new FileOutputStream(saida,true);
                psSaida = new PrintStream(outStream);
                System.setOut(psSaida);
                System.setErr(System.out);
            } catch (FileNotFoundException ex) {
                Log.e(ex);
            }
            catch (IOException ex) {
                Log.e(ex);
            }
    }
    public static void atualizarDataLog()
    {
        if(saidaEmArquivo)
        {
            String sDataNew = buildFileSimpleName((Calendar) MainController.INSTANCE.getThreadRelogio().getDataServidorAtual().clone());
            if (sDataNew!=null && !sDataNew.isEmpty()&& !sData.equals("_localTime"+sDataNew))
            {
                sData = sDataNew;
                criaArquivoSetaSaida();
            }
        }
    }
    public static String buildFileSimpleName(Calendar data) {
        String mes = adicionaZero(data.get(Calendar.MONTH)+1);
        String dia = adicionaZero(data.get(Calendar.DAY_OF_MONTH));
        int ano = data.get(Calendar.YEAR);
        String fileSimpleName = "_"+ano+mes+dia+".txt";
        return fileSimpleName;
    }

    private static String adicionaZero(int num) {
        String sNum = ""+num;
        if (num<10)
        {
            sNum = "0"+sNum;
        }
        return sNum;
    }
}
