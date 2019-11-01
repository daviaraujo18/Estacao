package core;

import utils.ArquivoUtils;
import utils.CryptoUtils;
import utils.LogAplicacao;
import utils.WinRegistry;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Classe responsavel por fazer a conexao da EstacaoPonto com o Registro do Windows
 */
public class RegistroWindows {
	public static final String KEY_REGISTRO = "SOFTWARE\\TJPIEstacaoPonto";
	
	public static String getCodigoAtivacaoRegistro() {

		if (OSVerifier.isWindows()) {
			try {
  
				String value = WinRegistry.readString(
						WinRegistry.HKEY_CURRENT_USER, //HKEY
						KEY_REGISTRO, //Key
						"codigoAtivacao");                                              //ValueName

				String valor = value;
				return valor;

			} catch (Exception e) {
				LogAplicacao.e("\\Estacao >> ERRO na busca do codigo de ativacao.");
				LogAplicacao.e(e);
//				e.printStackTrace();
				return null;
			}
		} else {
			return "SistemaOperacionalNaoSuportado";
		}
	}

	public static boolean registrarCodigoAtivacao(String codigoAtivacao) {
		if (OSVerifier.isWindows()) {
			try {
					
				int hkey = WinRegistry.HKEY_CURRENT_USER;
				String key = KEY_REGISTRO;
				WinRegistry.createKey(hkey, key);
				WinRegistry.writeStringValue(hkey, key, "codigoAtivacao", codigoAtivacao);

				LogAplicacao.i("Registro Salvo");

				return true;

			} catch (Exception e) {
				LogAplicacao.i("Erro ao criar registro do windows: " + e.getMessage());
				return false;
			}
		} else {
			return true;
		}
	}

	public static String getCodigoUnicoMaquina() throws IOException {

		final String nomeArquivoCodigoUincoMaquina = "unico";
		String codigoUnicoMaquina = "";

		File arquivoCodUnicoMaquina = new File(LocalPaths.PATH_DATA, nomeArquivoCodigoUincoMaquina);
		if (!arquivoCodUnicoMaquina.exists() && !arquivoCodUnicoMaquina.isDirectory()) {
			UUID uuid = UUID.randomUUID();
			String random = uuid.toString();
			codigoUnicoMaquina = random;
			ArquivoUtils.saveFile(nomeArquivoCodigoUincoMaquina, codigoUnicoMaquina);
			LogAplicacao.i("Gerando e salvando codigo unico maquina: " + codigoUnicoMaquina);
		} else {
			codigoUnicoMaquina = ArquivoUtils.readFile(nomeArquivoCodigoUincoMaquina);
		}

		return codigoUnicoMaquina;
	}
	/**
	 * Metodo que gera uma string randomica de tamanho 6 que servirar como codigo de ativacao
	 *
	 * @return
	 */
	public static String gerarCodigoAtivacao() {
		UUID uuid = UUID.randomUUID();
		String random = uuid.toString();

		return CryptoUtils.md5UB64(random);
	}

}
