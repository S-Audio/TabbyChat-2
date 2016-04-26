package mnm.mods.tabbychat;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;

import mnm.mods.tabbychat.api.Channel;
import mnm.mods.tabbychat.api.ChannelStatus;
import mnm.mods.tabbychat.api.Message;
import mnm.mods.tabbychat.api.events.MessageAddedToChannelEvent;
import mnm.mods.tabbychat.gui.ChatArea;
import mnm.mods.tabbychat.gui.settings.GuiSettingsChannel;
import mnm.mods.tabbychat.util.ChannelPatterns;
import mnm.mods.tabbychat.util.ChatTextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;

public class ChatChannel implements Channel {

    public static final Channel DEFAULT_CHANNEL = new ChatChannel("*") {
        // Don't mess with this channel
        @Override
        public void setAlias(String alias) {}

        @Override
        public void setPrefix(String prefix) {}

        @Override
        public void setPrefixHidden(boolean hidden) {}

        @Override
        public void openSettings() {
            // There are no settings for this channel
            TabbyChat.getInstance().openSettings(null);
        }
    };

    private transient List<Message> messages;

    private final String name;
    private final boolean isPm;
    private String alias;

    private String prefix = "";
    private boolean prefixHidden = false;

    private transient ChannelStatus status;

    public ChatChannel(String name) {
        this(name, false);
    }

    public ChatChannel(String name, boolean pm) {
        this.name = name;
        this.isPm = pm;
        this.alias = this.name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isPm() {
        return isPm;
    }

    @Override
    public String getAlias() {
        return this.alias;
    }

    @Override
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean isPrefixHidden() {
        return this.prefixHidden;
    }

    @Override
    public void setPrefixHidden(boolean hidden) {
        this.prefixHidden = hidden;
    }

    @Override
    public ChannelStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(ChannelStatus status) {
        // priorities
        if (status == null || this.status == null
                || status.ordinal() < this.status.ordinal()) {
            this.status = status;
        }
    }

    @Override
    public void openSettings() {
        TabbyChat.getInstance().openSettings(new GuiSettingsChannel(this));
    }

    @Override
    public List<Message> getMessages() {
        if (messages == null) {
            // dumb gson
            messages = Collections.synchronizedList(Lists.<Message> newArrayList());
        }
        return messages;
    }

    @Override
    public void addMessage(IChatComponent chat) {
        addMessage(chat, 0);
    }

    @Override
    public void addMessage(IChatComponent chat, int id) {
        List<Channel> channels = TabbyChat.getInstance().getChat().getChannels();
        if (!channels.contains(this)) {
            TabbyChat.getInstance().getChat().addChannel(this);
        }
        if (id != 0) {
            removeMessages(id);
        }
        MessageAddedToChannelEvent event = new MessageAddedToChannelEvent(chat.createCopy(), id, this);
        TabbyChat.getInstance().getBus().post(event);
        if (event.chat == null) {
            return;
        }
        if (TabbyChat.getInstance().settings.advanced.hideTag.get() && this != DEFAULT_CHANNEL) {
            ChannelPatterns pattern = TabbyChat.getInstance().serverSettings.general.channelPattern.get();
            Matcher matcher = pattern.getPattern().matcher(event.chat.getUnformattedText());
            if (matcher.find()) {
                event.chat = ChatTextUtils.subChat(event.chat, matcher.end());
            }
        }

        int uc = Minecraft.getMinecraft().ingameGUI.getUpdateCounter();
        Message msg = new ChatMessage(uc, event.chat, id, true);
        this.getMessages().add(0, msg);

        // compensate scrolling
        ChatArea chatbox = ((ChatManager) TabbyChat.getInstance().getChat()).getChatBox().getChatArea();
        if (getStatus() == ChannelStatus.ACTIVE && chatbox.getScrollPos() > 0 && id == 0) {
            chatbox.scroll(1);
        }

        trim(TabbyChat.getInstance().settings.advanced.historyLen.get());

        ((ChatManager) TabbyChat.getInstance().getChat()).save();
    }

    public void trim(int size) {
        Iterator<Message> iter = this.getMessages().iterator();

        for (int i = 0; iter.hasNext(); i++) {
            iter.next();
            if (i > size) {
                iter.remove();
            }
        }
    }

    @Override
    public void removeMessageAt(int pos) {
        this.getMessages().remove(pos);
        ((ChatManager) TabbyChat.getInstance().getChat()).save();
    }

    @Override
    public void removeMessages(int id) {
        Iterator<Message> iter = this.getMessages().iterator();
        while (iter.hasNext()) {
            Message msg = iter.next();
            if (msg.getID() == id) {
                iter.remove();
            }
        }
        ((ChatManager) TabbyChat.getInstance().getChat()).save();
    }

    @Override
    public void clear() {
        this.getMessages().clear();
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ChatChannel))
            return false;
        ChatChannel other = (ChatChannel) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return (isPm ? "@" : "#") + name;
    }

}
