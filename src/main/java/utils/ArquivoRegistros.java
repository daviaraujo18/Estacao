package utils;

import core.LocalPaths;
import core.RegistroWindows;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Jainilene
 */
public class ArquivoRegistros {

	private static File arquivo = new File(LocalPaths.PATH_REGISTROS + "regs.txt");
	private static File arquivoTemp = new File(LocalPaths.PATH_REGISTROS + "regstemp" + ".txt");

	public static boolean escrever(String registro) throws IOException {
		return escrever(registro,arquivo);
	}
	
	public static boolean escrever(String registro,File file) throws IOException {
		if (registro == null || registro.isEmpty()) {
			return false;
		}
		try {
			String dadosDescriptografados = ler(file,false);
//			System.out.println("ARQUIVO LIDO: " + dadosArquivo);
//			String dadosDescriptografados = CryptoUtils.decryptDES("cryp:gpf", dadosArquivo);
			if (dadosDescriptografados == null) {
				dadosDescriptografados = "";
			}
//            System.out.println("ARQUIVO LIDO DESCRIPTOGRAFADO: " + dadosDescriptografados);
			dadosDescriptografados = dadosDescriptografados + registro;
			FileWriter fileWriter = new FileWriter(file, false);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			String registroCriptografado = CryptoUtils.encryptDES("cryp:gpf", dadosDescriptografados);
			//System.out.println("REGISRO CRIPTOGRAFADO: " + registroCriptografado);
			printWriter.println(registroCriptografado);
			printWriter.flush();
			printWriter.close();
			fileWriter.close();
			LogEstacao.i("REGISTRO: " + registro);
			return true;
		} catch (IOException e) {
			LogAplicacao.e(e);
//            e.printStackTrace();
			return false;
		}
	}

	public static String ler(File file,boolean criptografado) throws FileNotFoundException, IOException {
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String conteudo = "";
		String linha = "";
		while ((linha = bufferedReader.readLine()) != null) {
			conteudo += linha;
		}

        //liberamos o fluxo dos objetos 
		// ou fechamos o arquivo
		fileReader.close();
		bufferedReader.close();
		if ((conteudo != null && !conteudo.isEmpty()) && !criptografado) {
//            System.out.println("CONTEUDO DO ARQUIVO: " + conteudo);
			conteudo = CryptoUtils.decryptDES("cryp:gpf", conteudo);
			LogEstacao.i("CONTEUDO DESCRIPTOGRAFADO DO ARQUIVO: " + conteudo);
		} else {
			conteudo = "";
		}
		return conteudo;
	}

	
	private static String lerArquivo(File file) throws FileNotFoundException, IOException {
		String separador = ";";
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String conteudo = "";
		String linha = "";
		while ((linha = bufferedReader.readLine()) != null) {
			if (linha != null && !linha.isEmpty()) {
				// Cada linha já está criptografada individualmente (ver
				// escreverRegistro() — proteção do arquivo em repouso).
				// Precisa descriptografar aqui antes de juntar, senão o
				// servidor recebe vários blocos DES concatenados por ";"
				// em vez de um texto reconhecível — não bate nem como
				// criptografia de bloco único, nem como texto puro.
				String linhaDescriptografada = CryptoUtils.decryptDES("cryp:gpf", linha);
				if (linhaDescriptografada != null && !linhaDescriptografada.isEmpty()) {
					conteudo += linhaDescriptografada;
					conteudo += separador;
				}
			}
		}
		if (!conteudo.isEmpty()) {
			conteudo = conteudo.substring(0, conteudo.length() - 1);
			LogEstacao.i("Dados arquivo: " + conteudo);
		}
		return conteudo;
	}

	private static final Object LOCK = new Object();

	/**
	 * Move os registros pendentes de regs.txt pra fila de envio
	 * (regstemp.txt, acumulada) e retorna o conteúdo total dessa fila.
	 *
	 * regstemp.txt só é limpo quando o envio é CONFIRMADO pelo servidor
	 * (ver Operacao.SINCRONIZANDO, listeners/Operacao.java) — se o envio
	 * falhar ou nunca for confirmado, o conteúdo continua na fila e é
	 * reenviado no próximo ciclo, em vez de ser perdido.
	 */
	public static String lerArquivoSincronizado() {
		try {
			moverRegistrosPendentesParaFilaDeEnvio();
			return lerArquivo(arquivoTemp);
		} catch (IOException ex) {
			LogAplicacao.e(ex);
			return "";
		}
	}

	/**
	 * Move regs.txt pra fila de envio via rename atômico (em vez de
	 * ler-o-conteúdo-depois-limpar) — elimina a janela de corrida em que um
	 * novo registro escrito por escreverRegistro() bem no meio do processo
	 * seria apagado sem nunca ter sido lido.
	 */
	private static void moverRegistrosPendentesParaFilaDeEnvio() throws IOException {
		synchronized (LOCK) {
			if (!arquivo.exists() || arquivo.length() == 0) {
				return;
			}
			File movido = new File(LocalPaths.PATH_REGISTROS + "regs_pending_" + System.nanoTime() + ".txt");
			List<String> linhas;
			if (arquivo.renameTo(movido)) {
				linhas = FileUtils.readLines(movido);
				movido.delete();
			} else {
				// Rename pode falhar entre volumes diferentes; cai pro
				// comportamento antigo (menos seguro, mas não trava a
				// sincronização).
				LogAplicacao.e("Não foi possível mover regs.txt atomicamente, usando fallback");
				linhas = FileUtils.readLines(arquivo);
				limparArquivoPrincipal();
			}
			if (!linhas.isEmpty()) {
				FileUtils.writeLines(arquivoTemp, linhas, true);
			}
		}
	}

	/**
	 * Limpa a fila de envio (regstemp.txt) — só deve ser chamada depois de
	 * confirmação real de entrega pelo servidor.
	 */
	public static void limparFilaDeEnvioConfirmada() throws IOException {
		limparArquivo();
	}

	public static boolean escreverRegistro(String registro) {
		if (registro == null) {
			return false;
		}
		synchronized (LOCK) {
			try {

				FileWriter fileWriter = new FileWriter(arquivo, true);
				PrintWriter printWriter = new PrintWriter(fileWriter);
				String registroCriptografado = CryptoUtils.encryptDES("cryp:gpf", registro);
				printWriter.println(registroCriptografado);
				printWriter.flush();
				printWriter.close();
				return true;
			} catch (IOException ex) {
				LogAplicacao.e(ex);
//            ex.printStackTrace();
				return false;
			}
		}
	}

	public static void limparArquivo() throws IOException {
		FileWriter fileWriter = new FileWriter(arquivoTemp, false);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		//printWriter.println("");
		printWriter.flush();
		printWriter.close();
	}

	public static void limparArquivoPrincipal() throws IOException {
		FileWriter fileWriter = new FileWriter(arquivo, false);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		//printWriter.println("");
		printWriter.flush();
		printWriter.close();
		fileWriter.close();
		
	}
}
