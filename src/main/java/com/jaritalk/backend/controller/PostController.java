package com.jaritalk.backend.controller;

import com.jaritalk.backend.dto.PostDto;
import com.jaritalk.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    @PostMapping("")
    public Long insertPost(HttpServletRequest request, @RequestBody PostDto.InsertPostReq requestDto) {
        return postService.InsertPost(request, requestDto);
    }

    @GetMapping("/{postId}")
    public void getPostDetail(@PathVariable(name = "postId") Long postId) {
        postService.getPostDetail(postId);
    }

    @GetMapping("")
    public void getPostList(HttpServletRequest request, Pageable pageable) {
        postService.getPostList(request, pageable);
    }

    @PatchMapping("/{postId}")
    public void updatePost(HttpServletRequest request, @PathVariable(name = "postId") Long postId,
                           @RequestBody PostDto.UpdatePostReq requestDto) {
        postService.updatePost(request, postId, requestDto);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(HttpServletRequest request, @PathVariable(name = "postId") Long postId) {
        postService.deletePost(request, postId);
    }

    @PostMapping("/like/{postId}")
    public void postLike(HttpServletRequest request, @PathVariable(name = "postId") Long postId,
                         @RequestParam(name = "likeYn", required = false) Boolean likeYn) {
        postService.postLike(request, postId, likeYn);
    }

}
