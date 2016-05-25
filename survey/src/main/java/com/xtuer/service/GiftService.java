package com.xtuer.service;

import com.xtuer.mapper.ParticipantGiftMapper;
import com.xtuer.bean.ParticipantGift;
import com.xtuer.bean.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class GiftService {
    public static final int GIFT_COUNT = 5;
    public static final int PAGE_SIZE = 50;

    @Autowired
    private ParticipantGiftMapper participantGiftMapper;

    public int getGiftCode() {
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

        return new Result(false, "失败");
    }

    public List<ParticipantGift> selectParticipantGifts(int page) {
        page = page > 1 ? page : 1; // 最小是 1
        int offset = PAGE_SIZE * (page - 1);
        int count = PAGE_SIZE * page - 1;

        return participantGiftMapper.selectParticipantGifts(offset, count);
    }

    public int selectPagesCountOfAllParticipantGifts() {
        return participantGiftMapper.countOfAllParticipantGifts() / PAGE_SIZE + 1;
    }

    public Result updateParticipantGiftDescription(ParticipantGift pg) {
        if (pg != null) {
            participantGiftMapper.updateGiftDescription(pg.getId(), pg.getDescription());
            return new Result(true, "成功");
        }

        return new Result(false, "失败");
    }
}
