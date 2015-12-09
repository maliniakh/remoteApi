package net.maliniak;

import net.maliniak.model.Site;
import net.maliniak.model.Window;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by llmali on 01/12/2015.
 */
public interface RemoteApi {
    Window getUniqueWindow(Site site) throws RemoteException;

    Window getUniqueWindow(String titleRegex) throws RemoteException;

    List<Window> getWindows(Integer processId, String titleRegex) throws RemoteException;

//    List<Window> getChildWindows(WinDef.HWND hwnd, String titleRegex) throws RemoteException;
}
