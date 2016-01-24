package net.maliniak;

import net.maliniak.model.Site;
import net.maliniak.model.Window;

import java.util.List;

/**
 * Created by llmali on 01/12/2015.
 */
public interface RemoteApi {
    Window getUniqueWindow(Site site);

    Window getUniqueWindow(String titleRegex);

    List<Window> getWindows(Integer processId, String titleRegex);

    byte[] captureImage(Long hwndPeer);

    void mouseMove(int x, int y);

    void mouseClick();

//    List<Window> getChildWindows(WinDef.HWND hwnd, String titleRegex) throws RemoteException;
}
