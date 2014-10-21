package listeners;

import controllers.MainController;
import core.DadosFrequentadores;
import core.IntranetURLs;
import core.RegistroWindows;
import javafx.scene.web.WebEngine;
import utils.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.AtualizarEstacao;

/**
 * Created by Danilo on 22/04/14.
 */
public enum Operacao {

    RECUPERAR_FREQUENTADORES("callRecuperarFrequentadores"){

        public void execute(String metodo, WebEngine engine){
            Log.i("Iniciando download dos dados dos Frequentadores");
            double inicioDownload = System.currentTimeMillis();
            Object data = engine.executeScript("window.bdFrequencia");

            String dataFixed = (String) data.toString().replace("\n", "");
            //System.out.println("dados Baixados: " + dataFixed);
            Log.i("Montando dados");
            DadosFrequentadores.getInstance().init(dataFixed);
            double fimDownload = System.currentTimeMillis();
            Log.i("Montagem finalizada");
            Log.i("Time elapsed: " + (fimDownload - inicioDownload) + " ms");
            String horario = MainController.INSTANCE.getThreadRelogio().atualizarRelogio();
            try {
                MainController.INSTANCE.atualizarHorario(horario);
            } catch (IOException ex) {
                Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        };

        @Override
        public boolean verificacaoSegundoNivel(WebEngine engine) {
            return engine.getLocation().contains("tjpi/presenca/PontoDePresenca");
        }
    },
    RECUPERAR_CODIGO_ATIVACAO("recuperarCodigoAtivacao"){
        public void execute(String metodo, WebEngine webEngine){
            Log.i("Recuperando CodigoDeAtivacao e setando no Registro do Windows");

            Object data = webEngine.executeScript("jQuery('#codigoAtivacao').val();");

            RegistroWindows.registrarCodigoAtivacao(data.toString());

            webEngine.load(IntranetURLs.INICIALIZAR_PONTO + IntranetURLs.getCodigos());
        };

        @Override
        public boolean verificacaoSegundoNivel(WebEngine engine) {
            return engine.getLocation().contains("tjpi/presenca/RecuperarCodigoAtivacao");
        }
    },
    HORARIO_SERVIDOR_ATUAL("horarioServidorAtual"){
        @Override
        public void execute(String metodo, WebEngine engine){
           
            String[] horario = metodo.split(":");

            int dia = Integer.parseInt(horario[1]);
            int mes = Integer.parseInt(horario[2]);
            int ano = Integer.parseInt(horario[3]);
            int hora = Integer.parseInt(horario[4]);
            int minutos = Integer.parseInt(horario[5]);
            Calendar dataServidor = Calendar.getInstance();
            dataServidor.set(ano, mes, dia, hora, minutos);
            Log.i("horário do servidor: "+((Calendar)(dataServidor.clone())).getTime()); //#flag
            MainController.INSTANCE.criarThreadRelogio(dataServidor);
            
            Log.atualizarDataLog();
        }
    },
    ATUALIZAR_RELOGIO_LOCAL("atualizarRelogioLocal"){
        @Override
        public void execute(String metodo, WebEngine engine){
            System.out.println("Recebendo requisição para atualizar horário na página");
            if (MainController.INSTANCE.getThreadRelogio() != null) {
                String horario = MainController.INSTANCE.getThreadRelogio().atualizarRelogio();
                try {
                    System.out.println("Atualizando...");
                    MainController.INSTANCE.atualizarHorario(horario);
                    Log.atualizarDataLog();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    },

    LIMPAR_REGISTROS_BATIMENTOS("limparRegistosBatimentos"){
        @Override
        public void execute(String metodo, WebEngine engine){
            try {
                MainController.INSTANCE.apagarRegistrosBatimentos();
            } catch (IOException ex) {
                Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    },
    NAOSINCRONIZADO("naosincronizado"){
        @Override
        public void execute(String metodo, WebEngine engine){
            Log.i("Não foi possível sincronizar."+MainController.INSTANCE.getThreadRelogio().getDataServidorAtual().getTime());//#flag
        }
    },
    SINCRONIZANDO("Sincronizando"){
        @Override
        public void execute(String metodo, WebEngine engine) {
            System.out.println("ALERT Sincronizando...");
        }
    },
    SINCRONZIAR_AGORA("Sincronizar Agora"){

        public void execute(String metodo, WebEngine engine){
            System.out.println("Sincronizando...");
            try {
                MainController.INSTANCE.iniciarSincronizacao();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    },
    VIVO_MORTO("vivooumorto"){

        @Override
        public void execute(String metodo, WebEngine engine){
            MainController.INSTANCE.iAmStillAlive();
        }
    }
,
    ATUALIZARESTACOES("atualizarEstacoes"){

        public void execute(String metodo, WebEngine engine){
            Object ultimaVersaoBD = engine.executeScript("window.ultimaVersaoBD");
            AtualizarEstacao.verificaVersoes(ultimaVersaoBD.toString());
        }
    };

    private String nome;

    public abstract void execute(String metodo, WebEngine engine);

    public boolean verificarAplicabilidade(String metodo, WebEngine engine){
        return metodo.contains(this.getNome()) && this.verificacaoSegundoNivel(engine);
    }

    public boolean verificacaoSegundoNivel(WebEngine engine){
        return true;
    }

    Operacao(String nome){
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}

