package net.maliniak;

import net.maliniak.model.Window;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        CallHandler callHandler = new CallHandler();
        String remoteHost = "localhost";
        int portWasBinded = 4457;

        Client client = new Client(remoteHost, portWasBinded, callHandler);
        RemoteApi remoteApi = (RemoteApi) client.getGlobal(RemoteApi.class);
        Window calculator = remoteApi.getUniqueWindow("Carmen.*");
        System.out.println(calculator);

        for(int i = 0; i < 300; i++) {
            long start = System.currentTimeMillis();
            byte[] imgBytes = remoteApi.captureImage(calculator.getHwndPeer());
            System.out.println("bytes: " + imgBytes + ", size: " + imgBytes.length);
//        BufferedImage capture = (BufferedImage) imgBytes;


            InputStream in = new ByteArrayInputStream(imgBytes);
            BufferedImage img = ImageIO.read(in);
            System.out.println(System.currentTimeMillis() - start);

            ImageIO.write(img, "png", new File("/tmp/888_" + i + ".png"));

            Thread.sleep(2000);
//            JFrame frame = new JFrame();
//            frame.getContentPane().setLayout(new FlowLayout());
//            frame.getContentPane().add(new JLabel(new ImageIcon(img)));
//            frame.pack();
//            frame.setVisible(true);
        }

//        System.out.println(calculator)
    }
}
