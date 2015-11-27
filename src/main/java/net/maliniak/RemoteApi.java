package net.maliniak;


import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
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
        final Pattern pattern = Pattern.compile(regex);

        final List<WinDef.HWND> candidateWindows = new ArrayList<WinDef.HWND>();

        instance.EnumWindows(new WinUser.WNDENUMPROC() {
            public boolean callback(WinDef.HWND hwnd, Pointer pointer) {
                String title = WindowUtils.getWindowTitle(hwnd);

                Matcher matcher = pattern.matcher(title);
                if(matcher.matches()) {
                    candidateWindows.add(hwnd);
                    System.out.println(title);
                }

                return true;
            }
        }, null);

        WinDef.HWND hwnd = Iterables.getOnlyElement(candidateWindows);
        return new Window(hwnd);
    }

    public List<Window> getChildWindows(String id, String regex) {
        WinDef.HWND hwnd = null;
        // todo: id -> hwnd

        List<Window> result = new ArrayList<Window>();
        final Pattern pattern = Pattern.compile(regex);
        instance.EnumChildWindows(hwnd,  (childHwnd, pointer) -> {
            String title = WindowUtils.getWindowTitle(childHwnd);
            if(pattern.matcher(title).matches()) {
                result.add(new Window(childHwnd));
            }
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
        instance.EnumWindows(new WinUser.WNDENUMPROC() {
            public boolean callback(WinDef.HWND hwnd, Pointer pointer) {
                System.out.println(hwnd);

                return true;
            }
        }, null);

//        instance.EnumChildWindows()

        System.out.println("CHUJ");
//        User32.INSTANCE.
//        System.out.println(hwnd);
    }
}
