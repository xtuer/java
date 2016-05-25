package com.xtuer.mapper;

import com.xtuer.bean.ParticipantGift;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ParticipantGiftMapper {
    List<ParticipantGift> selectParticipantGifts(@Param("offset") int offset, @Param("count") int count);
    int countOfAllParticipantGifts();
    void insertParticipantGift(@Param("participantId") int participantId, @Param("giftCode") int giftCode);
    void updateGiftDescription(@Param("id") int id, @Param("description") String description);
}
