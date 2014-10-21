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

            System.out.println("Executando runReplace.bat");
            Process p =  Runtime.getRuntime().exec("cmd.exe /c start runReplace.bat",null,new File(LocalPaths.realPath) );

            System.out.println("Download finalizado. Abrindo nova versão.");
            Platform.exit();
            System.exit(0);
        }
        catch (Exception ex)
        {
            System.out.println("erro");
            ex.printStackTrace();
        }
        System.out.println("Fim de atualizarEstacao.");
   }
   
   public static void downloadNovaVersao(String fileName)
   {
        if (fileName!= null && !fileName.isEmpty())
        {
            String url = IntranetURLs.URL_UPDATE_ALL+fileName+"/EstacaoPonto.jar";
            download(url);
        }
        else
        {
            String url = IntranetURLs.URL_UPDATE;
            download(url);
        }
   }
   public static void verificaVersoes(String ultimaVersaoBD)
   {
       if (ultimaVersaoBD!=null && !ultimaVersaoBD.isEmpty() && !ultimaVersaoBD.equals(EstacaoPonto.getInstance().versao))
       {
            System.out.println("versaoBD: "+ultimaVersaoBD+" versaoEP: "+EstacaoPonto.getInstance().versao);
            System.out.println("Estação deve ser atualizada.");
            downloadNovaVersao(ultimaVersaoBD);
       }
   }
}
