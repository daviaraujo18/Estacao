
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
    
	
    public static final String BASE_URL = "http://127.0.0.1:8080/intranet";
    //public static final String BASE_URL = "http://10.254.3.6:8087/presenca";

    public static final String INICIALIZAR_PONTO = BASE_URL+"/tjpi/presenca/InicializarPonto";
    //public static String INICIALIZAR_PONTO_COM_CODIGOS = BASE_URL+"/tjpi/presenca/InicializarPonto"+getCodigos();
    
    public static final String CADASTRO_FREQUENTADOR = BASE_URL+"/tjpi/presenca/Frequentador?type=create";
    public static final String BATIMENTO_PONTO = BASE_URL+"/tjpi/presenca/PontoDePresenca";
    
    //public static String BATIMENTO_PONTO_COM_CODIGOS = BATIMENTO_PONTO+getCodigos();
    public static final String INICIAR_PONTO = BASE_URL+"/tjpi/presenca/IniciarPonto";

    public static String getCodigos() {
        System.out.println("Recuperando codigoAtivacao: "+RegistroWindows.getCodigoAtivacaoRegistro());
        String codAtivacao = RegistroWindows.getCodigoAtivacaoRegistro();
        Log.i(codAtivacao);
        String codUnicoM = RegistroWindows.getCodigoUnicoMaquina();
        Log.i(codUnicoM);
        return "?codigoAtivacao="+ codAtivacao+"&codigoUnicoMaquina="+codUnicoM;
    }
       
    
}
