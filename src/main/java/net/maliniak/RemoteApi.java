package net.maliniak;

import com.sun.jna.platform.win32.User32;
import net.maliniak.model.Site;
import net.maliniak.model.Window;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Created by llmali on 01/12/2015.
 */
public interface RemoteApi {
    Window getUniqueWindow(Site site);

    Window getUniqueWindow(String titleRegex);

    List<Window> getWindows(Integer processId, String titleRegex);

    BufferedImage capture(Long hwndPeer);

    void mouseMove(int x, int y);

//    List<Window> getChildWindows(WinDef.HWND hwnd, String titleRegex) throws RemoteException;
}
