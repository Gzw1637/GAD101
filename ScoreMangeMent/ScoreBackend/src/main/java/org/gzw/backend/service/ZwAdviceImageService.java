package org.gzw.backend.service;

import org.gzw.backend.entity.ZwAdviceImage;
import java.util.List;

public interface ZwAdviceImageService {

    int addAdviceImage(ZwAdviceImage zwAdviceImage);

    int updateAdviceImage(ZwAdviceImage zwAdviceImage);

    int deleteAdviceImage(Long imageId);

    int deleteAdviceImagesByAdviceId(Long adviceId);

    ZwAdviceImage getAdviceImageById(Long imageId);

    List<ZwAdviceImage> getAdviceImagesByAdviceId(Long adviceId);
}
