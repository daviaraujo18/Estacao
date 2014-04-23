package hardware;

/**
 *
 * Classe que recupera o Serial do HD e da CPU
 * @author Anderson Soares
 */
import java.io.BufferedReader;  
import java.io.File;  
import java.io.FileWriter;  
import java.io.InputStreamReader;  
  
  
public class RecoverSerialHD {  
  
    public static String getHDSerial(String drive) {  
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
  
    public static String getCPUSerial() {  
        String result = "";  
        try {  
            File file = File.createTempFile("tmp", ".vbs");  
            file.deleteOnExit();  
            FileWriter fw = new java.io.FileWriter(file);  
  
            String vbs =  
                "On Error Resume Next \r\n\r\n" +  
                "strComputer = \".\"  \r\n" +  
                "Set objWMIService = GetObject(\"winmgmts:\" _ \r\n" +  
                "    & \"{impersonationLevel=impersonate}!\\\\\" & strComputer & \"\\root\\cimv2\") \r\n" +  
                "Set colItems = objWMIService.ExecQuery(\"Select * from Win32_Processor\")  \r\n " +  
                "For Each objItem in colItems\r\n " +  
                "    Wscript.Echo objItem.ProcessorId  \r\n " +  
                "    exit for  ' do the first cpu only! \r\n" +  
                "Next                    ";  
  
  
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
        if (result.trim().length() < 1 || result == null) {  
            result = "NO_CPU_ID";  
        }  
        return result.trim();  
    }  
  
    public static void main(String[] args) {  
        int valor = Integer.parseInt(getHDSerial("c"));
        Log.i("Serial do HD: " + Integer.toHexString(valor));
        Log.i("Serial da CPU: " + getCPUSerial());
    }  
  
}