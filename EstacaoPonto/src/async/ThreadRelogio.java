package async;

import java.util.ArrayList;
import java.util.Calendar;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import utils.CalendarUtils;
import utils.Log;
import utils.The;

/**
 * Classe responsável por fazer o controle do horário. Calcula o horário atual e controla o tempo da sincronizacao.
 * @author Jainilene
 */
public class ThreadRelogio extends Service<String> {

    private Calendar dataRestartDiario;

    private String horarioAtual;
    private Calendar dataServidorInicial;
    private Calendar dataServidorAtual;
    private long tempoNanoServidorLigado;
    private static ArrayList<String> diasDaSemana = new ArrayList<String>();
    private static ArrayList<String> mesExtenso = new ArrayList<String>();

    private Calendar ultimaSincronizacao;
    public static boolean sincronizacaoAtiva;

    public ThreadRelogio(Calendar dtServidorInicial) {
        Calendar dt = Calendar.getInstance();
        dt.setTime(dtServidorInicial.getTime());
        this.dataServidorInicial = dt;
        this.dataServidorAtual = dtServidorInicial;
        this.tempoNanoServidorLigado = System.nanoTime();
        this.ultimaSincronizacao = dt;
        inicializaDiasDaSemana();
        inicializaMesExtenso();

        setarHorarioRestart();
    }

    private void setarHorarioRestart() {
        int random = The.getRandomNumberBetween(1, 420);
        dataRestartDiario = CalendarUtils.getHojeAs(22,0); // 22:00
        dataRestartDiario.add(Calendar.MINUTE, random);
    }

    /*
     * Retorna DiaDaSemana, DiaDoMes, Mes, Ano, Horario(HH:MM)
     */
    private String calculaHorario() {
//        Log.i("Calculando horário...");
        String dataCompleta = "";
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
        dataCompleta = diasDaSemana.get(dataServidorAtual.get(Calendar.DAY_OF_WEEK)-1)+","
        + dataServidorAtual.get(Calendar.DAY_OF_MONTH) +","
        + mesExtenso.get(dataServidorAtual.get(Calendar.MONTH))+","
        +dataServidorAtual.get(Calendar.YEAR)+","+horarioAtual;
//        Log.i("DataCompleta::: " + dataCompleta);
        return dataCompleta;
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
        return (Calendar) dataServidorAtual.clone();
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

    public String getMomentoAtual() {
        calculaHorario();
        return dataServidorAtual.get(Calendar.DAY_OF_MONTH) + ":" + dataServidorAtual.get(Calendar.MONTH) + ":" + dataServidorAtual.get(Calendar.YEAR) + ":"
                + horarioAtual + ":" + dataServidorAtual.get(Calendar.SECOND);
    }

//    public String getMomentoBatimentoFrequentador() {
//        return dataServidorAtual.get(Calendar.DAY_OF_MONTH) + "/" + (dataServidorAtual.get(Calendar.MONTH) + 1) + "/" + dataServidorAtual.get(Calendar.YEAR) + "-" + horarioAtual;
//    }

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
        //double h = difTempo / 3600000; //1hora
        double h = difTempo/300000; // 5 minutos
//        double h = difTempo/600000; // 10 minutos
        //double h = difTempo/120000; // 2 minutos
//        double h = difTempo/10000; // 1 minutos
        if (h >= 1) {
            return true;
        }
        return false;
    }


    public Calendar getDataRestartDiario() {
        return (Calendar) dataRestartDiario.clone();
    }

    public void inicializaDiasDaSemana()
    {
        diasDaSemana.add("Domingo");
        diasDaSemana.add("Segunda");
        diasDaSemana.add("Terça");
        diasDaSemana.add("Quarta");
        diasDaSemana.add("Quinta");
        diasDaSemana.add("Sexta");
        diasDaSemana.add("Sábado");
    }

    private void inicializaMesExtenso() {
        mesExtenso.add("Janeiro");
        mesExtenso.add("Fevereiro");
        mesExtenso.add("Março");
        mesExtenso.add("Abril");
        mesExtenso.add("Maio");
        mesExtenso.add("Junho");
        mesExtenso.add("Julho");
        mesExtenso.add("Agosto");
        mesExtenso.add("Setembro");
        mesExtenso.add("Outubro");
        mesExtenso.add("Novembro");
        mesExtenso.add("Dezembro");
    }
}
