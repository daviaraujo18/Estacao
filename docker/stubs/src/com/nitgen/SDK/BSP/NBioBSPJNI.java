package com.nitgen.SDK.BSP;

/**
 * Stub da classe NBioBSPJNI do SDK Nitgen.
 *
 * Implementacoes vazias (no-op) apenas para permitir a compilacao do projeto
 * EstacaoPonto em ambientes onde o JAR real (br.jus.tjpi:NBioBSPJNI:0.1-nitgen)
 * nao esta disponivel.
 *
 * Em producao (Windows com o leitor biometrico conectado), o JAR real deve
 * substituir este stub. Veja docker/README.md e lib/README.md.
 *
 * As classes internas nao-estaticas sao instanciadas via bsp.new X() e por isso
 * nao sao 'static'. As classes ERROR, FIR_PURPOSE e WINDOW_STYLE sao acessadas
 * estaticamente (NBioBSPJNI.ERROR.NBioAPIERROR_NONE, etc.) e por isso sao
 * 'static'.
 */
public class NBioBSPJNI {

    public NBioBSPJNI() {
    }

    // -----------------------------------------------------------------------
    // Classes internas (instanciadas via bsp.new X())
    // -----------------------------------------------------------------------

    public class IndexSearch {
        public IndexSearch() {
        }

        public class SAMPLE_INFO {
            public SAMPLE_INFO() {
            }
        }

        public class FP_INFO {
            public int ID;

            public FP_INFO() {
            }
        }

        public int AddFIR(INPUT_FIR fir, int id, SAMPLE_INFO sampleInfo) {
            return 0;
        }

        public int SaveDB(String path) {
            return 0;
        }

        public int LoadDB(String path) {
            return 0;
        }

        public int ClearDB() {
            return 0;
        }

        public void Identify(INPUT_FIR fir, int securityLevel, FP_INFO fpInfo, int timeout) {
        }
    }

    public class WINDOW_OPTION {
        public int WindowStyle;

        public WINDOW_OPTION() {
        }
    }

    public class INIT_INFO_0 {
        public int DefaultTimeout;

        public INIT_INFO_0() {
        }
    }

    public class DEVICE_ENUM_INFO {
        public DeviceInfo[] DeviceInfo;

        public DEVICE_ENUM_INFO() {
            DeviceInfo = new DeviceInfo[1];
            DeviceInfo[0] = new DeviceInfo();
        }

        public class DeviceInfo {
            public String NameID;
            public int Instance;

            public DeviceInfo() {
            }
        }
    }

    public class INPUT_FIR {
        public INPUT_FIR() {
        }

        public void SetTextFIR(FIR_TEXTENCODE fir) {
        }
    }

    public class FIR_TEXTENCODE {
        public String TextFIR;

        public FIR_TEXTENCODE() {
        }
    }

    public class FIR_HANDLE {
        public FIR_HANDLE() {
        }
    }

    public class FIR_PAYLOAD {
        public FIR_PAYLOAD() {
        }
    }

    // -----------------------------------------------------------------------
    // Classes estaticas com constantes
    // -----------------------------------------------------------------------

    public static class ERROR {
        public static final int NBioAPIERROR_NONE = 0;
        public static final int NBioAPIERROR_USER_CANCEL = -1;
        public static final int NBioAPIERROR_CAPTURE_TIMEOUT = -2;
        public static final int NBioAPIERROR_DEVICE_NOT_OPENED = -3;
        public static final int NBioAPIERROR_DEVICE_INIT_FAIL = -4;
        public static final int NBioAPIERROR_DEVICE_OPEN_FAIL = -5;
    }

    public static class FIR_PURPOSE {
        public static final int VERIFY = 0;
        public static final int ENROLL = 1;
    }

    public static class WINDOW_STYLE {
        public static final int POPUP = 0;
        public static final int INVISIBLE = 1;
        public static final int NO_WELCOME = 2;
    }

    // -----------------------------------------------------------------------
    // Metodos da API
    // -----------------------------------------------------------------------

    public void EnumerateDevice(DEVICE_ENUM_INFO info) {
    }

    public void OpenDevice() {
    }

    public void CloseDevice() {
    }

    public void Enroll(FIR_HANDLE handle, Object window) {
    }

    public void Capture(int purpose, FIR_HANDLE handle, int timeout, Object window,
                        WINDOW_OPTION option) {
    }

    public void GetTextFIRFromHandle(FIR_HANDLE handle, FIR_TEXTENCODE text) {
    }

    public void VerifyMatch(INPUT_FIR fir1, INPUT_FIR fir2, Boolean result, FIR_PAYLOAD payload) {
    }

    public boolean IsErrorOccured() {
        return false;
    }

    public int GetErrorCode() {
        return 0;
    }

    public void CheckFinger(Boolean result) {
    }

    public void SetInitInfo(INIT_INFO_0 info) {
    }
}
