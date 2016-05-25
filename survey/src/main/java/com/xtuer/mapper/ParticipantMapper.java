package com.xtuer.mapper;

import com.xtuer.bean.Participant;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ParticipantMapper {
    void insertParticipant(Participant participant);
    int countOfTelephone(String telephone);
    int countOfAllParticipants();
    List<Participant> selectParticipants(@Param("offset") int offset, @Param("count") int count);
}
