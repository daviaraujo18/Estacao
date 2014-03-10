package core;


import com.ice.jni.registry.RegStringValue;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryKey;
import java.util.UUID;
import utils.CryptoUtils;


/**
 * Classe responsavel por fazer a conexao da EstacaoPonto com o Registro do Windows
 * @author Anderson Soares <aersandersonsoares@gmail.com>
 */
public class RegistroWindows {
    
    public static String getCodigoAtivacaoRegistro()  {
        
        if(OSVerifier.isWindows()) {
            try {
                RegistryKey localMachineReg = Registry.HKEY_LOCAL_MACHINE;
                RegistryKey softwareReg =  localMachineReg.openSubKey("SOFTWARE",RegistryKey.ACCESS_READ);
                RegistryKey estacaoPontoReg = softwareReg.openSubKey("TJPIEstacaoPonto",RegistryKey.ACCESS_READ);

                String valor = estacaoPontoReg.getStringValue("codigoAtivacao");

                estacaoPontoReg.closeKey();
                softwareReg.closeKey();
                localMachineReg.closeKey();
                System.out.println("\\Estação >> busca do código de ativação: " +valor);
                return valor;

            } catch(Exception e) {
                System.out.println("\\Estação >> ERRO na busca do código de ativação.");
                System.out.println("\\Estação >>" + e.getMessage() + " Causa: "+e.getCause().toString()+" Nome da classe: "+ e.getCause().getClass().getName());
                e.printStackTrace();
                return null;
            }
        } else {
            return "SistemaOperacionalNaoSuportado";
        }
    }

    public static boolean registrarCodigoAtivacao(String codigoAtivacao) {
		if(OSVerifier.isWindows()) {
            try {
                RegistryKey localMachineReg = Registry.HKEY_LOCAL_MACHINE;
                RegistryKey softwareReg = localMachineReg.openSubKey("SOFTWARE",RegistryKey.ACCESS_ALL);
                
                RegistryKey estacaoPontoReg = softwareReg.createSubKey("TJPIEstacaoPonto", "REG_SZ");

                RegStringValue valor = new RegStringValue(estacaoPontoReg,"codigoAtivacao");
                valor.setData(codigoAtivacao);
                estacaoPontoReg.setValue(valor);

                estacaoPontoReg.closeKey();
                softwareReg.closeKey();
                localMachineReg.closeKey();

                return true;
                
            } catch(Exception e) {
                System.out.println("Erro ao criar registro do windows: "+e.getMessage());
                return false;
            }
		} else {
			return true;
		}
    }
        
	public static String getCodigoUnicoMaquina() {
		
		if(OSVerifier.isWindows()) {
			
			String hdSerial = jWMI.montaCodAtivacao();
                        if (hdSerial.isEmpty())
                        {
                            return "Erro na construção do código de ativação.";
                        }
                                
			String serialCriptografado = CryptoUtils.md5UB64(hdSerial);
                        System.out.println("\n*HDSERIAL: " + hdSerial);
			System.out.println("\n**SERIAL CRIPTOGRAFADO: "+serialCriptografado);
			return serialCriptografado;
		} else {
			return "SistemaOperacionalNaoSuportado";
		}
	}
    
        
    /**
     * Metodo que gera uma string randomica de tamanho 6
     * que servirar como codigo de ativacao
     * @return 
     */
    public static String gerarCodigoAtivacao() {
        UUID uuid = UUID.randomUUID();
        String random = uuid.toString();
        
        return CryptoUtils.md5UB64(random);
    }

}
