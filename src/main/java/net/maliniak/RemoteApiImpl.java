package net.maliniak;

import com.google.common.collect.Iterables;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import jna.extra.GDI32Extra;
import jna.extra.User32Extra;
import jna.extra.WinGDIExtra;
import net.maliniak.model.Site;
import net.maliniak.model.Window;
import net.sf.lipermi.exception.LipeRMIException;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Server;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.Memory;

import javax.swing.*;

public class RemoteApiImpl implements RemoteApi {
    final static User32 instance = User32.INSTANCE;

    @Override
    public Window getUniqueWindow(Site site) {
        final String regex = getTitleRegex(site);
        return getUniqueWindow(regex);
    }

    @Override
    public Window getUniqueWindow(String titleRegex) {
        final Pattern pattern = Pattern.compile(titleRegex);

        final List<WinDef.HWND> candidateWindows = new ArrayList<>();

        instance.EnumWindows((hwnd, pointer) -> {
            String title = WindowUtils.getWindowTitle(hwnd);

            Matcher matcher = pattern.matcher(title);
            if (matcher.matches()) {
                candidateWindows.add(hwnd);
                System.out.println(title);
            }

            return true;
        }, null);

        WinDef.HWND hwnd = Iterables.getOnlyElement(candidateWindows);



//            long start = System.currentTimeMillis();
//            BufferedImage capture = capture(hwnd);
//            System.out.println(System.currentTimeMillis() - start);
//        System.out.println("capture: " + capture);

//        JFrame frame = new JFrame();
//        frame.getContentPane().setLayout(new FlowLayout());
//        frame.getContentPane().add(new JLabel(new ImageIcon(capture)));
//        frame.pack();
//        frame.setVisible(true);

        return createWindow(hwnd);
    }

    @Override
    public List<Window> getWindows(Integer processId, String titleRegex) {
        final List<Window> result = new ArrayList<>();

        final Pattern pattern = titleRegex != null ? Pattern.compile(titleRegex) : null;
        instance.EnumWindows((hwnd, pointer) -> {
            IntByReference intByRef = new IntByReference();
            instance.GetWindowThreadProcessId(hwnd, intByRef);

            if (intByRef.getValue() == processId) {
                if(pattern != null) {
                    String title = WindowUtils.getWindowTitle(hwnd);
                    if (pattern.matcher(title).matches()) {
                        result.add(createWindow(hwnd));
                    }
                } else {
                    result.add(createWindow(hwnd));
                }
            }

            return true;
        }, null);

        return result;
    }

//    @Override
    public List<Window> getChildWindows(WinDef.HWND hwnd, String titleRegex) throws RemoteException {
        throw new UnsupportedOperationException();
    }

    /**
     * Seems like first call is slow (~200ms), but subsequent ones are way faster
     * @param hwndPeer
     * @return
     */
    @Override
    public BufferedImage capture(Long hwndPeer) {
        WinDef.HWND hwnd = new WinDef.HWND(new Pointer(hwndPeer));

        User32.HDC hdcWindow = User32.INSTANCE.GetDC(hwnd);
        User32.HDC hdcMemDC = GDI32.INSTANCE.CreateCompatibleDC(hdcWindow);

        User32.RECT bounds = new User32.RECT();
        User32Extra.INSTANCE.GetClientRect(hwnd, bounds);

        int width = bounds.right - bounds.left;
        int height = bounds.bottom - bounds.top;

        User32.HBITMAP hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcWindow, width, height);

        User32.HANDLE hOld = GDI32.INSTANCE.SelectObject(hdcMemDC, hBitmap);
        GDI32Extra.INSTANCE.BitBlt(hdcMemDC, 0, 0, width, height, hdcWindow, 0, 0, WinGDIExtra.SRCCOPY);

        GDI32.INSTANCE.SelectObject(hdcMemDC, hOld);
        GDI32.INSTANCE.DeleteDC(hdcMemDC);

        BITMAPINFO bmi = new BITMAPINFO();
        bmi.bmiHeader.biWidth = width;
        bmi.bmiHeader.biHeight = -height;
        bmi.bmiHeader.biPlanes = 1;
        bmi.bmiHeader.biBitCount = 32;
        bmi.bmiHeader.biCompression = WinGDI.BI_RGB;

        Memory buffer = new Memory(width * height * 4);
        GDI32.INSTANCE.GetDIBits(hdcWindow, hBitmap, 0, height, buffer, bmi, WinGDI.DIB_RGB_COLORS);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, buffer.getIntArray(0, width * height), 0, width);

        GDI32.INSTANCE.DeleteObject(hBitmap);
        User32.INSTANCE.ReleaseDC(hwnd, hdcWindow);

        return image;

    }

    @Override
    public void mouseMove(int x, int y) {
        throw new UnsupportedOperationException();
    }

    private static Window createWindow(WinDef.HWND hwnd) {
        char[] chars = new char[256];
        User32.INSTANCE.GetWindowText(hwnd, chars, 256);
        String title = Native.toString(chars);

        WinDef.RECT rect = new WinDef.RECT();
        User32.INSTANCE.GetWindowRect(hwnd, rect);
        int width = rect.right - rect.left;
        int height = rect.bottom - rect.top;

        IntByReference intByRef = new IntByReference();
        User32.INSTANCE.GetWindowThreadProcessId(hwnd, intByRef);
        int processId = intByRef.getValue();

        return new Window(hwnd.getPointer().getLong(0), rect.left, rect.top, width, height, title, processId);
    }

    /**
     * @param site
     * @return Regex that finds a window by the title
     */
    private String getTitleRegex(Site site) {
        switch(site) {
            case TRIPLE_EIGHT:
                return "888poker";
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void startServer() throws LipeRMIException, IOException {
        CallHandler ch = new CallHandler();
        ch.registerGlobal(RemoteApi.class, new RemoteApiImpl());

        Server server = new Server();
        server.bind(4457, ch);

        System.out.println("server started");
    }


    public static void main(String[] args) throws IOException, LipeRMIException {
        RemoteApiImpl remoteApi = new RemoteApiImpl();
        remoteApi.startServer();

//        Window lobby = remoteApi.getUniqueWindow("Lobby");
//        System.out.println(lobby);
//
//
//        List<Window> childWindows = remoteApi.getChildWindows(lobby.getHwnd(), null);
//        System.out.println("child windows:");
//        for (Window w : childWindows) {
//            System.out.println(w);
//        }
//
//        IntByReference intByRef = new IntByReference();
//        int i = instance.GetWindowThreadProcessId(lobby.getHwnd(), intByRef);
//
//        System.out.println(intByRef);
//        intByRef.getValue();
//
//        System.out.println("windows by process id");
//        List<Window> windows = remoteApi.getWindows(lobby.getProcessId(), ".*NLH.*");
//        for (Window w : windows) {
//            System.out.println(w);
//        }

//        Window table = remoteApi.getUniqueWindow("Linz.*");
//        IntByReference tableIntByRef = new IntByReference();
//        instance.GetWindowThreadProcessId(table.getHwnd(), tableIntByRef);
//        System.out.println(tableIntByRef.getValue());

//
//        Native.
    }

}
