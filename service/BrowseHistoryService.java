package org.example.animalsystem.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.animalsystem.entity.Animal;
import org.example.animalsystem.entity.BrowseHistory;
import org.example.animalsystem.mapper.BrowseHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrowseHistoryService {

    @Autowired
    private BrowseHistoryMapper browseHistoryMapper;

    @Autowired
    private AnimalService animalService;

    // 记录浏览
    @Transactional
    public void recordBrowse(Long userId, Long animalId) {
        // 先删除旧记录
        LambdaQueryWrapper<BrowseHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrowseHistory::getUserId, userId)
                .eq(BrowseHistory::getAnimalId, animalId);
        browseHistoryMapper.delete(wrapper);

        // 插入新记录
        BrowseHistory history = new BrowseHistory();
        history.setUserId(userId);
        history.setAnimalId(animalId);
        browseHistoryMapper.insert(history);
    }

    // 获取浏览历史（最多20条）
    public List<Animal> getBrowseHistory(Long userId) {
        LambdaQueryWrapper<BrowseHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrowseHistory::getUserId, userId)
                .orderByDesc(BrowseHistory::getCreateTime)
                .last("OFFSET 0 ROWS FETCH NEXT 20 ROWS ONLY");

        List<BrowseHistory> histories = browseHistoryMapper.selectList(wrapper);
        return histories.stream()
                .map(h -> animalService.getAnimalById(h.getAnimalId()))
                .filter(a -> a != null && a.getStatus() == 1)
                .collect(Collectors.toList());
    }
}