package com.tech.motjip.Model;

// package com.tech.motjip.Model;

import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("messageContent")
    private String messageContent;

    @SerializedName("senderId") // 🚀 [추가]
    private String senderId;

    @SerializedName("senderNickname")
    private String senderNickname;

    private int viewType; // 앱 내부 구분용 (0: 나, 1: 남)

    // 기본 생성자 (GSON 파싱용 필수)
    public Message() {}

    public Message(String content, String senderId, String senderNickname, int viewType) {
        this.messageContent = content;
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.viewType = viewType;
    }

    public String getSenderId() { return senderId; }
    public String getContent() { return messageContent; }
    public String getSenderNickname() { return senderNickname; }
    public int getViewType() { return viewType; }
    public void setViewType(int viewType) { this.viewType = viewType; }

    @SerializedName("sentAt") // 🚀 서버가 보내주는 시간 데이터
    private String sentAt;

    // 생성자 (기존 생성자는 그대로 두고, 시간을 빼고 만들 때를 대비해 하나 더 둡니다)
    // 혹은 기존 생성자 끝에 sentAt을 추가해도 됩니다.

    public String getSentAt() { return sentAt; }
    public void setSentAt(String sentAt) { this.sentAt = sentAt; }


}