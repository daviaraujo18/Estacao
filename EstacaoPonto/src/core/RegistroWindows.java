package core;

import java.util.UUID;
import utils.CryptoUtils;
import utils.Log;
import utils.WinRegistry;

/**
 * Classe responsavel por fazer a conexao da EstacaoPonto com o Registro do Windows
 *
 * @author Anderson Soares <aersandersonsoares@gmail.com>
 */
public class RegistroWindows {

	public static String getCodigoAtivacaoRegistro() {

		if (OSVerifier.isWindows()) {
			try {
  
				String value = WinRegistry.readString(
						WinRegistry.HKEY_LOCAL_MACHINE, //HKEY
						"SOFTWARE\\TJPIEstacaoPonto", //Key
						"codigoAtivacao");                                              //ValueName

				String valor = value;
				return valor;

			} catch (Exception e) {
				Log.e("\\Estação >> ERRO na busca do código de ativação.");
				Log.e(e);
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

				int hkey = WinRegistry.HKEY_LOCAL_MACHINE;
				String key = "SOFTWARE\\TJPIEstacaoPonto";
				WinRegistry.createKey(hkey, key);
				WinRegistry.writeStringValue(hkey, key, "codigoAtivacao", codigoAtivacao);

				return true;

			} catch (Exception e) {
				Log.i("Erro ao criar registro do windows: " + e.getMessage());
				return false;
			}
		} else {
			return true;
		}
	}

	public static String getCodigoUnicoMaquina() {

		if (OSVerifier.isWindows()) {

			String hdSerial = jWMI.montaCodUnico();
			if (hdSerial.isEmpty()) {
				return "Erro na construção do código de ativação.";
			}

			String serialCriptografado = CryptoUtils.md5UB64(hdSerial);
			Log.i("\n*HDSERIAL: " + hdSerial);
			Log.i("\n**SERIAL CRIPTOGRAFADO: " + serialCriptografado);
			return serialCriptografado;
		} else {
			return "SistemaOperacionalNaoSuportado";
		}
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
