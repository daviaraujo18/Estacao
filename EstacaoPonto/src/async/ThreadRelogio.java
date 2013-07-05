package async;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author Jainilene
 */
public class ThreadRelogio extends Service<String> {

    private int horaAtual;
    private int minutosAtual;
    private String horarioAtual;
    private int horaServidorInicial;
    private int minutosServidorInicial;
    private long tempoNanoServidorLigado;

    public ThreadRelogio(int horaServidorInicial, int minutosServidorInicial) {
        this.horaServidorInicial = horaServidorInicial;
        this.horaAtual = horaServidorInicial;
        this.minutosServidorInicial = minutosServidorInicial;
        this.minutosAtual = minutosServidorInicial;
        this.tempoNanoServidorLigado = System.nanoTime();
    }
    
    private String incrementaHorario()
    {
        this.minutosAtual+=1;
        if(minutosAtual==60)
        {
            horaAtual+=1;
            minutosAtual=0;
        }
        horarioAtual = horaAtual+":"+minutosAtual;
        return horarioAtual;
        
    }
    private String calculaHorario()
    {
        long nanoH = (long) 3600000000000.00;
        long nanoM = (long) 60000000000.00;
        
        long nanoS = System.nanoTime();
        long difTempo = nanoS - tempoNanoServidorLigado;
        
        long horaDecorrida = difTempo/nanoH;
        long minutosDecorridos = (difTempo/nanoM) - horaDecorrida*60;
        
         int somaMinutos= (int) (minutosServidorInicial+minutosDecorridos);
         if (somaMinutos>= 60) {
            horaAtual = horaServidorInicial + (somaMinutos/60);
            minutosAtual = somaMinutos-60;
         }
         else
         {
             minutosAtual=somaMinutos;
             horaAtual = (int) (horaServidorInicial + horaDecorrida);
         }
         
         this.horarioAtual = horaAtual + ":" + minutosAtual;
         return horarioAtual;
        
    }

    public String atualizarRelogio() {        
        return incrementaHorario();
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
}
