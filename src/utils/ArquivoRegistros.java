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
				conteudo += linha;
				conteudo += separador;
			}
		}
		if (!conteudo.isEmpty()) {
			conteudo = conteudo.substring(0, conteudo.length() - 1);
			LogEstacao.i("\n -- Dados arquivo: " + conteudo);
		}
		return conteudo;
	}

	public static String lerArquivoSincronizado() {
		boolean deuCerto = false;
		List<String> dadosArquivoPrincipal = null;
		try {
			dadosArquivoPrincipal = FileUtils.readLines(arquivo) ;
			if(dadosArquivoPrincipal.size()>0){
				FileUtils.writeLines(arquivoTemp, dadosArquivoPrincipal);
//				escrever(dadosArquivoPrincipal,arquivoTemp);
			}
			deuCerto = true;
		} catch (Exception ex) {
			LogAplicacao.e(ex);
			deuCerto = false;
		} 
		if(deuCerto){
			try {
				if(!dadosArquivoPrincipal.isEmpty()){
					limparArquivoPrincipal();
				}	
				return lerArquivo(arquivoTemp);
			} catch (IOException ex) {
				LogAplicacao.e(ex);
			}
		}
		return "";
	}

	public static boolean escreverRegistro(String registro) {
		if (registro == null) {
			return false;
		}
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
