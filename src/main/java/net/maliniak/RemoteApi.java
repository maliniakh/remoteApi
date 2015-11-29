package net.maliniak;


import com.google.common.collect.Iterables;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.ptr.IntByReference;
import net.maliniak.model.Site;
import net.maliniak.model.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoteApi {
    final static User32 instance = User32.INSTANCE;

    public Window getUniqueWindow(Site site) {
        final String regex = getTitleRegex(site);
        return getUniqueWindow(regex);
    }

    public Window getUniqueWindow(String titleRegex) {
        final Pattern pattern = Pattern.compile(titleRegex);

        final List<WinDef.HWND> candidateWindows = new ArrayList<WinDef.HWND>();

        instance.EnumWindows(new WinUser.WNDENUMPROC() {
            public boolean callback(WinDef.HWND hwnd, Pointer pointer) {
                String title = WindowUtils.getWindowTitle(hwnd);

                Matcher matcher = pattern.matcher(title);
                if (matcher.matches()) {
                    candidateWindows.add(hwnd);
                    System.out.println(title);
                }

                return true;
            }
        }, null);

        WinDef.HWND hwnd = Iterables.getOnlyElement(candidateWindows);
        return new Window(hwnd);
    }

    public List<Window> getChildWindows(WinDef.HWND hwnd, String titleRegex) {
        List<Window> result = new ArrayList<Window>();
        final Pattern pattern = titleRegex != null ? Pattern.compile(titleRegex):null;
        instance.EnumChildWindows(hwnd,  (childHwnd, pointer) -> {
            if(pattern != null) {
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


    public static void main(String[] args) {
        RemoteApi remoteApi = new RemoteApi();
        Window lobby = remoteApi.getUniqueWindow("Lobby");
        System.out.println(lobby);


        List<Window> childWindows = remoteApi.getChildWindows(lobby.getHwnd(), null);
        System.out.println("child windows:");
        for (Window w : childWindows) {
            System.out.println(w);
        }

        IntByReference intByRef = new IntByReference();
        int i = instance.GetWindowThreadProcessId(lobby.getHwnd(), intByRef);

        System.out.println(intByRef);
        intByRef.getValue();

        Window table = remoteApi.getUniqueWindow("Linz.*");
        IntByReference tableIntByRef = new IntByReference();
        instance.GetWindowThreadProcessId(table.getHwnd(), tableIntByRef);
        System.out.println(tableIntByRef.getValue());

//
        Native.
    }

}
