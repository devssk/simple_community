package com.jaritalk.backend.repository;

import com.jaritalk.backend.entity.Post;
import com.jaritalk.backend.entity.PostLike;
import com.jaritalk.backend.entity.User;
import com.jaritalk.backend.repository.querydsl.QPostLikeRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long>, QPostLikeRepository {
    boolean existsByPostAndUser(Post post, User user);
    Optional<PostLike> findByPostAndUser(Post post, User user);
}
