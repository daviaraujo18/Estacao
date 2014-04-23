package registrowindows;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aersandersonsoares
 */
public class RegistroWindows {
    public static final String lerRegistro(String strLocalizacao, String strChave){
        StringWriter stringWriter = new StringWriter();
        InputStream inputStream;
        String strResultadoBusca;
        int i;

        try {
            //Roda reg query, depois lê a saída com StreamReader (classe interna)
            Process process = Runtime.getRuntime().exec("reg query \"" +
                    strLocalizacao + "\" /v " + strChave);

            inputStream = process.getInputStream();
            while ((i = inputStream.read()) != -1)
                stringWriter.write(i);

            //strResultadoBusca contém o resultado completo da busca
            //(localização, chave, tipo do registro e valor).
            strResultadoBusca = stringWriter.toString();

            //Imprime o resultado completo da busca. Caso necessite de todos os
            //campos basta retornar essa variável (strResultadoBusca).
            Log.i(strResultadoBusca);

            //Verifica qual o caracter utilizado para separação dos campos.
            //Win 7 = " " e Win XP = "\t"

            if (System.getProperty("os.name").equals("Windows 7")) {
                if(strResultadoBusca.contains(" ")){
                    //Separa os campos em um vetor de strings.
                    String[] camposRegistro = strResultadoBusca.split(" ");
                    strResultadoBusca = camposRegistro[camposRegistro.length-1];
                } else {
                    strResultadoBusca = null;
                }
            } else {
                if(strResultadoBusca.contains("\t")){
                    //Separa os campos em um vetor de strings.
                    String[] camposRegistro = strResultadoBusca.split("\t");
                    strResultadoBusca = camposRegistro[camposRegistro.length-1];
                } else {
                    strResultadoBusca = null;
                }
            }
            return strResultadoBusca;
        } catch (Exception e) {
            return null;
        }
    }

    public static final void incluirRegistro(String strLocalizacao, String strChave,
            String strTipo, String strValor){
        try {
            Process process = Runtime.getRuntime().exec("reg add " +
                    strLocalizacao + " /v " + strChave + " /t " + strTipo + " /d " + strValor);
        } catch (IOException ex) {
            Logger.getLogger(RegistroWindows.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        String registro = RegistroWindows.lerRegistro("HKCU\\Software\\Microsoft\\"
                + "Windows\\CurrentVersion\\Run", "TesteInclusaoChaveRegistro");

        RegistroWindows.incluirRegistro("HKCU\\Software\\Microsoft\\Windows\\"
                + "CurrentVersion\\Run", "TesteInclusaoChaveRegistro", "REG_SZ", "Teste");

        if (registro == null) {
            Log.i("O registro solicitado não foi encontrado.");
        } else {
            Log.i(registro);
        }
    }
}
