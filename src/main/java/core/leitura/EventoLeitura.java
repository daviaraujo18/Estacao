package core.leitura;

import utils.*;
import view.TelaPonto;

public enum EventoLeitura {
    NULO,


    DEDO_POSICINADO,

    LEITURA_EM_ANALISE,

    /**
     * Digital reconhecida: VerificacaoDigitalService já sincronizou o
     * registro imediatamente com o servidor (SincronizacaoImediata) — e só
     * grava na fila local (ArquivoRegistros) se essa sincronização tiver
     * falhado, como fallback pro ciclo periódico reenviar depois. Gravar
     * aqui também, incondicionalmente, fazia a MESMA batida ser enviada
     * duas vezes (uma na hora, outra no ciclo periódico), criando dois
     * TimeRecords fora de ordem e quebrando a alternância entrada/saída.
     * Aqui só falta a mesma confirmação visual do login manual: recarregar
     * a tela pra o servidor renderizar a batida recém-confirmada.
     */
    DIGITAL_RECONHECIDA{
        @Override
        public void process(TelaPonto tela, Leitura leitura) {
            LogEstacao.i("Digital Reconhecida -> " + leitura.getIdFrequentador() + "," + leitura.getMomento());
            The.inserirJavascript(tela.getWebEngine(), "window.location.reload()");
            tela.sound.playOK();
        }
    },

    /**
     * Login manual bem-sucedido: o ponto já foi sincronizado imediatamente
     * (ValidarBatidaManualService, mesmo endpoint/critérios do botão
     * "Simular digital" — PunchTypeService decide entrada/saída). Em vez de
     * montar dados pro process() em JS, só recarrega a página: o servidor já
     * renderiza quem bateu o ponto e o status atualizado.
     */
    LOGIN_MANUAL_SUCESSO{
        @Override
        public void process(TelaPonto tela, Leitura leitura) {
            The.inserirJavascript(tela.getWebEngine(), "window.location.reload()");
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
        public void process(TelaPonto tela, Leitura leitura) {
            LogEstacao.w("Registro com ressalva");
            The.inserirJavascript(tela.getWebEngine(), "window.location.reload()");
            tela.sound.playOK();
        }
    },
    USUARIO_SENHA_INVALIDOS{
        @Override
        public void after(TelaPonto tela) {
            LogEstacao.w("Usuário ou Senha Inválidos!");
            The.inserirJavascript(tela.getWebEngine(), "changeMensagemStatus('Usuário ou Senha Inválidos!')");
            tela.sound.playError();
        }
    },
    USUARIO_SEM_PERMISSAO_MANUAL{
        @Override
        public void after(TelaPonto tela) {

            LogEstacao.w("Usuário não tem autorização para registrar com login/senha. Entre em contato com a SEAD");
            The.inserirJavascript(tela.getWebEngine(), "changeMensagemStatus('Usuário sem permissão para login manual!')");
            tela.sound.playError();
        }
    },
    SEM_CONEXAO_TIMEOUT{
        @Override
        public void after(TelaPonto tela) {

            LogEstacao.w("Não foi possível conexão com Intranet - timeout");
            The.inserirJavascript(tela.getWebEngine(), "changeMensagemStatus('Sem conexão com o servidor!')");
            tela.sound.playError();
        }
    },
    ESTACAO_SEM_PERMISSAO_PARA_BATIDA_MANUAL{
        @Override
        public void after(TelaPonto tela) {

            LogEstacao.w("Estação não esta liberada para aceitar batidas com login/senha.");
            The.inserirJavascript(tela.getWebEngine(), "changeMensagemStatus('Estação não liberada para login manual!')");
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

}
