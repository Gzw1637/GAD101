package org.gzw.backend.mapper;

import org.apache.ibatis.annotations.Param;
import org.gzw.backend.entity.ZwAdvice;
import org.gzw.backend.entity.vo.ZwAdviceVO;
import java.util.List;

public interface ZwAdviceMapper {

    int insert(ZwAdvice zwAdvice);

    int update(ZwAdvice zwAdvice);

    int deleteById(Long adviceId);

    ZwAdviceVO selectById(Long adviceId);

    List<ZwAdviceVO> selectAll();

    List<ZwAdviceVO> selectByRecipientId(Long recipientId);

    List<ZwAdviceVO> selectBySenderId(Long senderId);

    List<ZwAdviceVO> selectByAdviceId(Long adviceId);

    List<ZwAdviceVO> searchByConditions(@Param("recipientId") Long recipientId,
                                       @Param("senderId") Long senderId,
                                       @Param("adviceType") Integer adviceType,
                                       @Param("adviceStatus") Integer adviceStatus);
}
