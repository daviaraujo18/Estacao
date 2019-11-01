package utils;

import core.LocalPaths;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Map;

public class ArquivoUtils {

    public static void saveFile(String arquivo, String conteudo) throws IOException {

        File file = new File(LocalPaths.PATH_DATA,arquivo);
        FileOutputStream writer = new FileOutputStream(file);
        writer.write(conteudo.getBytes());
        writer.close();
    }

    public static String readFile(String arquivo) throws IOException {
        File file = new File(LocalPaths.PATH_DATA,arquivo);
        FileInputStream inputStream = new FileInputStream(file);
        String myHash = IOUtils.toString(inputStream);
        return myHash;
    }

    public static void saveMapOnFile(Map<Integer, String> mapa, String arquivo) throws IOException {
        File file = new File(LocalPaths.PATH_DATA,arquivo);
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file))) {
            os.writeObject(mapa);
        }
    }

    public static Map<Integer, String> readMapOnFile(String arquivo) throws ClassNotFoundException, IOException {
        File file = new File(LocalPaths.PATH_DATA,arquivo);
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<Integer, String>) is.readObject();
        }
    }
}
