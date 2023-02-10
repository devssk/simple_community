package com.jaritalk.backend.dto;

import com.jaritalk.backend.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

public class PostDto {

    @Getter
    @Setter
    public static class InsertPostReq {
        private String title;
        private String content;
    }

    @Getter
    @Setter
    public static class UpdatePostReq {
        private String title;
        private String content;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PostDetailRes {
        private Long postId;
        private Long userId;
        private String nickname;
        private String accountType;
        private String title;
        private String content;
        private LocalDateTime createdDate;
        private LocalDateTime modifiedDate;
        private LocalDateTime deleteDate;

        public PostDetailRes(Post post) {
            this.postId = post.getPostId();
            this.userId = post.getUser().getUserId();
            this.nickname = post.getUser().getNickname();
            this.accountType = post.getUser().getAccountType().getValue();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.createdDate = post.getCreatedDate();
            this.modifiedDate = post.getModifiedDate();
            this.deleteDate = post.getDeleteDate();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PostListRes {
        private Long postId;
        private Long userId;
        private Boolean likeYn;
        private String nickname;
        private String accountType;
        private String title;
        private Integer likeCount;
        private LocalDateTime createdDate;
        private LocalDateTime modifiedDate;

        public PostListRes(Post post) {
            this.postId = post.getPostId();
            this.userId = post.getUser().getUserId();
            this.likeYn = false;
            this.nickname = post.getUser().getNickname();
            this.accountType = post.getUser().getAccountType().getValue();
            this.title = post.getTitle();
            this.likeCount = post.getPostLikeList().size();
            this.createdDate = post.getCreatedDate();
            this.modifiedDate = post.getModifiedDate();
        }
    }

}
