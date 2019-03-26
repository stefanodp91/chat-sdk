package it.fourn.chatsdk.core.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;
import com.google.firebase.database.ServerValue;

import java.util.Map;

public class ConversationDTO implements Conversation {

    @PropertyName("conversationId")
    @Exclude
    private String conversationId;

    @PropertyName("convers_with")
    @Exclude
    private String conversWith;

    @PropertyName("convers_with_fullname")
    @Exclude
    private String conversWithFullname;

    @PropertyName("is_new")
    private Boolean isNew;

    @PropertyName("last_message_text")
    private String lastMessageText;

    @PropertyName("recipient")
    private String recipient;

    @PropertyName("recipient_fullname")
    private String recipientFullname;

    @PropertyName("sender")
    private String sender;

    @PropertyName("sender_fullname")
    private String senderFullname;

    @PropertyName("status")
    private int status;

    @PropertyName("timestamp")
    private Long timestamp;

    @PropertyName("extras")
    private Map<String, Object> extras;

    @PropertyName("channel_type")
    private String channelType;

    @Exclude
    @Override
    public String getConversationId() {
        return conversationId;
    }

    @Exclude
    @Override
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    @Exclude
    @Override
    public String getConversWith() {
        return conversWith;
    }

    @Exclude
    @Override
    public void setConversWith(String conversWith) {
        this.conversWith = conversWith;
    }

    @Exclude
    @Override
    public String getConversWithFullname() {
        return conversWithFullname;
    }

    @Exclude
    @Override
    public void setConversWithFullname(String conversWithFullname) {
        this.conversWithFullname = conversWithFullname;
    }

    @Exclude
    @Override
    public Long getTimestampLong() {
        return timestamp;
    }

    @Override
    public Boolean isNew() {
        return isNew;
    }

    @Override
    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public String getLastMessageText() {
        return lastMessageText;
    }

    @Override
    public void setLastMessageText(String lastMessageText) {
        this.lastMessageText = lastMessageText;
    }

    @Override
    public String getRecipient() {
        return recipient;
    }

    @Override
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    @Override
    public String getRecipientFullName() {
        return recipientFullname;
    }

    @Override
    public void setRecipientFullName(String recipientFullname) {
        this.recipientFullname = recipientFullname;
    }

    @Override
    public String getSender() {
        return sender;
    }

    @Override
    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String getSenderFullname() {
        return senderFullname;
    }

    @Override
    public void setSenderFullname(String senderFullname) {
        this.senderFullname = senderFullname;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public java.util.Map<String, String> getTimestamp() {
        return ServerValue.TIMESTAMP;
    }

    @Override
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public Map<String, Object> getExtras() {
        return extras;
    }

    @Override
    public void setExtras(Map<String, Object> extras) {
        this.extras = extras;
    }

    @Override
    public String getChannelType() {
        return channelType;
    }

    @Override
    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Conversation) {
            Conversation conversation = (Conversation) object;
            return this.getConversationId().equals(conversation.getConversationId());
        }

        return false;
    }
}
