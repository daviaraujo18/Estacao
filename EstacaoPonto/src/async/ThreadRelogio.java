package async;

import java.util.Calendar;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * Classe responsável por fazer o controle do horário. Calcula o horário atual e controla o tempo da sincronizacao.
 * @author Jainilene
 */
public class ThreadRelogio extends Service<String> {

    private String horarioAtual;
    private Calendar dataServidorInicial;
    private Calendar dataServidorAtual;
    private Calendar ultimaSincronizacao;
    private long tempoNanoServidorLigado;
    public static boolean sincronizacaoAtiva;

    public ThreadRelogio(Calendar dtServidorInicial) {
        Calendar dt = Calendar.getInstance();
        dt.setTime(dtServidorInicial.getTime());
        this.dataServidorInicial = dt;
        this.dataServidorAtual = dtServidorInicial;
        this.tempoNanoServidorLigado = System.nanoTime();
        this.ultimaSincronizacao = dt;
    }

    private String calculaHorario() {
        System.out.println("Calculando horário...");
        long nanoH = (long) 3600000000000.00;
        long nanoM = (long) 60000000000.00;
        long nanoSe = (long) 1000000000.00;

        long nanoS = System.nanoTime();
        long difTempo = nanoS - tempoNanoServidorLigado;

        int horaDecorrida = (int) (difTempo / nanoH);
        long minutosDecorridos = (difTempo / nanoM) - horaDecorrida * 60;
        int segundosDecorridos = (int) ((difTempo / nanoSe) - (horaDecorrida * 60 * 60) - (minutosDecorridos * 60));
        int somaMinutos = (int) (dataServidorInicial.get(Calendar.MINUTE) + minutosDecorridos);
        int restoMinutos = somaMinutos%60;
        Calendar aux = Calendar.getInstance();
        aux.setTime(dataServidorInicial.getTime());
        aux.add(Calendar.HOUR_OF_DAY, (horaDecorrida + (somaMinutos / 60)));
        aux.set(Calendar.MINUTE, restoMinutos);
        dataServidorAtual = aux;
        if (dataServidorAtual.get(Calendar.MINUTE) < 10) {
            horarioAtual = dataServidorAtual.get(Calendar.HOUR_OF_DAY) + ":0" + dataServidorAtual.get(Calendar.MINUTE);
        } else {
            horarioAtual = dataServidorAtual.get(Calendar.HOUR_OF_DAY) + ":" + dataServidorAtual.get(Calendar.MINUTE);
        }
        dataServidorAtual.set(Calendar.SECOND, segundosDecorridos);
        return horarioAtual;
    }

    public String atualizarRelogio() {
        return calculaHorario();
    }

    @Override
    protected Task<String> createTask() {
        return new Task() {
            @Override
            protected String call() {
                return atualizarRelogio();
            }
        };

    }

    public String getHorarioAtual() {
        return horarioAtual;
    }

    public Calendar getDataServidorInicial() {
        return dataServidorInicial;
    }

    public Calendar getDataServidorAtual() {
        return dataServidorAtual;
    }

    public int getMinutosServidorAtual() {
        return dataServidorAtual.get(Calendar.MINUTE);
    }

    public int getHoraServidorAtual() {
        return dataServidorAtual.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinutosServidorInicial() {
        return dataServidorInicial.get(Calendar.MINUTE);
    }

    public int getHoraServidorInicial() {
        return dataServidorInicial.get(Calendar.HOUR_OF_DAY);
    }

    public String getMomentoBatimento() {
        calculaHorario();
        return dataServidorAtual.get(Calendar.DAY_OF_MONTH) + ":" + dataServidorAtual.get(Calendar.MONTH) + ":" + dataServidorAtual.get(Calendar.YEAR) + ":" + horarioAtual + ":" + dataServidorAtual.get(Calendar.SECOND);
    }

    public String getMomentoBatimentoFrequentador() {
        return dataServidorAtual.get(Calendar.DAY_OF_MONTH) + "/" + (dataServidorAtual.get(Calendar.MONTH) + 1) + "/" + dataServidorAtual.get(Calendar.YEAR) + "-" + horarioAtual;
    }

    public Calendar getUltimaSincronizacao() {
        return ultimaSincronizacao;
    }

    public void setUltimaSincronizacao(Calendar ultimaSincronizacao) {
        this.ultimaSincronizacao = ultimaSincronizacao;
    }
    
    public void ativarSincronizacao()
    {
        sincronizacaoAtiva = true;
    }
      
    public void desativarSincronizacao()
    {
        sincronizacaoAtiva = false;
    }
      
    /*
     * Método verifica se já chegou o horário de fazer a sincronização (se já passou uma hora da última sincronização)
     * return false - Ainda não chegou o momento da sincronização
     *        true  - Chegou o momento da sincronização
     */
    public boolean fazerSincronizacao() {
        long difTempo = dataServidorAtual.getTimeInMillis() - ultimaSincronizacao.getTimeInMillis();
        System.out.println("difTempo: " + difTempo);
        //double h = difTempo / 3600000; //1hora
        //double h = difTempo/300000; // 5 minutos
        //double h = difTempo/120000; // 2 minutos
        double h = difTempo/60000; // 1 minutos
        if (h >= 1) {
            return true;
        }
        return false;
    }
}
