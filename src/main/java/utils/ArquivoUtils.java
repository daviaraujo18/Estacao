package utils;

import core.LocalPaths;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Map;

public class ArquivoUtils {

    public static void saveStringOnFile(String conteudo, File arquivo) throws IOException {
        FileOutputStream writer = new FileOutputStream(arquivo);
        writer.write(conteudo.getBytes());
        writer.close();
    }

    public static String readStringOnFile(File arquivo) throws IOException {
        FileInputStream inputStream = new FileInputStream(arquivo);
        String str = IOUtils.toString(inputStream);
        return str;
    }

    public static void saveMapOnFile(Map<Integer, String> mapa, File arquivo) throws IOException {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(arquivo))) {
            os.writeObject(mapa);
        }
    }

    public static Map<Integer, String> readMapOnFile(File arquivo) throws ClassNotFoundException, IOException {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(arquivo))) {
            return (Map<Integer, String>) is.readObject();
        }
    }
}
