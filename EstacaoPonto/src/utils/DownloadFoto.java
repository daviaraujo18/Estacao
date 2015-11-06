/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import core.LocalPaths;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;
/**
 *
 * @author Daniel Leite TJPI
 */
public class DownloadFoto {
 
    public static boolean baixaFoto(String enderecoWeb) 
    {

        boolean baixou = false;
        System.out.println("Tentado baixar: "+enderecoWeb);
        BufferedInputStream in = null;
        ByteArrayOutputStream bais = null;
        //FileOutputStream fout = null;
       long startTempo = 0; 

        try
        {
            String nomeArquivo = FilenameUtils.getBaseName(enderecoWeb);

            File localFile = new File(LocalPaths.PATH_CACHE+nomeArquivo); 
            File dir = localFile.getParentFile();
            dir.mkdirs();
           
            URL url = new URL(enderecoWeb);
            URLConnection  conn = url.openConnection();
            conn.setConnectTimeout(50);
            conn.setReadTimeout(50);

            startTempo = System.currentTimeMillis(); 
            in = new BufferedInputStream(conn.getInputStream());
            
                     
             bais = new ByteArrayOutputStream();
            
            
            final byte data[] = new byte[1024];
            int count;
            
            while ((count = in.read(data, 0, 1024)) != -1) 
            {    
                bais.write(data, 0, count);
            }   
            
            byte bFoto[] = bais.toByteArray();


            String dataURI = javax.xml.bind.DatatypeConverter.printBase64Binary(bFoto);
            
            FileWriter fileWriter = new FileWriter(localFile, false);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(dataURI);
            printWriter.flush();
            printWriter.close(); 

            baixou=true;
        }
        catch(Exception ex)
        {
			Log.e(ex);
            System.out.println("Arquivo inexistente ou servidor de imagens desligado.");
            return false;
        }
        finally 
        {
            long fim = System.currentTimeMillis(); 
            long resulta = (fim-startTempo);

//            System.out.println("delta T: "+(fim-startTempo));
            
            if (in != null) 
            {
                    try {
                        in.close();
                    } catch (IOException ex) {
						Log.e(ex);
                        Logger.getLogger(DownloadFoto.class.getName()).log(Level.SEVERE, null, ex);
                    }
            }
            if (bais != null) {
                    try {
                        bais.close();
                    } catch (IOException ex) {
                        Logger.getLogger(DownloadFoto.class.getName()).log(Level.SEVERE, null, ex);
                    }
            }
        }
        return baixou;
    }

}
