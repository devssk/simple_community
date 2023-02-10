package com.jaritalk.backend.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Post extends TimeStamp{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ColumnDefault("false")
    private Boolean deleteYn;

    private LocalDateTime deleteDate;

    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST)
    private List<PostLike> postLikeList = new ArrayList<>();

    @Builder
    public Post(Long postId, User user, String title, String content, Boolean deleteYn, LocalDateTime deleteDate) {
        this.postId = postId;
        this.user = user;
        this.title = title;
        this.content = content;
        this.deleteYn = deleteYn;
        this.deleteDate = deleteDate;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updatePostLikeList(PostLike postLike) {
        this.getPostLikeList().add(postLike);
    }

    public void deletePost() {
        this.deleteYn = true;
        this.deleteDate = LocalDateTime.now();
    }


}
