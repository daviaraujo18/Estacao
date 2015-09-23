/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Log;

/**
 *
 * @author Daniel Leite TJPI
 */
public class LocalPaths {
    
     public static String idePath, realPath; 
    
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
     public static void getPath()
     {
        //Apenas para pegar o path do EstacaoPonto.jar seja qual for o ambiente em execução
         
        File fileOrPathProjeto = new File("."); 
        try {
            realPath = fileOrPathProjeto.getCanonicalPath();
            
        if (realPath!= null)
        {
            if (realPath.contains("EstacaoPonto\\dist"))
            //executando do .bat no netbeans    
            {
                File jar = new File(realPath+"\\EstacaoPonto.jar");
                if (jar.exists())
                {
                    System.out.println("Arquivo existente no NetBeans.");
                }
                
            }
            else
            {
                if (realPath.contains("EstacaoPonto\\build\\dist"))
                //executando do .bat no intellij
                {
                    File jar = new File(realPath+"\\EstacaoPonto.jar");
                    if (jar.exists())
                    {
                        System.out.println("Arquivo existente no Intellij.");
                    }
                    
                }
                else
                {
                    if (realPath.endsWith("EstacaoPonto"))
                    //executando direto do netbeans ou no formato do ambiente de testes/produção.
                    {
                        File arquivosOuDiretorios[]= fileOrPathProjeto.listFiles();
                        File pathInNetBeans = new File(realPath + "\\dist");
                        File pathInIntellij = new File(realPath + "\\build");
                        int indexNet = -1;
                        int indexIntellij = -1;
                        int cont = 0;
                        for (File arquivo: arquivosOuDiretorios)
                        {
                            if (arquivo.getCanonicalPath().equals(pathInNetBeans.getCanonicalPath()))
                            {
                                indexNet = cont;
                            }
                            if (arquivo.getCanonicalPath().equals(pathInIntellij.getCanonicalPath()))
                            {
                                indexIntellij = cont;
                            }
                            cont++;
                        }
                        
                        if (indexNet != -1)
                        {
                            //executando direto do Netbeans 
                            realPath = arquivosOuDiretorios[indexNet].getCanonicalPath();
                            System.out.println("Executando do netbeans.");
                        }
                        else
                        {
                            if(indexIntellij != -1)
                            {
                                //executando direto do Intellij
                                realPath = arquivosOuDiretorios[indexIntellij].getCanonicalPath();
                                realPath+= "\\dist";
                                System.out.println("Executando do intellij.");
                            }
                        }
                        
                        System.out.println("realPath: "+realPath);
                        File jar = new File(realPath+"\\EstacaoPonto.jar");
                        if (jar.exists())
                        {
                            System.out.println("Arquivo existente.");
                        }
                       
                        
                    }
                }
            }
            
            
        }

//        File afile[] = file.listFiles(); 
//        int i = 0; 
//        for (int j = afile.length; i < j; i++) {
//            File arquivo = afile[i]; 
//             String realPath =null;
//             try {
//                 realPath = arquivo.getCanonicalPath();
//                 System.out.println("b: "+arquivo.getCanonicalPath());
//             } catch (IOException ex) {
//                 Logger.getLogger(LocalPaths.class.getName()).log(Level.SEVERE, null, ex);
//             }
//             if ((realPath!= null) && realPath.contains())
//             {
//                 
//             }

            
//            if (arquivo.get){
//            }
//        }
} catch (IOException ex) {
	Log.e(ex.getMessage());
            Logger.getLogger(LocalPaths.class.getName()).log(Level.SEVERE, null, ex);
        }
      

     } 
    
}
