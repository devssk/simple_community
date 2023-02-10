package com.jaritalk.backend.repository;

import com.jaritalk.backend.entity.Post;
import com.jaritalk.backend.repository.querydsl.QPostRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, QPostRepository {
}
