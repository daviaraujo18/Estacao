/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.ArquivoUtils;
import utils.LogAplicacao;

/**
 *
 * @author Daniel Leite TJPI
 */
public class LocalPaths {

    public static String APP_DIR = Paths.get(System.getProperty("user.home")).resolve("AppData").resolve("Local").resolve("TJPI").resolve("EstacaoPonto").toString();
//    public static String APP_DIR = System.getenv("LOCALAPPDATA")+"\\TJPI\\EstacaoPonto\\";

    public static String idePath, realPath;

    public static final String MIOLO_PATH_REGISTROS = "Estacao\\";

    public static final String PATH_REGISTROS = APP_DIR;
    public static final String PATH_LOG = APP_DIR+"\\log\\";
    public static final String PATH_DATA = APP_DIR+"\\data\\";
    public static final String PATH_CACHE = PATH_DATA+"\\imgs\\";

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
        //Apenas para pegar o path do EstacaoPonto.jar seja qual for o ambiente em execu��o

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
                        LogAplicacao.i("Arquivo existente no NetBeans.");
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
                            LogAplicacao.i("Arquivo existente no Intellij.");
                        }

                    }
                    else
                    {
                        if (realPath.endsWith("EstacaoPonto"))
                        //executando direto do netbeans ou no formato do ambiente de testes/produ��o.
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
                                LogAplicacao.i("Executando do netbeans.");
                            }
                            else
                            {
                                if(indexIntellij != -1)
                                {
                                    //executando direto do Intellij
                                    realPath = arquivosOuDiretorios[indexIntellij].getCanonicalPath();
                                    realPath+= "\\dist";
                                    LogAplicacao.i("Executando do intellij.");
                                }
                            }

                            LogAplicacao.i("RealPath: "+realPath);
                            File jar = new File(realPath+"\\EstacaoPonto.jar");
                            if (jar.exists())
                            {
                                LogAplicacao.i("Arquivo existente.");
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
            LogAplicacao.e(ex);
            Logger.getLogger(LocalPaths.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    public static void createDirs() {
        java.io.File appDir = new java.io.File(LocalPaths.APP_DIR);
        java.io.File data = new java.io.File(LocalPaths.PATH_DATA);
        java.io.File cache = new java.io.File(LocalPaths.PATH_CACHE);
        java.io.File log = new java.io.File(LocalPaths.PATH_LOG);

        appDir.mkdir();
        data.mkdir();
        cache.mkdir();
        log.mkdir();
    }

    public static void checarArquivos() throws IOException {
        File dataDb = new File(LocalPaths.PATH_DATA,"data.db");

        if (!dataDb.exists()) {
            ArquivoUtils.saveStringOnFile("",new File(PATH_DATA, "hash"));
        }

        File configFile = new File(APP_DIR, "config.properties");
        if (!configFile.exists()) {
            LogAplicacao.i("Criando config.properties");
            StringBuilder sb = new StringBuilder("");
            sb.append("app_name=ESTACAOPONTO\n" +
                    "base_intranet_url=http://www.tjpi.jus.br/intranet\n" +
                    "tela_cheia=true\n" +
                    "bloqueio_tela=false\n" +
                    "# 1-9\n" +
                    "nivel_seguranca_leitor=8\n" +
                    "baixa_foto=false");
            ArquivoUtils.saveStringOnFile(sb.toString(), configFile);
        }

    }

}
