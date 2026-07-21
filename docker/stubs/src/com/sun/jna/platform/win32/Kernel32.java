package com.sun.jna.platform.win32;

/**
 * Stub da interface Kernel32 do JNA-Platform.
 * Implementacao vazia apenas para compilacao. Veja com.sun.jna.Pointer.
 *
 * Em producao (Windows) a instancia real e obtida via JNA Native.loadLibrary.
 * Aqui INSTANCE e uma instancia no-op do stub.
 */
public class Kernel32 {

    public static final Kernel32 INSTANCE = new Kernel32();

    public WinDef.HMODULE GetModuleHandle(String name) {
        return new WinDef.HMODULE();
    }
}
