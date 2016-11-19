package utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarUtils {

    public static Calendar getHojeAs(int horas, int minutos) {
        Calendar calendar = Calendar.getInstance();
        calendar = zerarTime(calendar);

        return getHojeAs(horas, minutos, 0);
    }

    public static Calendar getHojeAs(int horas, int minutos, int segundos) {
        Calendar calendar = Calendar.getInstance();
        calendar = zerarTime(calendar);

        calendar.set(Calendar.HOUR_OF_DAY, horas);
        calendar.set(Calendar.MINUTE, minutos);
        calendar.set(Calendar.SECOND, segundos);


        return calendar;
    }

    private static Calendar zerarTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal;
    }

    public static String format(Calendar dataRestartDiario) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        return sdf.format(dataRestartDiario.getTime());
    }

    public static boolean temMesmoHorario(Calendar dataServidorAtual, Calendar dataRestartDiario) {
        boolean horaEhIgual = dataRestartDiario.get(Calendar.HOUR_OF_DAY) == dataServidorAtual.get(Calendar.HOUR_OF_DAY);
        boolean minutoEhIgual = dataRestartDiario.get(Calendar.MINUTE) == dataServidorAtual.get(Calendar.MINUTE);

        return (horaEhIgual && minutoEhIgual);
    }
}

