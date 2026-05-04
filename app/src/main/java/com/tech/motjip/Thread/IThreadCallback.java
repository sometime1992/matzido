package com.tech.motjip.Thread;

// 멀티 쓰레드에서 작업이 완료되었음을 알립니다.
public interface IThreadCallback {
    // 쓰레드 작업이 완료되었음.
    public abstract void ThreadEnds();
}
