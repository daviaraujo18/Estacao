package utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 *
 * @author Jainilene
 */
public class VerificaConexao {
    
    public static boolean verificaConexao(String sUrl) throws MalformedURLException, IOException
	{
           
		try {
               // URL do destino escolhido
               //URL url = new URL("http://www.yahoo.com");
               URL url = new URL(sUrl);

               // abre a conexão
               HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();

               // tenta buscar conteúdo da URL
               // se não tiver conexão, essa linha irá falhar
               Object objData = urlConnect.getContent();

           } catch (UnknownHostException e) {
                    System.out.println("Excecao:: UnknownHostException");
               //e.printStackTrace();
               return false;
           }
           catch (IOException e) {
               return false;
           }
           return true;
	}

    
}
