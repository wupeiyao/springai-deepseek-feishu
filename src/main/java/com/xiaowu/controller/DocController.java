package com.xiaowu.controller;


import com.xiaowu.entity.vo.DocVO;
import com.xiaowu.service.DocService;
import com.xiaowu.utils.RestResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/doc")
@RequiredArgsConstructor
public class DocController {

    private final DocService docService;

    @GetMapping("/list")
    public RestResult<List<DocVO>> list() {
        return RestResult.buildSuccessResult(docService.list());
    }

    @GetMapping("/load")
    public RestResult<String> load() {
        docService.loadData();
        return RestResult.buildSuccessResult();
    }

}
