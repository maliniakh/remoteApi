package net.maliniak;

import net.maliniak.model.Window;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientTest {

    private void doTest() {
        try {
            // fire to localhost port 1099
            Registry myRegistry = LocateRegistry.getRegistry("127.0.0.1", 1099);

            // search for myMessage service
            RemoteApi impl = (RemoteApi) myRegistry.lookup("remoteApi");

            // call server's method
            Window uniqueWindow = impl.getUniqueWindow("Untitled.*Notepad.*");
            System.out.println(uniqueWindow);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        CallHandler callHandler = new CallHandler();
        String remoteHost = "localhost";
        int portWasBinded = 4457;

        Client client = new Client(remoteHost, portWasBinded, callHandler);
        RemoteApi remoteApi = (RemoteApi) client.getGlobal(RemoteApi.class);
        Window calculator = remoteApi.getUniqueWindow("Calculator");
        System.out.println(calculator);

        for(int i = 0; i < 100; i++) {
            long start = System.currentTimeMillis();
            byte[] imgBytes = remoteApi.captureImage(calculator.getHwndPeer());
            System.out.println("bytes: " + imgBytes);
//        BufferedImage capture = (BufferedImage) imgBytes;

            InputStream in = new ByteArrayInputStream(imgBytes);
            BufferedImage img = ImageIO.read(in);
            System.out.println(System.currentTimeMillis() - start);

//            JFrame frame = new JFrame();
//            frame.getContentPane().setLayout(new FlowLayout());
//            frame.getContentPane().add(new JLabel(new ImageIcon(img)));
//            frame.pack();
//            frame.setVisible(true);
        }

//        System.out.println(calculator)
    }
}
