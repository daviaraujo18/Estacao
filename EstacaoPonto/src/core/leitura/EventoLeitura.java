package core.leitura;

import core.DadosFrequentadores;
import java.io.File;
import java.io.FileInputStream;
import utils.ArquivoRegistros;
import utils.The;
import view.TelaPonto;

import java.util.Map;
import org.apache.commons.io.FilenameUtils;
import utils.CacheManipulation;

public enum EventoLeitura {
    NULO,


    DEDO_POSICINADO,

    LEITURA_EM_ANALISE,

    DIGITAL_RECONHECIDA{
        @Override
        public boolean before(Leitura leitura) {
            return ArquivoRegistros.escreverRegistro(leitura.getIdFrequentador()  + "-" + leitura.getMomento());
        }

        @Override
        public String getData(TelaPonto tela, Leitura leitura) {
            Map<Integer, String> mapaIdInfoFrequentadores = DadosFrequentadores.getInstance().getFrequentadores();
            Integer id = Integer.parseInt(leitura.getIdFrequentador());
            String[] dados = mapaIdInfoFrequentadores.get(id).split(";");
            String matricula = dados[0];
            String nome = dados[1];
            String urlFoto = dados[2];
            
            String nomeArquivo = FilenameUtils.getBaseName(urlFoto);
            nomeArquivo = nomeArquivo +"."+ FilenameUtils.getExtension(urlFoto);
            FileInputStream fileInputStream=null;

            File file = new File("C:\\Estacao\\imgs\\"+nomeArquivo);
            if (!CacheManipulation.searchAndEdit(urlFoto))
            {
                if (!CacheManipulation.insert(urlFoto))
                {
                    file = new File("C:\\Estacao\\imgs\\silhueta_masculina.jpg");
                }
            }
            //File file = new File("C:\\Estacao\\imgs\\a.jpg");

            byte[] bFile = new byte[(int) file.length()];

            try {
                //convert file into array of bytes
                fileInputStream = new FileInputStream(file);
                fileInputStream.read(bFile);
                fileInputStream.close();

                for (int i = 0; i < bFile.length; i++) {
                    System.out.print((char)bFile[i]);
                }

              
            }catch(Exception e){
                    e.printStackTrace();
            }
            String dataURI =  javax.xml.bind.DatatypeConverter.printBase64Binary(bFile);
            
//            String dad = "'"+leitura.getIdFrequentador() + "','" + leitura.getMomento() + "','" + nome + "','" + matricula + "','" + urlFoto + "'";
            String dad = "'"+leitura.getIdFrequentador() + "," + leitura.getMomento() + "," + matricula + "," + nome + "," + dataURI+"'";
        //    System.out.println("Dad "+dad);
            return dad;
        }
        
        public void after(TelaPonto tela) {
            tela.sound.playOK();
        }

    },

    DIGITAL_NAO_RECONHECIDA{
        @Override
        public void after(TelaPonto tela) {
            tela.sound.playError();
        }
    },

    ERRO_LEITURA{
        @Override
        public void after(TelaPonto tela) {
            tela.sound.playError();
        }
    };

    public void process(TelaPonto tela, Leitura leitura) {
        boolean bf = before(leitura);
        if(bf){
            try {
                The.inserirJavascript(tela.webEngine, "process('" + this.name()+"', "+getData(tela, leitura)+")");
            }catch (RuntimeException e){
                e.printStackTrace();
            }


            after(tela);
        }
    }
    public boolean before(Leitura leitura){return true;}
    public String getData(TelaPonto tela, Leitura leitura){return "''";}
    public void after(TelaPonto tela){}


}