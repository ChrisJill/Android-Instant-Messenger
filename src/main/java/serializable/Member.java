package serializable;

import java.io.Serializable;
import java.net.Socket;

/**
 * Class representing a user in the system
 * @author satyajeet
 */
public class Member implements Serializable {

    public String name, ip;
    public Socket socket;
    public int port;

    public Member(String name, String ip, int port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
    }
}
