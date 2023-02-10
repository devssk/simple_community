package com.jaritalk.backend.repository.querydsl;

import com.jaritalk.backend.entity.Post;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QPostRepository {
    Optional<Post> findFetchPostById(Long postId);
    List<Post> findFetchPostList(Pageable pageable, Boolean deleteYn);
    Long countFetchPostList(Boolean deleteYn);
}
