package serializable;

import java.io.Serializable;

/**
 * Created by pranavdadlani on 11/27/15.
 */
public class ChatMessage implements Serializable {
    public String name;
    public String message;

    public ChatMessage(String name, String message)
    {
        this.name = name;
        this.message=message;
    }
}
