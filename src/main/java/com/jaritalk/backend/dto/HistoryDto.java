package com.jaritalk.backend.dto;


import com.jaritalk.backend.entity.Post;
import com.jaritalk.backend.entity.PostLike;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class HistoryDto {

    @Getter
    @Setter
    public static class PostLikeHistoryRes {
        private Long userId;
        private String accountType;
        private Long postId;
        private String title;

        public PostLikeHistoryRes(PostLike postLike) {
            this.userId = postLike.getUser().getUserId();
            this.accountType = postLike.getUser().getAccountType().getValue();
            this.postId = postLike.getPost().getPostId();
            this.title = postLike.getPost().getTitle();
        }
    }

    @Getter
    @Setter
    public static class PostHistoryRes {
        private Long userId;
        private String accountType;
        private Long postId;
        private String title;
        private LocalDateTime createdDate;
        private LocalDateTime modifiedDate;
        private LocalDateTime deleteDate;

        public PostHistoryRes(Post post) {
            this.userId = post.getUser().getUserId();
            this.accountType = post.getUser().getAccountType().getValue();
            this.postId = post.getPostId();
            this.title = post.getTitle();
            this.createdDate = post.getCreatedDate();
            this.modifiedDate = post.getModifiedDate();
            this.deleteDate = post.getDeleteDate();
        }
    }

}
