package core.leitura;

import core.DadosFrequentadores;
import utils.ArquivoRegistros;
import utils.The;
import view.TelaPonto;

import java.util.Map;

public enum EventoLeitura {
    NULO,

    DEDO_POSICINADO,

    DIGITAL_RECONHECIDA{
        @Override
        public boolean before(Leitura leitura) {
            return ArquivoRegistros.escreverRegistro(leitura.getIdFrequentador()  + "-" + leitura.getMomento());
        }

        @Override
        public String getData(TelaPonto tela, Leitura leitura) {
            Map<Integer, String> mapaIdInfoFrequentadores = DadosFrequentadores.getInstance().getMapaIdInfoFrequentadores();
            Integer id = Integer.parseInt(leitura.getIdFrequentador());
            String[] dados = mapaIdInfoFrequentadores.get(id).split(";");
            String nome = dados[0];
            String matricula = dados[1];
            String urlFoto = dados[2];
            String dad = "'"+leitura.getIdFrequentador() + "','" + leitura.getMomento() + "','" + nome + "','" + matricula + "','" + urlFoto + "'";
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

    ERRO_LEITURA;

    public void process(TelaPonto tela, Leitura leitura) {
        boolean bf = before(leitura);
        if(bf){
            The.inserirJavascript(tela.webEngine, "process('" + this.name()+"', "+getData(tela, leitura)+")");

            after(tela);
        }
    }
    public boolean before(Leitura leitura){return true;}
    public String getData(TelaPonto tela, Leitura leitura){return "''";}
    public void after(TelaPonto tela){}


}