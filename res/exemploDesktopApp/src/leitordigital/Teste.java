
import com.nitgen.SDK.BSP.NBioBSPJNI;
import com.nitgen.SDK.BSP.NBioBSPJNI.FIR_SECURITY_LEVEL;
import com.nitgen.SDK.BSP.NBioBSPJNI.FIR_TEXTENCODE;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Anderson Soares
 */
public class Teste {
    
    private static NBioBSPJNI bsp;
    private static NBioBSPJNI.DEVICE_ENUM_INFO deviceEnumInfo;
    private static NBioBSPJNI.WINDOW_OPTION winOption;
    private static NBioBSPJNI.INIT_INFO_0 init_info;
    
    
    public static void main(String[] args) {
        
        Teste teste = new Teste();
        
        
        while(true) {
            try {
                //Captura a primeira digital
                FIR_TEXTENCODE textSavedFIR = teste.capturarDigital();
                System.out.println("Digital 1 formato texto: "+textSavedFIR.TextFIR);

                NBioBSPJNI.INPUT_FIR inputFIR = bsp.new INPUT_FIR();
                inputFIR.SetTextFIR(textSavedFIR);

                //Captura a segunda digital
                FIR_TEXTENCODE textCapturedFIR = teste.capturarDigital();
                System.out.println("Digital 2 formato texto: "+textCapturedFIR.TextFIR);

                NBioBSPJNI.INPUT_FIR inputFIR2 = bsp.new INPUT_FIR();
                inputFIR2. SetTextFIR(textCapturedFIR);


                Boolean bResult = new Boolean(false);
                NBioBSPJNI.FIR_PAYLOAD payload = bsp.new FIR_PAYLOAD();


                bsp.VerifyMatch(inputFIR, inputFIR2, bResult, payload);
                if (bsp.IsErrorOccured() == false) {
                    if (bResult)
                        System.out.println("Verify OK - Payload: " + payload.GetText());
                    else
                        System.out.println("Verify Failed");
                }
            }catch(Exception e){
                System.out.println("Sem digital");
                System.gc();
            }
        }
        
    }
    
    /**
     * faz a captura da digital e retorna a mesma em formato texto
     * caso nao haja erro
     * @return digital formato texto(String)
     */
    public FIR_TEXTENCODE capturarDigital() throws Exception {
        abrirLeitor();
        
        
        NBioBSPJNI.FIR_HANDLE hSavedFIR = bsp.new FIR_HANDLE();
       
        bsp.Capture(NBioBSPJNI.FIR_PURPOSE.VERIFY, hSavedFIR, -1, null, winOption);
        
        //bsp.Enroll(hSavedFIR, null);
        
        if(bsp.IsErrorOccured()) {
            throw new Exception("Nao foi possivel achar o padrao de uma digital");
              //return capturarDigital();
        } else {
            //Recupera a digital em formato de texto
            NBioBSPJNI.FIR_TEXTENCODE textSavedFIR = bsp.new FIR_TEXTENCODE();
            bsp.GetTextFIRFromHandle(hSavedFIR, textSavedFIR);
            
            
            
            return textSavedFIR;
        }
    }

    /**
     * abre conexao com o leitor de digital
     */
    private void abrirLeitor() {
        bsp = new NBioBSPJNI(); // Declare NBioBSPJNI Class Object
        
        deviceEnumInfo = bsp.new DEVICE_ENUM_INFO();
        winOption = bsp.new WINDOW_OPTION();
        init_info = bsp.new INIT_INFO_0();
        
        int seguranca = NBioBSPJNI.FIR_SECURITY_LEVEL.LOWEST;
        init_info.SecurityLevel = seguranca;
        init_info.MaxFingersForEnroll = 1;
        
        winOption.WindowStyle = NBioBSPJNI.WINDOW_STYLE.INVISIBLE;
        winOption.WindowStyle |= NBioBSPJNI.WINDOW_STYLE.NO_WELCOME;
        
        // Enumerate device
        bsp.EnumerateDevice(deviceEnumInfo);
        
        bsp.OpenDevice(deviceEnumInfo.DeviceInfo[0].NameID, deviceEnumInfo.DeviceInfo[0].Instance);
        
    }

    /**
     * Fecha conexao com o leitor de digital
     */
    private void fecharLeitor() {
        bsp.CloseDevice(deviceEnumInfo.DeviceInfo[0].NameID,deviceEnumInfo.DeviceInfo[0].Instance);
        deviceEnumInfo = null;
        bsp = null;
    }
    
}
