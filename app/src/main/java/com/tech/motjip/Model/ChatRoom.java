package com.tech.motjip.Model;

public class ChatRoom {

    private Long roomId;         // 서버 DB의 방 번호
    private String roomName;     // 방 제목
    private String lastMessage;  // 마지막 대화 (UI용)
    private String time;         // 시간 (UI용)

    // 🚀 1. 기본 생성자 (파라미터 0개짜리) - "found 0" 에러 해결!
    public ChatRoom() {
    }

    // 🚀 2. 전체 생성자 (파라미터 4개짜리)
    public ChatRoom(Long roomId, String roomName, String lastMessage, String time) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.lastMessage = lastMessage;
        this.time = time;
    }

    // 🚀 3. Getter & Setter 메서드들 - "Cannot resolve method" 에러들 해결!

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}