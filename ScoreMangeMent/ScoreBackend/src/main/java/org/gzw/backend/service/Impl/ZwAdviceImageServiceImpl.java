package org.gzw.backend.service.Impl;

import org.gzw.backend.entity.ZwAdviceImage;
import org.gzw.backend.mapper.ZwAdviceImageMapper;
import org.gzw.backend.service.ZwAdviceImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ZwAdviceImageServiceImpl implements ZwAdviceImageService {

    @Autowired
    private ZwAdviceImageMapper zwAdviceImageMapper;

    @Override
    @Transactional
    public int addAdviceImage(ZwAdviceImage zwAdviceImage) {
        return zwAdviceImageMapper.insert(zwAdviceImage);
    }

    @Override
    @Transactional
    public int updateAdviceImage(ZwAdviceImage zwAdviceImage) {
        return zwAdviceImageMapper.update(zwAdviceImage);
    }

    @Override
    @Transactional
    public int deleteAdviceImage(Long imageId) {
        return zwAdviceImageMapper.deleteById(imageId);
    }

    @Override
    @Transactional
    public int deleteAdviceImagesByAdviceId(Long adviceId) {
        return zwAdviceImageMapper.deleteByAdviceId(adviceId);
    }

    @Override
    public ZwAdviceImage getAdviceImageById(Long imageId) {
        return zwAdviceImageMapper.selectById(imageId);
    }

    @Override
    public List<ZwAdviceImage> getAdviceImagesByAdviceId(Long adviceId) {
        return zwAdviceImageMapper.selectByAdviceId(adviceId);
    }
}
