package core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class GerarConfig {


    public static void init() throws IOException {
        createConfigProperties();
    }

    private static void createConfigProperties() throws IOException {
        File c_estacao_estacaoponto = new File(LocalPaths.realPath);
        File runOpenUpdate_bat = new File(c_estacao_estacaoponto, "config.properties");

        FileWriter fw = new FileWriter(runOpenUpdate_bat.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("app_name=ESTACAOPONTO");
        bw.newLine();
        bw.write("base_intranet_url=http://www.tjpi.jus.br/intranet");
        bw.newLine();
        bw.write("#base_intranet_url=http://10.254.0.6:8087/presenca");
        bw.newLine();
        bw.write("#base_intranet_url=http://10.254.0.6:8086/intranet");
        bw.newLine();
        bw.write("tela_cheia=true");
        bw.newLine();
        bw.write("bloqueio_tela=false");
        bw.newLine();
        bw.write("# 1-9");
        bw.newLine();
        bw.write("nivel_seguranca_leitor=9");
        bw.newLine();
        bw.write("baixa_foto=false");
        bw.newLine();

        bw.close();
    }

    private static void updateBaseIntranetUrl(String baseIntranetUrl) throws IOException {
        File c_estacao_estacaoponto = new File(LocalPaths.realPath);
        File runOpenUpdate_bat = new File(c_estacao_estacaoponto, "config.properties");

        FileWriter fw = new FileWriter(runOpenUpdate_bat.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("app_name=ESTACAOPONTO");
        bw.newLine();
        bw.write("base_intranet_url=http://www.tjpi.jus.br/intranettt");
        bw.newLine();
        bw.write("#base_intranet_url=http://10.254.0.6:8087/presenca");
        bw.newLine();
        bw.write("#base_intranet_url=http://10.254.0.6:8086/intranet");
        bw.newLine();
        bw.write("tela_cheia=true");
        bw.newLine();
        bw.write("bloqueio_tela=false");
        bw.newLine();
        bw.write("# 1-9");
        bw.newLine();
        bw.write("nivel_seguranca_leitor=9");
        bw.newLine();
        bw.write("baixa_foto=false");
        bw.newLine();

        bw.close();
    }
}
