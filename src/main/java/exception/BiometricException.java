package exception;

public class BiometricException extends Exception{

    private static final long serialVersionUID = 1L;

    public BiometricException(String msg){
        super(msg);
    }

    public static class DevideNotConnectedException extends BiometricException{
        public DevideNotConnectedException() {
            super("Dispositivo não conectado ou com defeito.");
        }
    }

    public static class CaptureTimeOutException extends BiometricException{
        public CaptureTimeOutException() {
            super("Timeout de captura.");
        }
    }


    public static class InvalidDigitalDataException extends BiometricException{
        public InvalidDigitalDataException() {
            super("Digital inválida.");
        }
    }
    public static class MatchTimeoutException extends BiometricException{
        public MatchTimeoutException() {
            super("Tempo para realização de match excedido.");
        }
    }
    public static class CanceledOperationException extends BiometricException{
        public CanceledOperationException() {
            super("Operação cancelada pelo usário.");
        }
    }
}
