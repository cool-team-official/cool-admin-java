package com.cool.modules.base.service.sys;

import com.cool.modules.base.dto.sys.CodeContentDto;

import java.util.List;

public interface BaseCodingService {
    List<String> getModuleTree();

    void createCode(List<CodeContentDto> codes);
}
