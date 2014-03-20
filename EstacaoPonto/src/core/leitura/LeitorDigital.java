package core.leitura;


import utils.Log;
import com.nitgen.SDK.BSP.NBioBSPJNI;
import com.nitgen.SDK.BSP.NBioBSPJNI.IndexSearch;
import com.nitgen.SDK.BSP.NBioBSPJNI.WINDOW_OPTION;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;


/**
 * Classe responsavel por fazer a conexao da core.EstacaoPonto com o dispositivo biometrico
 * @author Anderson Soares
 */
public class LeitorDigital {

    public static NBioBSPJNI bsp;
    private IndexSearch indexSearchEngine;
    private static NBioBSPJNI.INIT_INFO_0 initInfo;
    private static NBioBSPJNI.DEVICE_ENUM_INFO deviceEnumInfo;
    private static NBioBSPJNI.WINDOW_OPTION winOption;

    public static boolean ativo;

    private static LeitorDigital INSTANCE = null;
    private LeitorDigital() {
        INSTANCE = this;
        abrirLeitor();
        indexSearchEngine = bsp.new IndexSearch();
        fecharLeitor();
    }

    public static LeitorDigital getInstance(){
        if(INSTANCE == null){
            INSTANCE = new LeitorDigital();
        }
        return INSTANCE;
    }

    public IndexSearch getIndexSearchEngine() {
        return indexSearchEngine;
    }

    public void addDigitalToIndexSearch(Map<String,String> mapaIdHashFrequentadores) {

        Log.i("Guardando frequentadores na memória...");

        double inicioMontagem = System.currentTimeMillis();

        abrirLeitor();

        NBioBSPJNI.IndexSearch.SAMPLE_INFO sampleInfo = indexSearchEngine.new SAMPLE_INFO();
        NBioBSPJNI.INPUT_FIR firDigital;
        NBioBSPJNI.FIR_TEXTENCODE firDigitalTexto;
        Iterator<String> mapaIterator = mapaIdHashFrequentadores.keySet().iterator();

        while(mapaIterator.hasNext()) {
            String idFrequentador = mapaIterator.next();
            String digitalFrequentador = mapaIdHashFrequentadores.get(idFrequentador);
            firDigital = bsp.new INPUT_FIR();
            firDigitalTexto = bsp.new FIR_TEXTENCODE();
            firDigitalTexto.TextFIR = digitalFrequentador;
            firDigital.SetTextFIR(firDigitalTexto);
            indexSearchEngine.AddFIR(firDigital, Integer.parseInt(idFrequentador), sampleInfo);
        }

        fecharLeitor();

        double fimMontagem = System.currentTimeMillis();
        Log.i("Montagem finalizada");
        Log.i("Time elapsed: "+(fimMontagem - inicioMontagem)+" ms");

    }

    public int searchDigitalOnIndexSearchEngine(String hashDigital) throws Exception {

        abrirLeitor();

        NBioBSPJNI.IndexSearch.FP_INFO fpInfo = indexSearchEngine.new FP_INFO();

        NBioBSPJNI.INPUT_FIR firDigital = bsp.new INPUT_FIR();
        NBioBSPJNI.FIR_TEXTENCODE firDigitalTexto = bsp.new FIR_TEXTENCODE();
        firDigitalTexto.TextFIR = hashDigital;
        firDigital.SetTextFIR(firDigitalTexto);

        // 0 = maxSearchTime
        indexSearchEngine.Identify(firDigital,NBioBSPJNI.FIR_SECURITY_LEVEL.NORMAL, fpInfo, 5000);
        if(bsp.IsErrorOccured()) {
            throwError();
            fecharLeitor();
            return -1;
        }

        fecharLeitor();

        return fpInfo.ID;
    }


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
            fecharLeitor();
            return null;

        } else {
            //Recupera a digital em formato de texto
            NBioBSPJNI.FIR_TEXTENCODE textSavedFIR = bsp.new FIR_TEXTENCODE();
            bsp.GetTextFIRFromHandle(hSavedFIR, textSavedFIR);

            fecharLeitor();

            return textSavedFIR.TextFIR;
        }
    }

    public String capturarDigital_popup() throws Exception {
        this.abrirLeitor();
        WINDOW_OPTION window_option = bsp.new WINDOW_OPTION();
        window_option.WindowStyle = NBioBSPJNI.WINDOW_STYLE.POPUP;
        String digital = capturarDigital(window_option);
        this.fecharLeitor();
        return digital; //digital;

    }

    public String capturarDigital() throws Exception {
        return capturarDigital(winOption);
    }

    /**
     * faz a captura da digital e retorna a mesma em formato texto
     * caso nao haja erro
     * @return digital formato texto(String)
     */
    public String capturarDigital( WINDOW_OPTION window_option) throws Exception {
        NBioBSPJNI.FIR_HANDLE hSavedFIR = bsp.new FIR_HANDLE();
        bsp.Capture(NBioBSPJNI.FIR_PURPOSE.VERIFY, hSavedFIR, -1, null, window_option);
        if(bsp.IsErrorOccured()) {
            throwError();
            return null;
        } else {
            //Recupera a digital em formato de texto
            NBioBSPJNI.FIR_TEXTENCODE textSavedFIR = bsp.new FIR_TEXTENCODE();
            bsp.GetTextFIRFromHandle(hSavedFIR, textSavedFIR);
            return textSavedFIR.TextFIR;
        }
    }

    /**
     * abre conexao com o leitor de digital
     */
    public void abrirLeitor() {
        bsp = new NBioBSPJNI();
        // Setar timeout do leitorDigital
//		initInfo = bsp.new INIT_INFO_0();
//		initInfo.DefaultTimeout = 2000;
//		bsp.SetInitInfo(initInfo);

        deviceEnumInfo = bsp.new DEVICE_ENUM_INFO();
        winOption = bsp.new WINDOW_OPTION();

        winOption.WindowStyle = NBioBSPJNI.WINDOW_STYLE.INVISIBLE;
        //winOption.WindowStyle |= NBioBSPJNI.WINDOW_STYLE.NO_WELCOME;

        // Enumerate device
        bsp.EnumerateDevice(deviceEnumInfo);

        //bsp.OpenDevice(deviceEnumInfo.DeviceInfo[0].NameID, deviceEnumInfo.DeviceInfo[0].Instance);
        ativo = true;
        bsp.OpenDevice();
    }
    private void configCadastro() {

    }
    private void config() {

    }

    public void fecharLeitor() {
        //bsp.CloseDevice(deviceEnumInfo.DeviceInfo[0].NameID,deviceEnumInfo.DeviceInfo[0].Instance);
        bsp.CloseDevice();
        deviceEnumInfo = null;
        ativo = false;
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
        System.out.println("Error code: "+bsp.GetErrorCode());
        String errorName = null;
        Class aClass = NBioBSPJNI.ERROR.class;
        Field[] fields = aClass.getFields();
        for (Field field : fields) {
            if(new Integer(field.get(null).toString()) == errorNumber) {
                errorName = field.getName();
                break;
            }
        }
        fecharLeitor();
        throw new Exception("Erro: "+errorName);
    }

    public boolean temDedo()
    {
        Boolean temDedo =false;
        bsp.CheckFinger(temDedo);
        return temDedo;
    }

}
