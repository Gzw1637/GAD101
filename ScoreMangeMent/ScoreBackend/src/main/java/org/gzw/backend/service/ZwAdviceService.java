package org.gzw.backend.service;

import org.gzw.backend.entity.ZwAdvice;
import org.gzw.backend.entity.vo.ZwAdviceVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ZwAdviceService {

    Long addAdvice(ZwAdvice zwAdvice);

    int updateAdvice(ZwAdvice zwAdvice);

    int deleteAdvice(Long adviceId);

    ZwAdviceVO getAdviceById(Long adviceId);

    List<ZwAdviceVO> getAllAdvices();

    List<ZwAdviceVO> getAdvicesByRecipientId(Long recipientId);

    List<ZwAdviceVO> getAdvicesBySenderId(Long senderId);

    List<ZwAdviceVO> searchAdvicesByConditions(Long recipientId, Long senderId, Integer adviceType, Integer adviceStatus);

    int uploadAdviceImages(Long adviceId, MultipartFile[] files);

    String downloadAdviceImage(Long imageId);

    int deleteAdviceImage(Long imageId);

    List<ZwAdviceVO> getAdviceImagesByAdviceId(Long adviceId);

    List<ZwAdviceVO> getMySentAdvices(Long currentUserId);

    List<ZwAdviceVO> getMyReceivedAdvices(Long currentUserId);

    int replyAdvice(ZwAdvice zwAdvice);
}
