package net.maliniak.model;

import java.io.Serializable;

/**
 * Created by llmali on 27/11/2015.
 */
@SuppressWarnings("unused")
public class Window implements Serializable {
    private int hwndPeer;
    private int x;
    private int y;
    private int width;
    private int height;
    private String title;
    private Integer processId;

    public Window(int hwndPeer, int x, int y, int width, int height, String title, Integer processId) {
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

    public int getHwndPeer() {
        return hwndPeer;
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

    public Integer getProcessId() {
        return processId;
    }

    @Override
    public String toString() {
        return "Window{" +
                "hwnd=" + hwndPeer +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", title='" + title + '\'' +
                ", processId=" + processId +
                '}';
    }
}
