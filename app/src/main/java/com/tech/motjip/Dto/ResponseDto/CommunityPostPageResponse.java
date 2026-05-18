package com.tech.motjip.Dto.ResponseDto;

import com.tech.motjip.Model.CommunityPost;

import java.util.List;

public class CommunityPostPageResponse {

    private List<CommunityPost> content;

    private int page;

    private int size;

    private boolean last;

    public List<CommunityPost> getContent() {
        return content;
    }

    public void setContent(List<CommunityPost> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}