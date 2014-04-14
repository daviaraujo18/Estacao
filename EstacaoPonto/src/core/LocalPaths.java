/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.File;

/**
 *
 * @author Daniel Leite TJPI
 */
public class LocalPaths {
    
     public static final String MIOLO_PATH_REGISTROS = "Estacao\\";
     public static final String MIOLO_PATH_IMGS = "Estacao\\imgs\\";
     public static final String MIOLO_PATH_LOG = "Estacao\\log\\";

     public static final String PATH_REGISTROS = getParticao()+"Estacao\\";
     public static final String PATH_CACHE = getParticao()+"Estacao\\imgs\\";
     public static final String PATH_LOG = getParticao()+"Estacao\\log\\";
     
     public static String getParticao()
     {
        if (new File("C:\\"+MIOLO_PATH_REGISTROS).canWrite())
        {
            return "C:\\";
        }
        File[] roots = File.listRoots();
        String caminho;
        for (File file : roots) {
            caminho =file.toString()+MIOLO_PATH_REGISTROS;
            File path = new File(caminho);
            boolean podeEscrever = path.canWrite();
            if (podeEscrever)
            {
               return file.toString();
            }
        }
        return null;
     }
    
}
