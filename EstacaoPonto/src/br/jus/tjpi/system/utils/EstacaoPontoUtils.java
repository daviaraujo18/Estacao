package br.jus.tjpi.system.utils;


import com.ice.jni.registry.RegStringValue;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryKey;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.UUID;


/**
 *
 * @author Anderson Soares <aersandersonsoares@gmail.com>
 */
public class EstacaoPontoUtils {
    
    public static String getCodigoAtivacaoRegistro()  {
        try {
            RegistryKey localMachineReg = Registry.HKEY_LOCAL_MACHINE;
            RegistryKey softwareReg = localMachineReg.openSubKey("SOFTWARE",RegistryKey.ACCESS_ALL);
            RegistryKey estacaoPontoReg = softwareReg.openSubKey("TJPIEstacaoPonto",RegistryKey.ACCESS_READ);
            
            String valor = estacaoPontoReg.getStringValue("codigoAtivacao");
            
            estacaoPontoReg.closeKey();
            softwareReg.closeKey();
            localMachineReg.closeKey();
            
            return valor;
            
        } catch(Exception e) {
            return null;
        }

	}

        public static boolean registrarCodigoAtivacao(String codigoAtivacao) {
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
                return false;
            }
            
        }
        
	public static String getCodigoUnicoMaquina() {
		int valorHdSerial = Integer.parseInt(getHDSerial("c"));
        String hdSerial = Integer.toHexString(valorHdSerial);
        
        String serialCriptografado = CryptoUtils.md5UB64(hdSerial);
        System.out.println(serialCriptografado);
		return serialCriptografado;
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
        
        String codigoCriptografado = CryptoUtils.md5UB64(random);
        System.out.println(codigoCriptografado);
        
        return CryptoUtils.md5UB64(random);
    }
	
}
