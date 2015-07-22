
package core;

import utils.Log;

/**
 * Classe que contem url's do sistema intranet predefinidas
 *
 * @author aers
 */
public class IntranetURLs {
    
    private static String CODIGOS = "?codigoAtivacao="+
            RegistroWindows.getCodigoAtivacaoRegistro()+
            "&codigoUnicoMaquina="+RegistroWindows.getCodigoUnicoMaquina();

    public static final String BASE_URL = Configuracoes.base_intranet_url.get();
    public static final String INICIALIZAR_PONTO = BASE_URL+"/presenca/InicializarPonto";
    public static final String CADASTRO_FREQUENTADOR = BASE_URL+"/presenca/Frequentador?type=create";
    public static final String BATIMENTO_PONTO = BASE_URL+"/presenca/PontoDePresenca";
    public static final String INICIAR_PONTO = BASE_URL+"/presenca/IniciarPonto";
    //    public static final String URL_UPDATE = "http://teste.tjpi.jus.br/intranet/uploads_producao/tjpi/upload_presenca/EstacaoPonto.jar";//producao
    //    public static final String URL_UPDATE_ALL = "http://teste.tjpi.jus.br/intranet/uploads_producao/tjpi/upload_presenca/versao/";//producao 
    public static String URL_UPDATE = BASE_URL+"/uploads/tjpi/upload_presenca/EstacaoPonto.jar";//teste
    public static String URL_UPDATE_ALL = BASE_URL+"/uploads/tjpi/upload_presenca/versao/";//teste
    
//    public static  String URL_UPDATE= "http://localhost/intranet_uploads/tjpi/upload_presenca/EstacaoPonto.jar";//desenvolvimento
//    public static  String URL_UPDATE_ALL = "http://localhost/intranet_uploads/tjpi/upload_presenca/versao/";//desenvolvimento
    public static String PROBLEMA_REGISTRO=BASE_URL+"/presenca/ProblemaRegistro";;
    
    public static String getCodigos() {
        String codAtivacao = RegistroWindows.getCodigoAtivacaoRegistro();
        String codUnicoM = RegistroWindows.getCodigoUnicoMaquina();
        Log.i(codUnicoM);
        return "?codigoAtivacao="+ codAtivacao+"&codigoUnicoMaquina="+codUnicoM;
    }
    public static void init()
    {

        if (EstacaoPonto.ambiente.equals("desenvolvimento"))
        {
            URL_UPDATE= "http://localhost/intranet_uploads/tjpi/upload_presenca/EstacaoPonto.jar";//desenvolvimento
            URL_UPDATE_ALL = "http://localhost/intranet_uploads/tjpi/upload_presenca/versao/";//desenvolvimento
        }
        else
        {
                 String uploadPath = BASE_URL;
                uploadPath = uploadPath.replace(":8087/presenca", "/intranet");
            if (EstacaoPonto.ambiente.equals("teste"))
            {
;
                URL_UPDATE = uploadPath+"/uploads/tjpi/upload_presenca/EstacaoPonto.jar";//teste 
                URL_UPDATE_ALL = uploadPath+"/uploads/tjpi/upload_presenca/versao/";//teste
            }
            else
            {
                if (EstacaoPonto.ambiente.equals("producao"))
                {
                    URL_UPDATE = uploadPath+"/uploads/tjpi/upload_presenca/EstacaoPonto.jar";//producao
                    URL_UPDATE_ALL = uploadPath+"/uploads/tjpi/upload_presenca/versao/";//producao 
                }
            }
        }
        
    }
       
}