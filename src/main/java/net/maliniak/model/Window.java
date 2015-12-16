package net.maliniak.model;

import java.io.Serializable;

/**
 * Created by llmali on 27/11/2015.
 */
@SuppressWarnings("unused")
public class Window implements Serializable {
    private Long hwndPeer;
    private int x;
    private int y;
    private int width;
    private int height;
    private String title;
    private Integer processId;

//    public Window(WinDef.HWND hwnd) {
////        this.hwnd = hwnd;
//
//        char[] chars = new char[256];
//        User32.INSTANCE.GetWindowText(hwnd, chars, 256);
//        String title = Native.toString(chars);
//        this.title = title;
//
//        WinDef.RECT rect = new WinDef.RECT();
//        User32.INSTANCE.GetWindowRect(hwnd, rect);
//        this.x = rect.left;
//        this.y = rect.top;
//        this.height = rect.bottom - rect.top;
//        this.width = rect.right - rect.left;
//
//        IntByReference intByRef = new IntByReference();
//        User32.INSTANCE.GetWindowThreadProcessId(hwnd, intByRef);
//        this.processId = intByRef.getValue();
//    }

    public Window(Long hwndPeer, int x, int y, int width, int height, String title, Integer processId) {
        this.hwndPeer = hwndPeer;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.title = title;
        this.processId = processId;
    }

    public String getTitle() {
        return title;
    }

    public Long getHwndPeer() {
        return hwndPeer;
    }

    //    public WinDef.HWND getHwnd() {
//        return hwnd;
//    }

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

    public Integer getProcessId() {
        return processId;
    }

    @Override
    public String toString() {
        return "Window{" +
//                "hwnd=" + hwnd +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", title='" + title + '\'' +
                ", processId=" + processId +
                '}';
    }
}
