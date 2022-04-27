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

import org.apache.commons.io.FileUtils;
import utils.ArquivoUtils;
import utils.LogAplicacao;

/**
 *
 * @author Daniel Leite TJPI
 */
public class LocalPaths {

    public static  final String APP_DIR = Paths.get(System.getProperty("user.home")).resolve("AppData").resolve("Local").resolve("TJPI").resolve("EstacaoPonto").toString();
    public static final String INSTALL_DIR = RegistroWindows.getInstallDir();

    public static final String PATH_REGISTROS = APP_DIR+"\\";
    public static final String PATH_LOG = APP_DIR+"\\log\\";
    public static final String PATH_DATA = APP_DIR+"\\data\\";
    public static final String PATH_CACHE = PATH_DATA+"\\imgs\\";

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
                    "base_intranet_url=https://www.tjpi.jus.br/intranet\n" +
                    "tela_cheia=true\n" +
                    "bloqueio_tela=false\n" +
                    "# 1-9\n" +
                    "nivel_seguranca_leitor=8\n" +
                    "baixa_foto=false");
            ArquivoUtils.saveStringOnFile(sb.toString(), configFile);
        }

    }

    public static void moverDiretorioAntigo() {
        String antigo = "C:/Estacao";
        File dirAntigo = new File(antigo);

        LogAplicacao.i("Iniciando");
        if (dirAntigo.isDirectory() && dirAntigo.exists()) {

            String novo = APP_DIR + "\\old";
            File dirNovo = new File(novo);

            try {
                LogAplicacao.i("============ movendo diretorio antigo =============");
                FileUtils.moveDirectory(dirAntigo, dirNovo);
            } catch (IOException e) {
                LogAplicacao.e("Nao foi possivel mover diretorio antigo");
                LogAplicacao.e(e.getMessage());
            }

        }
    }
}
