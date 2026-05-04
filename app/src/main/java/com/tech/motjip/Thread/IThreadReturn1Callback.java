package com.tech.motjip.Thread;

// 멀티 쓰레드에서 작업이 완료되었음을 알립니다.
public interface IThreadReturn1Callback<T>{
    // 쓰레드 작업이 완료되었을때 T result 한개를 반환합니다.
    public abstract void ThreadEnds(T result);
    // 에러가 발생했을시 예외를 반환합니다.
    public abstract void onError(Exception e);
}
