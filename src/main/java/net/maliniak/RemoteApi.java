package net.maliniak;


import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

public class RemoteApi {
    public static void main(String[] args) {
        WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        System.out.println(hwnd);
    }
}
