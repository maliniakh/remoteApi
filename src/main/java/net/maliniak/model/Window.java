package net.maliniak.model;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

/**
 * Created by llmali on 27/11/2015.
 */
public class Window {
    private String id;
    private int x;
    private int y;
    private int width;
    private int height;
    private String title;

    public Window(WinDef.HWND hwnd) {
        char[] chars = new char[256];
        User32.INSTANCE.GetWindowText(hwnd, chars, 256);
        String title = Native.toString(chars);
        this.title = title;

        WinDef.RECT rect = new WinDef.RECT();
        User32.INSTANCE.GetWindowRect(hwnd, rect);
        this.x = rect.left;
        this.y = rect.top;
        this.height = rect.top - rect.bottom;
        this.width = rect.right - rect.left;
    }

    public String getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
