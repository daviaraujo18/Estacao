package core;


import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import utils.CalendarUtils;
import utils.DateUtils;
import utils.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class VivoOuMortoService extends Service<Boolean>  {

	String urlString = Configuracoes.base_intranet_url.get() + "/presenca/AdicioneEstacao";

	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() {

				String codAtivacao = RegistroWindows.getCodigoAtivacaoRegistro();
				String versao = EstacaoPonto.versao;
				String arquivosDeLog = getNameLogs(10);
				String estadoEstacao = "FUNCIONANDO";

				try {

					String codAtivacaoEncoded = URLEncoder.encode(codAtivacao,  java.nio.charset.StandardCharsets.UTF_8.toString());
					String arquivosDeLogEncoded = URLEncoder.encode(arquivosDeLog,  java.nio.charset.StandardCharsets.UTF_8.toString());
					String estadoEstacaoEncoded = URLEncoder.encode(estadoEstacao,  java.nio.charset.StandardCharsets.UTF_8.toString());
					String versaoEncoded = URLEncoder.encode(versao,  java.nio.charset.StandardCharsets.UTF_8.toString());

					String urlParameters = "?codAtivacao=" + codAtivacaoEncoded + "&versao=" + versaoEncoded + "&estadoEstacao=" + estadoEstacaoEncoded + "&arquivosDeLog=" + arquivosDeLogEncoded;
					System.out.println("URLParameters: " + urlParameters);

					urlString = urlString + urlParameters;

					URL url = new URL(urlString);
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					//add reuqest header
					con.setRequestMethod("GET");
					con.setRequestProperty("User-Agent", "JavaFX");

					BufferedReader in = new BufferedReader(
							new InputStreamReader(con.getInputStream(), "UTF-8"));
					String inputLine;
					StringBuffer response = new StringBuffer();

					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();

					System.out.println("VivoOuMorto -> " + response.toString());

					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}

			}
		};
	}

	public static String getNameLogs(int deTantosDiasAtras) {

		deTantosDiasAtras = (-1) * (deTantosDiasAtras + 1);
		String logsNames="";
		File folder = new File(LocalPaths.PATH_LOG);

		final Calendar xDiasAtrasCalendar = Calendar.getInstance();
		xDiasAtrasCalendar.add(Calendar.DAY_OF_MONTH, deTantosDiasAtras);
		System.out.println(DateUtils.format(xDiasAtrasCalendar, "dd/MM/yyyy"));

		if (folder.exists())
		{
			FilenameFilter fnf = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {

					String[] partesString = name.split("_");
					String dataBruta = partesString[2];

					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
					try {
						Date data = sdf.parse(dataBruta);

						Calendar fileDate = (Calendar) xDiasAtrasCalendar.clone();
						fileDate.setTime(data);

						return !(fileDate.before(xDiasAtrasCalendar));

					} catch (ParseException e) {
						e.printStackTrace();
						return true;
					}
				}
			};

			String[] listFiles = folder.list(fnf);

			for (String fileNameString : listFiles) {
				logsNames= fileNameString +" / "+logsNames;
			}
		}
		return logsNames;
	}
}
