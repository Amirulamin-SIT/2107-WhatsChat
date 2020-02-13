package whatschat;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Chat
 */
public class Group {
    public String name;
    String ip;
    public ArrayList<String> messages = new ArrayList<String>();
    public HashSet<String> members = new HashSet<String>();
    public boolean memberOf = false;

    public Group(String groupName, String groupIp) {
        this.name = groupName;
        this.ip = groupIp;
    }

    public void setMessages(ArrayList<String> messages) {
        this.messages.addAll(messages);
    }

    public void setUsers(ArrayList<String> members) {
        this.members.addAll(members);
    }
}