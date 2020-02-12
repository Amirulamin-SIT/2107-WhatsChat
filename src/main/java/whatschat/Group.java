package whatschat;

import java.util.ArrayList;

/**
 * Chat
 */
public class Group {
    public String name;
    String ip;
    public ArrayList<String> messages = new ArrayList<String>();
    public ArrayList<String> members = new ArrayList<String>();
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