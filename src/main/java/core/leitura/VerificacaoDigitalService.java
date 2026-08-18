package core.leitura;

import controllers.MainController;
import core.DadosFrequentadores;
import core.SincronizacaoImediata;
import java.util.Map;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import utils.ArquivoRegistros;
import utils.LogAplicacao;

/**
 * Created by Danilo on 12/02/14.
 */
public class VerificacaoDigitalService extends Service<Leitura>{

    String digitalHash;

    public VerificacaoDigitalService(String hash){
        this.digitalHash = hash;
    }

    @Override
    protected Task<Leitura> createTask() {
        return new Task<Leitura>() {
            @Override
            protected Leitura call() throws Exception {
                EventoLeitura resultado = EventoLeitura.NULO;
                int id = 0;

                LeitorDigital ld = MainController.INSTANCE.getLeitorDigital();
                id = ld.searchDigitalOnIndexSearchEngine(digitalHash);
                ld.fecharLeitor();

                if (digitalHash == null || digitalHash.isEmpty()) {
                    resultado = EventoLeitura.ERRO_LEITURA;
                }else{
                    if (id > 0) {
                        /////////////aqui
                //                    resultado = EventoLeitura.DIGITAL_RECONHECIDA;
                //    }else{
                        Map<Integer, String> mapaIdInfoFrequentadores = DadosFrequentadores.getInstance().getFrequentadores();
                        String[] dados = mapaIdInfoFrequentadores.get(id).split(";");
                        String localTrabalho = dados[4];
                        boolean definido =false;
//                        System.out.println("localtrabalho: "+localTrabalho);
                        if (!localTrabalho.equals("0"))
                        {
                            String prediosIds = MainController.INSTANCE.prediosIds;
//                            System.out.println("executado");
//                            System.out.println("prediosIds: "+prediosIds.toString());
                            String [] prediosIdsArray = prediosIds.toString().split(";");
//                            System.out.println("comparando predios");
                            

                            for (String predioId : prediosIdsArray)
                            {
                                if (localTrabalho.equals(predioId))
                                {
//                                    System.out.println("predio igual: "+localTrabalho + " "+predioId);
                                    resultado = EventoLeitura.DIGITAL_RECONHECIDA;
                                    definido = true;
                                    break;
                                }
                            }
                        }
                        if (!definido)
                        {
//                            System.out.println("fim da compara��o");
                            resultado = EventoLeitura.DIGITAL_RECONHECIDA_RESSALVA_PREDIO;
                        }
                        
                    }else{
                        resultado = EventoLeitura.DIGITAL_NAO_RECONHECIDA;
                    }
                }

                // Horário do SERVIDOR (SincronizacaoImediata.momentoAtual() ->
                // ThreadRelogio), mesma fonte que já alimenta o relógio
                // principal da tela — não o relógio físico da máquina
                // Windows. Assim o texto de confirmação de ponto sempre bate
                // com o relógio da tela, independente de fuso ou hora
                // configurados errados na máquina onde a Estação roda.
                String momento = SincronizacaoImediata.momentoAtual();

                // Sincroniza na hora (mesmo padrão do login manual,
                // ValidarBatidaManualService) em vez de esperar o ciclo
                // periódico de ArquivoRegistros/ThreadRelogio — assim a
                // batida biométrica já está confirmada no servidor a tempo
                // do reload da tela mostrar a mesma mensagem de confirmação
                // que o login manual mostra. Só grava na fila local
                // (ArquivoRegistros) se essa chamada falhar — se gravasse
                // sempre, a MESMA batida seria reenviada de novo pelo ciclo
                // periódico, criando um TimeRecord duplicado fora de ordem
                // e quebrando a alternância entrada/saída.
                if (resultado == EventoLeitura.DIGITAL_RECONHECIDA || resultado == EventoLeitura.DIGITAL_RECONHECIDA_RESSALVA_PREDIO) {
                    boolean sincronizado = false;
                    try {
                        sincronizado = SincronizacaoImediata.sincronizar(id, momento, "biometric");
                    } catch (Exception e) {
                        LogAplicacao.e(e);
                    }
                    if (!sincronizado) {
                        ArquivoRegistros.escreverRegistro(id + "-" + momento);
                    }
                }

                Leitura l = new Leitura(resultado, digitalHash, String.valueOf(id), momento);
                return l;
            }


        };
    }
}
