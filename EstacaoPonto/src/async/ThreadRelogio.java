package async;

import java.util.Calendar;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author Jainilene
 */
public class ThreadRelogio extends Service<String> {

    private String horarioAtual;
    private Calendar dataServidorInicial;
    private Calendar dataServidorAtual;
    private long tempoNanoServidorLigado;

    public ThreadRelogio(Calendar dtServidorInicial) {
        Calendar dt = Calendar.getInstance();
        dt.setTime(dtServidorInicial.getTime());
        this.dataServidorInicial = dt;
        this.dataServidorAtual = dtServidorInicial;
        this.tempoNanoServidorLigado = System.nanoTime();
        System.out.println("\n***CONTRUTOR\n");
    }

    private String incrementaHorario() {
        dataServidorAtual.set(Calendar.MINUTE, this.dataServidorAtual.get(Calendar.MINUTE) + 1);

        if (dataServidorAtual.get(Calendar.MINUTE) == 60) {
            dataServidorAtual.set(Calendar.HOUR_OF_DAY, this.dataServidorAtual.get(Calendar.HOUR_OF_DAY) + 1);
            dataServidorAtual.set(Calendar.MINUTE, 0);
        }
        if (dataServidorAtual.get(Calendar.MINUTE) < 10) {
            horarioAtual = dataServidorAtual.get(Calendar.HOUR_OF_DAY) + ":0" + dataServidorAtual.get(Calendar.MINUTE);
        } else {
            horarioAtual = dataServidorAtual.get(Calendar.HOUR_OF_DAY) + ":" + dataServidorAtual.get(Calendar.MINUTE);
        }
        return horarioAtual;

    }

    private String calculaHorario() {
        long nanoH = (long) 3600000000000.00;
        long nanoM = (long) 60000000000.00;

        long nanoS = System.nanoTime();
        long difTempo = nanoS - tempoNanoServidorLigado;

        long horaDecorrida = difTempo / nanoH;
        System.out.println("HORA DECORRIDA: " + horaDecorrida);
        long minutosDecorridos = (difTempo / nanoM) - horaDecorrida * 60;
        System.out.println("MINUTOS DECORRIDOS: " + minutosDecorridos);
        int somaMinutos = (int) (dataServidorInicial.get(Calendar.MINUTE) + minutosDecorridos);
        if (somaMinutos >= 60) {
            dataServidorAtual.set(Calendar.HOUR_OF_DAY, dataServidorInicial.get(Calendar.HOUR_OF_DAY) + (somaMinutos / 60));
            dataServidorAtual.set(Calendar.MINUTE, somaMinutos - 60);
        } else {
            dataServidorAtual.set(Calendar.MINUTE, somaMinutos);
            dataServidorAtual.set(Calendar.HOUR_OF_DAY, (int) (dataServidorInicial.get(Calendar.HOUR_OF_DAY) + horaDecorrida));
        }

        if (dataServidorAtual.get(Calendar.HOUR_OF_DAY) > 23) {
            dataServidorAtual.set(Calendar.DAY_OF_YEAR, dataServidorAtual.get(Calendar.DAY_OF_YEAR + 1));
        }
        if (dataServidorAtual.get(Calendar.MINUTE) < 10) {
            horarioAtual = dataServidorAtual.get(Calendar.HOUR_OF_DAY) + ":0" + dataServidorAtual.get(Calendar.MINUTE);
        } else {
            horarioAtual = dataServidorAtual.get(Calendar.HOUR_OF_DAY) + ":" + dataServidorAtual.get(Calendar.MINUTE);
        }
        System.out.println("HORARIO ATUAL: " + horarioAtual);
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
    public String getMomentoBatimento()
    {
        return dataServidorAtual.get(Calendar.DAY_OF_MONTH)+":"+dataServidorAtual.get(Calendar.MONTH) +":"+dataServidorAtual.get(Calendar.YEAR)+":"+horarioAtual;
    }
    public String getMomentoBatimentoFrequentador()
    {
        return dataServidorAtual.get(Calendar.DAY_OF_MONTH)+"/"+(dataServidorAtual.get(Calendar.MONTH)+1)+"/"+dataServidorAtual.get(Calendar.YEAR)+"-"+horarioAtual;
    }
}
