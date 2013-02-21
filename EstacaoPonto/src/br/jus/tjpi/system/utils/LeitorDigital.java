package br.jus.tjpi.system.utils;


import com.nitgen.SDK.BSP.NBioBSPJNI;
import com.nitgen.SDK.BSP.NBioBSPJNI.DEVICE_ENUM_INFO;
import com.nitgen.SDK.BSP.NBioBSPJNI.FIR_TEXTENCODE;
import com.nitgen.SDK.BSP.NBioBSPJNI.WINDOW_OPTION;
import java.lang.reflect.Field;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Anderson Soares
 */
public class LeitorDigital {
    
    private static NBioBSPJNI bsp;
	private static NBioBSPJNI.INIT_INFO_0 initInfo;
    private static NBioBSPJNI.DEVICE_ENUM_INFO deviceEnumInfo;
    private static NBioBSPJNI.WINDOW_OPTION winOption;
    
    public LeitorDigital() {}
    
	/**
	 * Metodo para fazer a leitura das digitais do usuario
	 * Uma janela aparecerá para o cadastrador selecionar os dedos
	 * que serao cadastrados
	 * 
	 * @return hashDasDigitais
	 */
	public String enroll() throws Exception {
		abrirLeitor();
		
		NBioBSPJNI.FIR_HANDLE hSavedFIR = bsp.new FIR_HANDLE();
		bsp.Enroll(hSavedFIR, null);
//		bsp.RollCapture(NBioBSPJNI.FIR_PURPOSE.ENROLL, hSavedFIR, -1, null, winOption);
		
		if(bsp.IsErrorOccured()) {
			
			throwError();
			return null;
			
		} else {
			//Recupera a digital em formato de texto
            NBioBSPJNI.FIR_TEXTENCODE textSavedFIR = bsp.new FIR_TEXTENCODE();
            bsp.GetTextFIRFromHandle(hSavedFIR, textSavedFIR);
            
            fecharLeitor();
            
            return textSavedFIR.TextFIR;
		}
	}
    
    /**
     * faz a captura da digital e retorna a mesma em formato texto
     * caso nao haja erro
     * @return digital formato texto(String)
     */
    public String capturarDigital() throws Exception {
        abrirLeitor();
        
        NBioBSPJNI.FIR_HANDLE hSavedFIR = bsp.new FIR_HANDLE();
       
        bsp.Capture(NBioBSPJNI.FIR_PURPOSE.VERIFY, hSavedFIR, -1, null, winOption);
        //bsp.Capture(hSavedFIR);
        
        if(bsp.IsErrorOccured()) {
            throwError();
			return null;
        } else {
            //Recupera a digital em formato de texto
            NBioBSPJNI.FIR_TEXTENCODE textSavedFIR = bsp.new FIR_TEXTENCODE();
            bsp.GetTextFIRFromHandle(hSavedFIR, textSavedFIR);
            
            fecharLeitor();
            
            return textSavedFIR.TextFIR;
        }
    }
    
    /**
     * abre conexao com o leitor de digital
     */
    private void abrirLeitor() {
        bsp = new NBioBSPJNI(); // Declare NBioBSPJNI Class Object
        
		// Setar timeout do leitorDigital
		initInfo = bsp.new INIT_INFO_0();
		initInfo.DefaultTimeout = 2000;
		bsp.SetInitInfo(initInfo);
		
        deviceEnumInfo = bsp.new DEVICE_ENUM_INFO();
        winOption = bsp.new WINDOW_OPTION();
        
        winOption.WindowStyle = NBioBSPJNI.WINDOW_STYLE.INVISIBLE;
        //winOption.WindowStyle |= NBioBSPJNI.WINDOW_STYLE.NO_WELCOME;
        
        
        // Enumerate device
        bsp.EnumerateDevice(deviceEnumInfo);
        
        bsp.OpenDevice(deviceEnumInfo.DeviceInfo[0].NameID, deviceEnumInfo.DeviceInfo[0].Instance);
        
    }
    
     private void fecharLeitor() {
        bsp.CloseDevice(deviceEnumInfo.DeviceInfo[0].NameID,deviceEnumInfo.DeviceInfo[0].Instance);
        deviceEnumInfo = null;
        bsp = null;
    }

    public boolean verificaCompatibilidadeDigitais(String digital, String digitalCapturada) {
        
        abrirLeitor();
        
        NBioBSPJNI.INPUT_FIR firDigital = bsp.new INPUT_FIR();
        NBioBSPJNI.INPUT_FIR firDigitalCapturada = bsp.new INPUT_FIR();
        
        NBioBSPJNI.FIR_PAYLOAD firPayload = bsp.new FIR_PAYLOAD();
        
        NBioBSPJNI.FIR_TEXTENCODE firDigitalTexto = bsp.new FIR_TEXTENCODE();
        NBioBSPJNI.FIR_TEXTENCODE firDigitalCapturadaTexto = bsp.new FIR_TEXTENCODE();
        
        firDigitalTexto.TextFIR = digital;
        firDigitalCapturadaTexto.TextFIR = digitalCapturada;
        
        firDigital.SetTextFIR(firDigitalTexto);
        firDigitalCapturada.SetTextFIR(firDigitalCapturadaTexto);
        
        Boolean bResult = false;
        
        bsp.VerifyMatch(firDigital, firDigitalCapturada, bResult, firPayload);
        
        boolean ocorreuErro = bsp.IsErrorOccured();
        fecharLeitor();
        if(ocorreuErro == false) {
            // nao teve erro
            if(bResult) { 
                System.out.println("INFO: Digitais conferem");
                return true;
            }
            else {
                System.out.println("INFO: Digitais nao conferem");
                return false;
            }
        } else {
            System.out.println("INFO: Erro na Verificacao. Verifique o Hardware");
            return false;
        }
    }

	/**
	 * Método para pegar o nome do error, pois nao consegui recuperar o nome
	 * atraves da NBioBSPJNI
	 * @aers
	 */
	private void throwError() throws IllegalArgumentException, IllegalAccessException, Exception {
		int errorNumber = bsp.GetErrorCode();
		String errorName = null;
		Class aClass = NBioBSPJNI.ERROR.class;
		Field[] fields = aClass.getFields();
		for (Field field : fields) {
			if((int) field.get(null) == errorNumber) {
				errorName = field.getName();
				break;
			}
		}
		throw new Exception("Erro: "+errorName);
	}
	
}
