/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
/**
 *
 * @author Daniel Leite TJPI
 */
public class DownloadFoto {
 
    public static boolean baixaFoto(String enderecoWeb) 
    {
        boolean baixou = false;
        HttpGet httpGet = new HttpGet(enderecoWeb);
        
        String nomeArquivo = FilenameUtils.getBaseName(enderecoWeb);
        nomeArquivo = nomeArquivo +"."+ FilenameUtils.getExtension(enderecoWeb);
        
        HttpClient httpClient=new DefaultHttpClient();
        
        try
        {
            HttpResponse response = httpClient.execute(httpGet);
            int code = response.getStatusLine().getStatusCode();
            if (code==200)
            {

                InputStream remoteContentStream = response.getEntity().getContent();
                long fileSize = response.getEntity().getContentLength();

                File localFile = new File("C:\\Estacao\\imgs\\"+nomeArquivo);
                File dir = localFile.getParentFile();
                dir.mkdirs();

                OutputStream localFileStream;
                localFileStream = new FileOutputStream(localFile);
                int bufferSize =1024;
                byte[] buffer = new byte[bufferSize];
                int sizeOfChunk;
                int amountComplete = 0;
                while ((sizeOfChunk = remoteContentStream.read(buffer)) != -1)
                {
                    localFileStream.write(buffer, 0, sizeOfChunk);
                    amountComplete += sizeOfChunk;
                    updateProgress(amountComplete, fileSize);
                }
                System.out.println("filesize: "+fileSize);
                System.out.println("localFile.length(): "+localFile.length());
                
                if (localFile.length()==fileSize)
                {
                    baixou=true;
                }
                localFileStream.close();
            }
            else
            {
                System.out.println("code: "+code);
            }
            
            
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return baixou;
    }

    

    private static void updateProgress(int amountComplete, long fileSize) {
        System.out.println("Completado "+amountComplete+" de "+fileSize+".");
    }
}
