package org.gzw.backend.service.Impl;

import cn.hutool.core.util.IdUtil;
import org.gzw.backend.entity.ZwAdvice;
import org.gzw.backend.entity.ZwAdviceImage;
import org.gzw.backend.entity.vo.ZwAdviceVO;
import org.gzw.backend.mapper.ZwAdviceImageMapper;
import org.gzw.backend.mapper.ZwAdviceMapper;
import org.gzw.backend.service.ZwAdviceImageService;
import org.gzw.backend.service.ZwAdviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ZwAdviceServiceImpl implements ZwAdviceService {

    @Autowired
    private ZwAdviceMapper zwAdviceMapper;

    @Autowired
    private ZwAdviceImageMapper zwAdviceImageMapper;

    @Value("${advice.upload.path:/tmp/advice-images}")
    private String uploadPath;

    @Value("${advice.upload.base-url:/api/advice/images}")
    private String baseUrl;

    @Override
    @Transactional
    public Long addAdvice(ZwAdvice zwAdvice) {
        zwAdvice.setCreateTime(new Date());
        zwAdviceMapper.insert(zwAdvice);
        return zwAdvice.getAdviceId();
    }

    @Override
    @Transactional
    public int updateAdvice(ZwAdvice zwAdvice) {
        return zwAdviceMapper.update(zwAdvice);
    }

    @Override
    @Transactional
    public int deleteAdvice(Long adviceId) {
        zwAdviceImageMapper.deleteByAdviceId(adviceId);
        return zwAdviceMapper.deleteById(adviceId);
    }

    @Override
    public ZwAdviceVO getAdviceById(Long adviceId) {
        return zwAdviceMapper.selectById(adviceId);
    }

    @Override
    public List<ZwAdviceVO> getAllAdvices() {
        return zwAdviceMapper.selectAll();
    }

    @Override
    public List<ZwAdviceVO> getAdvicesByRecipientId(Long recipientId) {
        return zwAdviceMapper.selectByRecipientId(recipientId);
    }

    @Override
    public List<ZwAdviceVO> getAdvicesBySenderId(Long senderId) {
        return zwAdviceMapper.selectBySenderId(senderId);
    }

    @Override
    public List<ZwAdviceVO> searchAdvicesByConditions(Long recipientId, Long senderId, Integer adviceType, Integer adviceStatus) {
        return zwAdviceMapper.searchByConditions(recipientId, senderId, adviceType, adviceStatus);
    }

    @Override
    @Transactional
    public int uploadAdviceImages(Long adviceId, MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return 0;
        }

        int successCount = 0;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            try {
                String originalFilename = file.getOriginalFilename();
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String newFileName = UUID.randomUUID().toString() + extension;
                String savePath = uploadPath + File.separator + newFileName;

                file.transferTo(new File(savePath));

                ZwAdviceImage adviceImage = new ZwAdviceImage();
                adviceImage.setAdviceId(adviceId);
                adviceImage.setImageUrl(baseUrl + "/" + newFileName);
                adviceImage.setCreateTime(new Date());

                zwAdviceImageMapper.insert(adviceImage);
                successCount++;
            } catch (IOException e) {
                throw new RuntimeException("上传图片失败: " + e.getMessage());
            }
        }

        return successCount;
    }

    @Override
    public String downloadAdviceImage(Long imageId) {
        ZwAdviceImage adviceImage = zwAdviceImageMapper.selectById(imageId);
        if (adviceImage == null) {
            return null;
        }
        return adviceImage.getImageUrl();
    }

    @Override
    @Transactional
    public int deleteAdviceImage(Long imageId) {
        ZwAdviceImage adviceImage = zwAdviceImageMapper.selectById(imageId);
        if (adviceImage != null) {
            String imageUrl = adviceImage.getImageUrl();
            if (imageUrl != null) {
                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                File file = new File(uploadPath + File.separator + fileName);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        return zwAdviceImageMapper.deleteById(imageId);
    }

    @Override
    public List<ZwAdviceVO> getAdviceImagesByAdviceId(Long adviceId) {
        return zwAdviceMapper.selectByAdviceId(adviceId);
    }

    @Override
    public List<ZwAdviceVO> getMySentAdvices(Long currentUserId) {
        return zwAdviceMapper.selectBySenderId(currentUserId);
    }

    @Override
    public List<ZwAdviceVO> getMyReceivedAdvices(Long currentUserId) {
        List<ZwAdviceVO> advices = zwAdviceMapper.selectByRecipientId(currentUserId);
        for (ZwAdviceVO advice : advices) {
            if (advice.getAdviceStatus() != null && advice.getAdviceStatus() == 10131001) {
                ZwAdvice updateAdvice = new ZwAdvice();
                updateAdvice.setAdviceId(advice.getAdviceId());
                updateAdvice.setAdviceStatus(10131002);
                zwAdviceMapper.update(updateAdvice);
                advice.setAdviceStatus(10131002);
                advice.setAdviceStatusName("已读");
            }
        }
        return advices;
    }

    @Override
    @Transactional
    public int replyAdvice(ZwAdvice zwAdvice) {
        zwAdvice.setCreateTime(new Date());
        if (zwAdvice.getAdviceStatus() == null) {
            zwAdvice.setAdviceStatus(10131001);
        }
        return zwAdviceMapper.insert(zwAdvice);
    }
}
