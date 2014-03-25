/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.util.Iterator;
import java.util.Map;
import utils.CacheManipulation;

/**
 *
 * @author Daniel Leite TJPI
 */
public class CacheDownloadService {
   
    public static void downloadAndCacheFotos(Map<Integer,String> mapaIdFotosFrequentadores)
    {
        int numTotal = mapaIdFotosFrequentadores.size();
        Iterator it = mapaIdFotosFrequentadores.entrySet().iterator();
        System.out.println("Iniciando download das fotos...");
        int numAtual = 1;

        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            String enderecoWeb = pairs.getValue().toString();
            System.out.println("Endereço Web: "+enderecoWeb);
            System.out.print("Baixando "+numAtual+" de "+numTotal+". ");
            if (!CacheManipulation.searchAndEdit(enderecoWeb))
            {
                CacheManipulation.insert(enderecoWeb);
            }

            it.remove(); // avoids a ConcurrentModificationException
            numAtual++;
        }
    }
}
