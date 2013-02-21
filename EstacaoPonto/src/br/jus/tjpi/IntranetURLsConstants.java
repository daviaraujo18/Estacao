
package br.jus.tjpi;

import br.jus.tjpi.system.utils.EstacaoPontoUtils;

/**
 *
 * @author aers
 */
public class IntranetURLsConstants {
    
    private static String CODIGOS = "?codigoAtivacao="+
            EstacaoPontoUtils.getCodigoAtivacaoRegistro()+
            "&codigoUnicoMaquina="+EstacaoPontoUtils.getCodigoUnicoMaquina();
    
	
    public static final String BASE_URL = "http://192.168.1.17:8080/intranet";
    public static final String INICIALIZAR_PONTO = BASE_URL+"/tjpi/presenca/InicializarPonto";
    public static String INICIALIZAR_PONTO_COM_CODIGOS = BASE_URL+"/tjpi/presenca/InicializarPonto"+getCodigos();
    
    public static final String CADASTRO_FREQUENTADOR = BASE_URL+"/tjpi/presenca/Frequentador?type=create";
    public static final String BATIMENTO_PONTO = BASE_URL+"/tjpi/presenca/PontoDePresenca";
    
    public static String BATIMENTO_PONTO_COM_CODIGOS = BATIMENTO_PONTO+getCodigos();
    public static final String INICIAR_PONTO = BASE_URL+"/tjpi/presenca/IniciarPonto";
    
    public static String getCodigos() {
        System.out.println("Recuperando codigoAtivacao: "+EstacaoPontoUtils.getCodigoAtivacaoRegistro());
        return "?codigoAtivacao="+
            EstacaoPontoUtils.getCodigoAtivacaoRegistro()+
            "&codigoUnicoMaquina="+EstacaoPontoUtils.getCodigoUnicoMaquina();
    }
    
       
    
}
