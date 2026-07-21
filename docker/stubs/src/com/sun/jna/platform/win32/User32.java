package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;

/**
 * Stub da classe User32 do JNA-Platform.
 * Implementacao vazia apenas para compilacao. Veja com.sun.jna.Pointer.
 *
 * Em producao (Windows) a instancia real e obtida via JNA Native.loadLibrary.
 * Aqui INSTANCE e uma instancia no-op do stub.
 */
public class User32 {

    public static final User32 INSTANCE = new User32();

    public WinUser.HHOOK SetWindowsHookEx(int idHook, WinUser.LowLevelKeyboardProc lpfn,
                                          WinDef.HMODULE hMod, int dwThreadId) {
        return new WinUser.HHOOK();
    }

    public WinDef.LRESULT CallNextHookEx(WinUser.HHOOK hhk, int nCode,
                                         WinDef.WPARAM wParam, Pointer lParam) {
        return new WinDef.LRESULT();
    }

    public void UnhookWindowsHookEx(WinUser.HHOOK hhk) {
    }

    public int GetMessage(WinUser.MSG msg, Object hwnd, int filterMin, int filterMax) {
        return 0;
    }

    public void TranslateMessage(WinUser.MSG msg) {
    }

    public void DispatchMessage(WinUser.MSG msg) {
    }
}
