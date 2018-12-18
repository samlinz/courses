package sec.portscanner;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class PortScanner {

    final static int MIN_PORT = 1024;
    final static int MAX_PORT = 49151;

    public static void main(String[] args) throws Exception {
        Scanner reader = new Scanner(System.in);

        String address = "sec-mooc-1.cs.helsinki.fi";
        int start = 20050;
        int end = 20150;

        Set<Integer> ports = getAccessiblePorts(address, start, end);
        System.out.println("");

        if (ports.isEmpty()) {
            System.out.println("None found :(");
        } else {
            System.out.println("Found:");
            ports.stream().forEach(p -> System.out.println("\t" + p));

            for (int port : ports) {
                URLConnection conn = new URL("http://" + address + ":" + port).openConnection();
                InputStream inputStream = null;
                try {
                    inputStream = conn.getInputStream();
                    String msg = "";
                    int c = 0;
                    while ((c = inputStream.read()) != -1) {
                        msg += (char) c;
                    }
                    System.out.println(msg);
                } catch (Exception ex) {
                    System.out.println(ex.getLocalizedMessage());
                } finally {
                    if (inputStream != null)
                        inputStream.close();
                }
            }
        }
    }

    public static Set<Integer> getAccessiblePorts(String address, int start, int end) {
        Set<Integer> accessiblePorts = new TreeSet<>();
        start = Math.max(start, MIN_PORT);
        end = Math.min(end, MAX_PORT);

        for (int i = start; i < end; i++) {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(address, i), 50);
                socket.close();
                accessiblePorts.add(i);
            } catch (Exception e) {
            }
        }

        return accessiblePorts;
    }
}
