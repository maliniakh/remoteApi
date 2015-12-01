package net.maliniak;

import com.google.common.collect.Iterables;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import net.maliniak.model.Site;
import net.maliniak.model.Window;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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
        return new Window(hwnd);
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
                        result.add(new Window(hwnd));
                    }
                } else {
                    result.add(new Window(hwnd));
                }
            }

            return true;
        }, null);

        return result;
    }

    @Override
    public List<Window> getChildWindows(WinDef.HWND hwnd, String titleRegex) {
        List<Window> result = new ArrayList<Window>();
        final Pattern pattern = titleRegex != null ? Pattern.compile(titleRegex) : null;
        instance.EnumChildWindows(hwnd, (childHwnd, pointer) -> {
            if (pattern != null) {
                String title = WindowUtils.getWindowTitle(childHwnd);
                if (pattern.matcher(title).matches()) {
                    result.add(new Window(childHwnd));
                }
            } else {
                result.add(new Window(childHwnd));
            }

            return true;
        }, null);

        return result;
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

    private void startServer() {
        try {
            // create on port 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            // create a new service named myMessage
            registry.rebind("remoteApi", new RemoteApiImpl());
            System.out.println("system is ready");

            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws RemoteException {
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
