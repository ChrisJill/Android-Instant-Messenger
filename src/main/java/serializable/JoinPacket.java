package serializable;

import java.io.Serializable;

/**
 * Created by pranavdadlani on 11/27/15.
 */
public class JoinPacket implements Serializable {
    public String name;
    public int listenPort;
    public JoinPacket(String name,int listenPort)
    {
        this.name=name;
        this.listenPort=listenPort;
    }
}
