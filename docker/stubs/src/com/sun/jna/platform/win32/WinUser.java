package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;

/**
 * Stub das classes WinUser do JNA-Platform.
 * Implementacoes vazias apenas para compilacao. Veja com.sun.jna.Pointer.
 */
public class WinUser {

    public static class HHOOK {
        public HHOOK() {
        }
    }

    public static class KBDLLHOOKSTRUCT {
        public int vkCode;

        public KBDLLHOOKSTRUCT() {
        }

        public Pointer getPointer() {
            return new Pointer();
        }
    }

    public interface LowLevelKeyboardProc {
        WinDef.LRESULT callback(int nCode, WinDef.WPARAM wParam, KBDLLHOOKSTRUCT info);
    }

    public static class MSG {
        public MSG() {
        }
    }
}
