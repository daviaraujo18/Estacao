package core;


import utils.CryptoUtils;
import com.ice.jni.registry.RegStringValue;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryKey;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.UUID;


/**
 * Classe responsavel por fazer a conexao da EstacaoPonto com o Registro do Windows
 * @author Anderson Soares <aersandersonsoares@gmail.com>
 */
public class RegistroWindows {
    
    public static String getCodigoAtivacaoRegistro()  {
        
        if(OSVerifier.isWindows()) {
            try {
                RegistryKey localMachineReg = Registry.HKEY_LOCAL_MACHINE;
                RegistryKey softwareReg =  localMachineReg.openSubKey("SOFTWARE",RegistryKey.ACCESS_ALL);
                RegistryKey estacaoPontoReg = softwareReg.openSubKey("TJPIEstacaoPonto",RegistryKey.ACCESS_READ);

                String valor = estacaoPontoReg.getStringValue("codigoAtivacao");

                estacaoPontoReg.closeKey();
                softwareReg.closeKey();
                localMachineReg.closeKey();

                return valor;

            } catch(Exception e) {
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
			int valorHdSerial = Integer.parseInt(getHDSerial("c"));
			String hdSerial = Integer.toHexString(valorHdSerial);

			String serialCriptografado = CryptoUtils.md5UB64(hdSerial);
                        System.out.println("\n*HDSERIAL: " + hdSerial);
			System.out.println("\n**SERIAL CRIPTOGRAFADO: "+serialCriptografado);
			return serialCriptografado;
		} else {
			return "SistemaOperacionalNaoSuportado";
		}
	}
    
    
    private static String getHDSerial(String drive) {  
        String result = "";  
        try {  
            //File file = File.createTempFile("tmp",".vbs");  
            File file = File.createTempFile("tmp", ".vbs");  
            file.deleteOnExit();  
            FileWriter fw = new java.io.FileWriter(file);  
  
            String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n" + "Set colDrives = objFSO.Drives\n"   
                            + "Set objDrive = colDrives.item(\"" + drive + "\")\n" + "Wscript.Echo objDrive.SerialNumber";    
            fw.write(vbs);  
            fw.close();  
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());  
            BufferedReader input =  
                new BufferedReader(new InputStreamReader(p.getInputStream()));  
            String line;  
            while ((line = input.readLine()) != null) {  
                result += line;  
            }  
            input.close();  
        } catch (Exception e) {  
  
        }  
        if (result.trim().length() < 1  || result == null) {  
            result = "NO_DISK_ID";  
  
        }  
  
        return result.trim();  
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
