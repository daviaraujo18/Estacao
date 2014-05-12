/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import core.Configuracoes;
import core.EstacaoPonto;
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
    public static void i(Object msg) {
        System.out.println("[LOG-INFO] "+msg.toString());
    }

    public static void e(Object msg) {
        System.out.println("[LOG-ERROR] "+msg.toString());
    }

    public static void saidaEmArquivo(boolean saidaEmArquivo)
    {
        //saída em arquivo
        if (saidaEmArquivo)
        {
            Calendar data = Calendar.getInstance();
            String sData = buildFileSimpleName(data);
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
