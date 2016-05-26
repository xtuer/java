package com.xtuer.service;

import com.xtuer.mapper.ParticipantGiftMapper;
import com.xtuer.bean.ParticipantGift;
import com.xtuer.bean.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class GiftService {
    private static Logger logger = LoggerFactory.getLogger(GiftService.class);
    public static final int GIFT_COUNT = 5; // 奖品的个数, 用于生成 0 到 4 的序号进行抽奖
    public static final int PAGE_SIZE = 5;

    @Autowired
    private ParticipantGiftMapper participantGiftMapper;

    public int generateGiftCode() {
        // 随机生成 gift code
        Random random = new Random();
        int giftCode = random.nextInt(GIFT_COUNT);

        return giftCode;
    }

    public Result insertParticipantGift(int participantId, int giftCode) {
        // 插入表 participant_gift 中
        if (participantId > 0 && giftCode >= 0) {
            participantGiftMapper.insertParticipantGift(participantId, giftCode);

            return new Result(true, "成功");
        }

        logger.info("创建 gift 失败, participantId({}) 和 giftCode({}) 必须大于 0", participantId, giftCode);
        return new Result(false, "保存失败");
    }

    public List<ParticipantGift> selectParticipantGifts(int page) {
        page = page > 1 ? page : 1; // 最小是 1
        int offset = PAGE_SIZE * (page - 1);

        return participantGiftMapper.selectParticipantGifts(offset, PAGE_SIZE);
    }

    public int selectPagesCountOfAllParticipantGifts() {
        return (participantGiftMapper.countOfAllParticipantGifts() - 1) / PAGE_SIZE + 1;
    }

    public Result updateParticipantGiftDescription(int participantGiftId, String description) {
        participantGiftMapper.updateGiftDescription(participantGiftId, description);
        return new Result(true, "成功");
    }
}
