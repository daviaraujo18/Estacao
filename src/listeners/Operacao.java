package listeners;

import controllers.MainController;
import core.*;
import core.leitura.LeitorDigital;
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

            final ConexaoIntranetService ci = new ConexaoIntranetService();
            ci.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent workerStateEvent) {
                    Long resposta = ci.getValue();
                    if (resposta != ConexaoIntranetService.NAO_CONECTADO) {

                        LeitorDigital.getInstance().clearDB();
                        final DownloadFrequentadoresService downloadFrequentadoresService = new DownloadFrequentadoresService();
                        downloadFrequentadoresService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent workerStateEvent) {
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
            });
            ci.start();
        }



        @Override
        public boolean verificacaoSegundoNivel(WebEngine engine) {
            return engine.getLocation().contains("presenca/PontoDePresenca");
        }
    },
    RECUPERAR_PREDIOS_PERMITIDOS("prediosPermitidos"){

        public void execute(String metodo, WebEngine engine){
            final ConexaoIntranetService ci = new ConexaoIntranetService();
            ci.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent workerStateEvent) {
                    Long resposta = ci.getValue();
                    if (resposta != ConexaoIntranetService.NAO_CONECTADO) {
                        LogAplicacao.i("Recuperando PrediosPermitidos");
                        final PrediosPermitidosService prediosPermitidosService = new PrediosPermitidosService();
                        prediosPermitidosService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent workerStateEvent) {
                                String prediosPermitidosIDs = prediosPermitidosService.getValue();
                                LogAplicacao.i("Predios permitidos: " + prediosPermitidosIDs);
                                MainController.INSTANCE.prediosIds = prediosPermitidosIDs;

                            }
                        });
                        prediosPermitidosService.start();
                    }
                }
            });
            ci.start();
        }

    },
    RECUPERAR_CODIGO_ATIVACAO("recuperarCodigoAtivacao"){
        public void execute(String metodo, WebEngine webEngine){
            LogAplicacao.i("Recuperando CodigoDeAtivacao e setando no Registro do Windows");

            Object data = webEngine.executeScript("jQuery('#codigoAtivacao').val();");

            if (RegistroWindows.registrarCodigoAtivacao(data.toString()))
            {
                try {
                    webEngine.load(IntranetURLs.INICIALIZAR_PONTO + IntranetURLs.getCodigos());
                } catch (IOException e) {
                    LogAplicacao.e("Não foi possível recuperar códigos de aplicação");
                    e.printStackTrace();
                }

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
            LogEstacao.i("Horário do servidor: "+((Calendar)(dataServidor.clone())).getTime()); //#flag
            MainController.INSTANCE.criarThreadRelogio(dataServidor);

        }
    },
    ATUALIZAR_RELOGIO_LOCAL("atualizarRelogioLocal"){
        @Override
        public void execute(String metodo, WebEngine engine){
            LogAplicacao.i("Solicitando para atualizar relógio interno");
            if (MainController.INSTANCE.getThreadRelogio() != null) {
                String horario = MainController.INSTANCE.getThreadRelogio().atualizarRelogio();

//                    System.out.println("Atualizando...");
                MainController.INSTANCE.atualizarHorario(horario);

                Calendar dataRestartDiario = MainController.INSTANCE.getThreadRelogio().getDataRestartDiario();
                Calendar dataServidorAtual = MainController.INSTANCE.getThreadRelogio().getDataServidorAtual();

                if (CalendarUtils.temMesmoHorario(dataServidorAtual, dataRestartDiario)) {
                    try {
                        LogAplicacao.i("Restart automático");
                        ScriptsBat.restartAplicacao();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
                LogEstacao.e(ex.getMessage());
            }
        }
    },
    NAOSINCRONIZADO("naosincronizado"){
        @Override
        public void execute(String metodo, WebEngine engine){
            LogEstacao.i("Nao foi possivel sincronizar."+MainController.INSTANCE.getThreadRelogio().getDataServidorAtual().getTime());//#flag
        }
    },
    SINCRONIZANDO("Sincronizando"){
        @Override
        public void execute(String metodo, WebEngine engine) {
            LogEstacao.i("Sincronizando");
        }
    },
    SINCRONZIAR_AGORA("Sincronizar Agora"){

        public void execute(String metodo, WebEngine engine){
            LogEstacao.i("Sincronizando Agora");
            final ConexaoIntranetService ci = new ConexaoIntranetService();
            ci.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent workerStateEvent) {
                    Long resposta = ci.getValue();
                    if (resposta != ConexaoIntranetService.NAO_CONECTADO) {

                        try {
                            MainController.INSTANCE.iniciarSincronizacao();
                        } catch (FileNotFoundException ex) {
                            LogEstacao.e(ex);
                            Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            LogEstacao.e(ex);
                            Logger.getLogger(OnAlertListener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
            ci.start();
        }
    },
    VIVO_MORTO("deadOrAlive"){

        @Override
        public void execute(String metodo, WebEngine engine){
            final ConexaoIntranetService ci = new ConexaoIntranetService();
            ci.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent workerStateEvent) {
                    Long resposta = ci.getValue();
                    if (resposta != ConexaoIntranetService.NAO_CONECTADO) {
                        final VivoOuMortoService vivoOuMortoService = new VivoOuMortoService();
                        vivoOuMortoService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent workerStateEvent) {
                                if (!vivoOuMortoService.getValue()) {
                                    LogAplicacao.e("VivoOuMortoService -> falhou");
                                }
                            }
                        });
                        vivoOuMortoService.start();
                    }
                }
            });
            ci.start();
        }
    },

    ATUALIZARVERSAOESTACAO("atualizarVersaoEstacao"){

        public void execute(String metodo, WebEngine engine){
            final ConexaoIntranetService ci = new ConexaoIntranetService();
            ci.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent workerStateEvent) {
                    Long resposta = ci.getValue();
                    if (resposta != ConexaoIntranetService.NAO_CONECTADO) {
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
            });
            ci.start();
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

