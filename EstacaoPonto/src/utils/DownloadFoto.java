/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.*;
import java.net.URL;
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
       
        try
        {
            String nomeArquivo = FilenameUtils.getBaseName(enderecoWeb);
     //       nomeArquivo = nomeArquivo +"."+ FilenameUtils.getExtension(enderecoWeb);

            File localFile = new File("C:\\Estacao\\imgs\\"+nomeArquivo); 
            File dir = localFile.getParentFile();
            dir.mkdirs();
           
            URL url = new URL(enderecoWeb);
            in = new BufferedInputStream(url.openStream());
            
           
             
            
           // fout = new FileOutputStream(localFile);
             bais = new ByteArrayOutputStream();
            
            
            final byte data[] = new byte[1024];
            int count;
            
            while ((count = in.read(data, 0, 1024)) != -1) 
            {    
                bais.write(data, 0, count);
               // fout.write(data, 0, count);
            }   
            
            byte bFoto[] = bais.toByteArray();


//            MessageDigest md = MessageDigest.getInstance("MD5");
//            byte[] thedigest = md.digest(bFoto);
//            thedigest.toString();
//            
            String dataURI = javax.xml.bind.DatatypeConverter.printBase64Binary(bFoto);
            
            FileWriter fileWriter = new FileWriter(localFile, false);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(dataURI);
            printWriter.flush();
            printWriter.close(); 

            baixou=true;
        }
        catch(Exception e)
        {
            System.out.println("Arquivo inexistente ou servidor de imagens desligado.");
            return false;
        }
        finally 
        {
            if (in != null) 
            {
                    try {
                        in.close();
                    } catch (IOException ex) {
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
