package net.maliniak;


import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

public class RemoteApi {
    public static void main(String[] args) {
        final User32 instance = User32.INSTANCE;
        boolean b = instance.EnumWindows(new WinUser.WNDENUMPROC() {
            public boolean callback(WinDef.HWND hwnd, Pointer pointer) {
                System.out.println(hwnd);
                char[] chars = new char[256];
                instance.GetWindowText(hwnd, chars, 256);
                String text = Native.toString(chars);
                System.out.println(text);

                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return true;
            }
        }, null);

//        instance.EnumChildWindows()

        System.out.println("CHUJ");
//        User32.INSTANCE.
//        System.out.println(hwnd);
    }
}
