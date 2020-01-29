/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import core.EstacaoPonto;
import core.IntranetURLs;
import core.LocalPaths;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import javafx.application.Platform;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Daniel Leite TJPI
 */
public class AtualizarEstacao {

   public static void downloadNovaVersaoIndividual()
   {
        String url = IntranetURLs.URL_UPDATE;
        download(url);
   }
   
   public static void downloadNovaVersaoAll(String fileName)
   {
        String url = IntranetURLs.URL_UPDATE_ALL+fileName+"/EstacaoPonto.jar";
        download(url);
   }
   public static void download(String url)
   {
     String nomeArquivo = FilenameUtils.getName(url);//getBaseName(url)+"."+FilenameUtils.getExtension(url);
        String pathArquivo = LocalPaths.PATH_CACHE+nomeArquivo;

        File localFile = new File(pathArquivo);
        File dir = localFile.getParentFile();
        dir.mkdirs();

        URL link;
        try
        {
            link = new URL(url);
            InputStream in = new BufferedInputStream(link.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1!=(n=in.read(buf)))
            {
               out.write(buf, 0, n);
            }
            out.close();
            in.close();
            byte[] response = out.toByteArray();

            FileOutputStream fos = new FileOutputStream(pathArquivo);
            fos.write(response);
            fos.close();

            LogAplicacao.i("Executando runReplace.bat");
            ScriptsBat.updateAplicacao();
            Platform.exit();
            System.exit(0);
        }
        catch (Exception ex)
        {
			LogAplicacao.e(ex);
            LogAplicacao.e("Nao foi possivel baixar a nova versao. Endereco: "+url);
        }
   }
   
   public static void downloadNovaVersao(String versao)
   {
        String url = "";
        boolean is64bit = false;
        if (System.getProperty("os.name").contains("Windows")) {
            is64bit = (System.getenv("ProgramFiles(x86)") != null);
        } else {
            is64bit = (System.getProperty("os.arch").indexOf("64") != -1);
        }
        if (versao!= null && !versao.isEmpty())
        {
            if(is64bit)
            {
                url = IntranetURLs.URL_UPDATE_ALL+versao+"/X64/EstacaoPonto.jar";
            }
            else
            {
                url = IntranetURLs.URL_UPDATE_ALL+versao+"/X86/EstacaoPonto.jar";
            }
        }
        else
        {
            url = IntranetURLs.URL_UPDATE;
        }
        download(url);
   }
   public static void verificaVersoes(String ultimaVersaoBD)
   {
       if (ultimaVersaoBD!=null && !ultimaVersaoBD.isEmpty() && !ultimaVersaoBD.equals(EstacaoPonto.getInstance().versao))
       {
           LogAplicacao.i("versaoBD: "+ultimaVersaoBD+" versaoEP: "+EstacaoPonto.getInstance().versao);
           LogAplicacao.i("Estacao deve ser atualizada.");
           downloadNovaVersao(ultimaVersaoBD);
       }
   }
}
