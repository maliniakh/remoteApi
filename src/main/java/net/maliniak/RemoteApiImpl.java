package net.maliniak;

import com.google.common.collect.Iterables;
import com.sun.jna.Pointer;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import net.sf.lipermi.exception.LipeRMIException;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Server;
import net.maliniak.model.Site;
import net.maliniak.model.Window;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoteApiImpl implements RemoteApi {
    final static User32 instance = User32.INSTANCE;

    @Override
    public Window getUniqueWindow(Site site) {
        final String regex = getTitleRegex(site);
        return getUniqueWindow(regex);
    }

    @Override
    public Window getUniqueWindow(String titleRegex) {
        System.out.println("chuj");

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

        new WinDef.HWND(new Pointer(222L));
        WinDef.HWND hwnd = Iterables.getOnlyElement(candidateWindows);
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
//        try {
//            // create on port 1099
//            Registry registry = LocateRegistry.createRegistry(1099);
//
//            // create a new service named myMessage
//            registry.rebind("remoteApi", new RemoteApiImpl());
//            System.out.println("system is ready");
//
//            System.in.read();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

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
