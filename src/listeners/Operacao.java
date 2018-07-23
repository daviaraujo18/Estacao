package listeners;

import controllers.MainController;
import core.*;
import core.leitura.EventoLeitura;
import core.leitura.Leitura;
import core.leitura.VerificacaoDigitalHandler;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.web.WebEngine;
import utils.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Danilo on 22/04/14.
 */
public enum Operacao {

    RECUPERAR_FREQUENTADORES("downloadFrequentadores"){

        public void execute(final String metodo, final WebEngine engine){

            boolean temConexaoComIntranet = VerificaConexao.verificaConexao() != -1;
            if (temConexaoComIntranet) {
                Log.i("Iniciando download dos dados dos frequentadores...");
                final DownloadFrequentadoresService downloadFrequentadoresService = new DownloadFrequentadoresService();
                downloadFrequentadoresService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent) {
                        System.out.println("Download finished");
                        String dadosFrequentadoresBruto = downloadFrequentadoresService.getValue();
                        DadosFrequentadores.getInstance().init(dadosFrequentadoresBruto);
                        The.inserirJavascript(MainController.INSTANCE.tela.getWebEngine(), "removeLoading()");

//
                        RECUPERAR_PREDIOS_PERMITIDOS.execute(metodo, engine);
                    }
                });
                downloadFrequentadoresService.start();
            }

        }

        @Override
        public boolean verificacaoSegundoNivel(WebEngine engine) {
            return engine.getLocation().contains("presenca/PontoDePresenca");
        }
    },
        RECUPERAR_PREDIOS_PERMITIDOS("prediosPermitidos"){

        public void execute(String metodo, WebEngine engine){
            boolean temConexaoComIntranet = VerificaConexao.verificaConexao() != -1;
            if (temConexaoComIntranet) {
                Log.i("Iniciando PrediosPermitidos");
                final PrediosPermitidosService prediosPermitidosService = new PrediosPermitidosService();
                prediosPermitidosService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent) {
                        String prediosPermitidosIDs = prediosPermitidosService.getValue();
                        Log.i("Predios permitidos: " + prediosPermitidosIDs);
                        MainController.INSTANCE.prediosIds = prediosPermitidosIDs;

                    }
                });
                prediosPermitidosService.start();
            }
        }

    },
    RECUPERAR_CODIGO_ATIVACAO("recuperarCodigoAtivacao"){
        public void execute(String metodo, WebEngine webEngine){
            Log.i("Recuperando CodigoDeAtivacao e setando no Registro do Windows");

            Object data = webEngine.executeScript("jQuery('#codigoAtivacao').val();");

            if (RegistroWindows.registrarCodigoAtivacao(data.toString()))
            {
                webEngine.load(IntranetURLs.INICIALIZAR_PONTO + IntranetURLs.getCodigos());

            }
            else
            {
                webEngine.load(IntranetURLs.PROBLEMA_REGISTRO);
            }
        }

        @Override
        public boolean verificacaoSegundoNivel(WebEngine engine) {
            return engine.getLocation().contains("presenca/RecuperarCodigoAtivacao");
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
            Log.i("horario do servidor: "+((Calendar)(dataServidor.clone())).getTime()); //#flag
            MainController.INSTANCE.criarThreadRelogio(dataServidor);

            Log.atualizarDataLog();
        }
    },
    ATUALIZAR_RELOGIO_LOCAL("atualizarRelogioLocal"){
        @Override
        public void execute(String metodo, WebEngine engine){
//            System.out.println("Recebendo requisi��o para atualizar hor�rio na p�gina");
            if (MainController.INSTANCE.getThreadRelogio() != null) {
                String horario = MainController.INSTANCE.getThreadRelogio().atualizarRelogio();
                try {
//                    System.out.println("Atualizando...");
                    MainController.INSTANCE.atualizarHorario(horario);

                    Calendar dataRestartDiario = MainController.INSTANCE.getThreadRelogio().getDataRestartDiario();
                    Calendar dataServidorAtual = MainController.INSTANCE.getThreadRelogio().getDataServidorAtual();

                    if (CalendarUtils.temMesmoHorario(dataServidorAtual, dataRestartDiario)) {
                        try {
                            ScriptsBat.restartAplicacao();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    Log.atualizarDataLog();
                } catch (FileNotFoundException ex) {
						Log.e(ex);
                    Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
						Log.e(ex);
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
					Log.e(ex);
                Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    },
    NAOSINCRONIZADO("naosincronizado"){
        @Override
        public void execute(String metodo, WebEngine engine){
            Log.i("Nao foi possivel sincronizar."+MainController.INSTANCE.getThreadRelogio().getDataServidorAtual().getTime());//#flag
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
                boolean temConexaoComIntranet = VerificaConexao.verificaConexao() != -1;
                if (temConexaoComIntranet) {
                    MainController.INSTANCE.iniciarSincronizacao();
                }
            } catch (FileNotFoundException ex) {
				Log.e(ex);
                Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
				Log.e(ex);
                Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    },
    VIVO_MORTO("deadOrAlive"){

        @Override
        public void execute(String metodo, WebEngine engine){
            boolean temConexaoComIntranet = VerificaConexao.verificaConexao() != -1;
            if (temConexaoComIntranet) {
                final VivoOuMortoService vivoOuMortoService = new VivoOuMortoService();
                vivoOuMortoService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent) {
                        if (!vivoOuMortoService.getValue()) {
                            Log.e("VivoOuMortoService -> falhou");
                        }
                    }
                });
                vivoOuMortoService.start();
            }
        }
    },

    ATUALIZARVERSAOESTACAO("atualizarVersaoEstacao"){

        public void execute(String metodo, WebEngine engine){

            boolean temConexaoComIntranet = VerificaConexao.verificaConexao() != -1;
            if (temConexaoComIntranet) {
                final ChecarUltimaVersaoService checarUltimaVersaoService = new ChecarUltimaVersaoService();
                checarUltimaVersaoService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent) {
                        String ultimaVersaoNoServidor = checarUltimaVersaoService.getValue();

                        AtualizarEstacao.verificaVersoes(ultimaVersaoNoServidor);

                    }
                });
                checarUltimaVersaoService.start();
            }
        }
    },
    LOGINMANUAL("loginManual"){
        public void execute(String metodo,WebEngine engine){
            MainController.INSTANCE.getCds().loginManual = true;

        }
    }
    ;

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

