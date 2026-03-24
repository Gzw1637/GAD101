package org.gzw.backend.mapper;

import org.gzw.backend.entity.Code;
import java.util.List;

public interface CodeMapper {
    
    List<Code> selectByType(String type);
    
    Code selectByCodeId(Integer codeId);
}
