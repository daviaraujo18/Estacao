/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;


import controllers.MainController;
import core.LocalPaths;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Daniel Leite TJPI
 * Trata da edi��o do arquivo ./imgs/cache.txt, arquivo respons�vel por permitir o download das fotos 3x4 para a Esta��o.
 */
public class CacheManipulation {
   private static final int VALIDADE = 20; //qtd dias para o cache expirar
   
    //verifica se nome do arquivo existe no cache.txt
    //se existe, retorna true e tenta baixar a foto, alterando a data de download no cache.txt em caso positivo.
   public static boolean searchAndEdit(String enderecoWeb)
   {
        boolean encontrado=false;
        String conteudo="";
        //search & edit
		
//        File arquivo = new File(LocalPaths.PATH_CACHE+"cache.txt"); 
//
//        File dir  = arquivo.getParentFile();
//        dir.mkdirs();


        
        
        String nomeArquivo = FilenameUtils.getBaseName(enderecoWeb);
        
        String enderecoLocal=LocalPaths.PATH_CACHE+nomeArquivo;

//        try {
//               arquivo.createNewFile();
//            FileReader fr = new FileReader(arquivo);  
//            BufferedReader br = new BufferedReader(fr); 
//            
//            String linhaCache = br.readLine(); //l� a primeira linha
            
//            while (linhaCache!=null)
//            {
//                if (linhaCache.contains(nomeArquivo) && encontrado == false)
//                {//h� um registro no cache que a foto foi baixada.
//                       encontrado=true;
//                       System.out.println("Arquivo encontrado: "+linhaCache);
//                       String dadosCache[] = linhaCache.split(" ");
                       
//                       Calendar dataDownloadFoto = viewDateToCalendar(dadosCache[1]);
//                       dataDownloadFoto.add(Calendar.DAY_OF_YEAR,VALIDADE);
//                       //Calendar today = Calendar.getInstance();
//                       Calendar today =(Calendar) MainController.INSTANCE.getThreadRelogio().getDataServidorAtual().clone();
//                       
                       File foto = new File(enderecoLocal);
                       
                       if (!foto.exists())
                       {
//                            System.out.println("Foto inexistente...Baixando...");
                            if (DownloadFoto.baixaFoto(enderecoWeb))
                            {
								encontrado = true;
//                                System.out.println("Download terminado.");
//                                conteudo += (dadosCache[0]+" "+viewDate(today.getTime())+"\r\n");
                            }           
                            else
                            {
                                LogAplicacao.w("Problemas no download.");
//                                conteudo+=linhaCache+"\r\n";
                            }
                            
                       }
					   else{
						  encontrado = true; 
					   }
//                       else
//                       {
////                           System.out.println("Comparando a data: "+dataDownloadFoto.getTime().toString()+" com a do servidor: "+today.getTime().toString());
//                           if (today.after(dataDownloadFoto))
//                           {//foto antiga, baixar novamente.
//                             System.out.println("Validade da foto expirou...Baixando novamente...");
//                             if (DownloadFoto.baixaFoto(enderecoWeb))
//                             {
////                                 System.out.println("Download terminado.");
//                                 conteudo += (dadosCache[0]+" "+viewDate(today.getTime())+" "+"\r\n");
//                       }
//                             else
//                             {
//                                 System.out.println("Problemas no download.");
//                                 conteudo+=linhaCache+"\r\n";
//                 }
//                           }
//                 else
//                 {
////                                System.out.println("Foto dentro da validade. N�o h� necessidade de download...");
//                     conteudo+=linhaCache+"\r\n";
//                 }
//                       }
//                 }
//                 else
//                 {
//                     conteudo+=linhaCache+"\r\n";
//                 }
//                 linhaCache = br.readLine(); //se tiver mais linhas, l� todas elas 
//            }
//            br.close();
//            fr.close();
//            FileWriter fileWriter = new FileWriter(arquivo, false);
//            PrintWriter printWriter = new PrintWriter(fileWriter);
//            printWriter.print(conteudo);
//            printWriter.flush();
//            printWriter.close();  
//        }
//        catch (IOException ex) 
//        {
//			LogAplicacao.e(ex);
//           ex.printStackTrace();
//        }
        return encontrado;
   }
   
   //Tenta baixar a foto e, em caso positivo, insere uma linha no
   //cache.txt no formato '<nome_do_arquivo.jpg> <data_dowload>'.
   //Se tudo der certo, retorna true.
   public static boolean insert(String enderecoWeb)
   {
        File arquivo = new File(LocalPaths.PATH_CACHE+"cache.txt"); 
        String nomeArquivo = FilenameUtils.getBaseName(enderecoWeb);
        //nomeArquivo = nomeArquivo +"."+ FilenameUtils.getExtension(enderecoWeb);
        boolean insercaoValida = false;
		DownloadFoto dw =new DownloadFoto();
        try 
        {   
            FileWriter fw;
            
            fw = new FileWriter(arquivo,true);
            BufferedWriter bw = new BufferedWriter(fw);
           
//            Calendar dataAtual =(Calendar) Calendar.getInstance();
            Calendar dataAtual =(Calendar) MainController.INSTANCE.getThreadRelogio().getDataServidorAtual().clone();
            LogAplicacao.i("Baixando foto "+nomeArquivo+"...");
            if (dw.baixaFoto(enderecoWeb))
            {
//                System.out.println("Download terminado.");
                bw.write(nomeArquivo+" "+viewDate(dataAtual.getTime()));
                bw.newLine();
                insercaoValida= true;
            }           
            else
            {
                LogAplicacao.w("Problemas no download.");
            }
            bw.flush();
            bw.close();

            
        }
        catch (IOException ex) 
        {
			LogAplicacao.e(ex);
//           ex.printStackTrace();
        }
        return insercaoValida;
   }     
    public static Calendar viewDateToCalendar(String in) {

                DateFormat df =  DateFormat.getDateInstance(DateFormat.SHORT, new Locale("pt", "BR"));
                df.setLenient(false);
                
                try
                {
                    Date date = df.parse(in);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    return cal;
                }
                catch(Exception ex)
                {
					LogAplicacao.e(ex);
//                    System.out.println("Data inv�lida.");
                    return null;
                }
                
    }
    public static String viewDate(Date date) {
            if (date == null) {
                    return "";
            } else {
                    return new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR")).format(date).toString();
            }
    }

}
