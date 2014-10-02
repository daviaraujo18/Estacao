
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
    public static final String INICIALIZAR_PONTO = BASE_URL+"/tjpi/presenca/InicializarPonto";
    public static final String CADASTRO_FREQUENTADOR = BASE_URL+"/tjpi/presenca/Frequentador?type=create";
    public static final String BATIMENTO_PONTO = BASE_URL+"/tjpi/presenca/PontoDePresenca";
    public static final String INICIAR_PONTO = BASE_URL+"/tjpi/presenca/IniciarPonto";
    //public static final String URL_UPDATE = "http://localhost/intranet_uploads/tjpi/upload_presenca/EstacaoPonto.jar";
    public static final String URL_UPDATE = "http://teste.tjpi.jus.br/intranet/uploads/tjpi/upload_presenca/EstacaoPonto.jar";//servidor de teste

    public static String getCodigos() {
        String codAtivacao = RegistroWindows.getCodigoAtivacaoRegistro();
        String codUnicoM = RegistroWindows.getCodigoUnicoMaquina();
        Log.i(codUnicoM);
        return "?codigoAtivacao="+ codAtivacao+"&codigoUnicoMaquina="+codUnicoM;
    }
       
    
}
