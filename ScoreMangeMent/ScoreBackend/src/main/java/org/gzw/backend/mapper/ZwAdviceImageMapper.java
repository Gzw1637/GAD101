package org.gzw.backend.mapper;

import org.apache.ibatis.annotations.Param;
import org.gzw.backend.entity.ZwAdviceImage;
import java.util.List;

public interface ZwAdviceImageMapper {

    int insert(ZwAdviceImage zwAdviceImage);

    int update(ZwAdviceImage zwAdviceImage);

    int deleteById(Long imageId);

    int deleteByAdviceId(Long adviceId);

    ZwAdviceImage selectById(Long imageId);

    List<ZwAdviceImage> selectByAdviceId(Long adviceId);
}
