package com.jaritalk.backend.controller;

import com.jaritalk.backend.dto.HistoryDto;
import com.jaritalk.backend.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/history")
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping("/postLike")
    public Page<HistoryDto.PostLikeHistoryRes> getPostLikeHistoryList(Pageable pageable, @RequestParam(name = "userId", required = false) Long userId) {
        return historyService.getPostLikeHistoryList(pageable, userId);
    }

    @GetMapping("/post")
    public Page<HistoryDto.PostHistoryRes> getPostHistoryList(Pageable pageable) {
        return historyService.getPostHistoryList(pageable);
    }

}
