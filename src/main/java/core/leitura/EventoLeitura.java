package core.leitura;

import core.DadosFrequentadores;
import core.LocalPaths;
import java.io.File;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import utils.*;
import view.TelaPonto;

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
            return reconheceDigital(leitura);
        }

        public void after(TelaPonto tela) {
            tela.sound.playOK();
        }

    },

    DIGITAL_NAO_RECONHECIDA{
        @Override
        public void after(TelaPonto tela) {
            LogEstacao.w("Digital NÃO Reconhecida");
            tela.sound.playError();
        }
    },

    ERRO_LEITURA{
        @Override
        public void after(TelaPonto tela) {
            LogEstacao.e("Erro de Leitura");
            tela.sound.playError();
        }
    },
    DIGITAL_RECONHECIDA_RESSALVA_PREDIO{
        @Override
        public boolean before(Leitura leitura) {
            return ArquivoRegistros.escreverRegistro(leitura.getIdFrequentador()  + "-" + leitura.getMomento());
        }

        @Override
        public String getData(TelaPonto tela, Leitura leitura) {
            LogEstacao.w("Registro com ressalva");
            return reconheceDigital(leitura);
        }

        public void after(TelaPonto tela) {
            tela.sound.playOK();
        }

    },
    USUARIO_SENHA_INVALIDOS{
        @Override
        public void after(TelaPonto tela) {
            LogEstacao.w("Usuário ou Senha Inválidos!");
//            The.inserirJavascript(tela.getWebEngine(), "changeMensagemStatus(' Usuário ou Senha Inválidos!')");
            tela.sound.playError();
        }
    },
    USUARIO_SEM_PERMISSAO_MANUAL{
        @Override
        public void after(TelaPonto tela) {

            LogEstacao.w("Usuário não tem autorização para registrar com login/senha. Entre em contato com a SEAD");
            tela.sound.playError();
        }
    },
    SEM_CONEXAO_TIMEOUT{
        @Override
        public void after(TelaPonto tela) {

            LogEstacao.w("Não foi possível conexão com Intranet - timeout");
            tela.sound.playError();
        }
    },
    ESTACAO_SEM_PERMISSAO_PARA_BATIDA_MANUAL{
        @Override
        public void after(TelaPonto tela) {

            LogEstacao.w("Estação não esta liberada para aceitar batidas com login/senha.");
            tela.sound.playError();
        }
    }

    ;

    public void process(TelaPonto tela, Leitura leitura) {
        boolean bf = before(leitura);
        if(bf){
            try {
                The.inserirJavascript(tela.getWebEngine(), "process('" + this.name()+"', "+getData(tela, leitura)+")");
            }catch (RuntimeException e){
                LogAplicacao.e(e);
//                e.printStackTrace();
            }


            after(tela);
        }
    }
    public boolean before(Leitura leitura){return true;}
    public String getData(TelaPonto tela, Leitura leitura){return "''";}
    public void after(TelaPonto tela){}
    private static String reconheceDigital(Leitura leitura)
    {
        Map<Integer, String> mapaIdInfoFrequentadores = DadosFrequentadores.getInstance().getFrequentadores();
        Integer id = Integer.parseInt(leitura.getIdFrequentador());
        String[] dados = mapaIdInfoFrequentadores.get(id).split(";");
        String matricula = dados[0];
        String nome = dados[1];
        String urlFoto = dados[2];
        String sexo = dados[3];
//            System.out.println("Sexo: "+sexo+" urlFoto: "+urlFoto);
        String nomeArquivo = FilenameUtils.getBaseName(urlFoto);
        //nomeArquivo = nomeArquivo +"."+ FilenameUtils.getExtension(urlFoto);


        String dataURI="";
        String dataURI_M ="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";
        String dataURI_F= "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";

        File file = new File(LocalPaths.PATH_CACHE+nomeArquivo);
        if (!CacheManipulation.searchAndEdit(urlFoto))
        {
            if (!CacheManipulation.insert(urlFoto))
            {
                if (sexo.equals("M"))
                {
                    dataURI=dataURI_M;
                }
                else
                {
                    dataURI=dataURI_F;
                }

            }
        }
        else
        {
            if (!file.exists())
            {
                if (sexo.equals("M"))
                {
                    dataURI=dataURI_M;
                }
                else
                {
                    dataURI=dataURI_F;
                }
            }
        }
        if (dataURI.isEmpty())
        {


            try {
                dataURI = FileUtils.readFileToString(file);
            }catch(Exception e){
                LogAplicacao.e(e.getMessage());
            }


        }

        String dad = "'"+leitura.getIdFrequentador() + "," + leitura.getMomento() + "," + matricula + "," + nome + "," + dataURI+"'";
        LogEstacao.i("Digital Reconhecida -> "+leitura.getIdFrequentador() + "," + leitura.getMomento() + "," + matricula + "," + nome );
        return dad;
    }

}