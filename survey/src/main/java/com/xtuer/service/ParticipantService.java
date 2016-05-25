package com.xtuer.service;

import com.xtuer.mapper.ParticipantMapper;
import com.xtuer.bean.Participant;
import com.xtuer.bean.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParticipantService {
    private static final int PAGE_SIZE = 50;

    @Autowired
    private ParticipantMapper participantMapper;

    public boolean isTelephoneUsed(String telephone) {
        return participantMapper.countOfTelephone(telephone) > 0;
    }

    @Transactional
    public Result insertParticipant(Participant participant) {
        if (isTelephoneUsed(participant.getTelephone())) {
            return new Result(false, "电话号码已经使用过");
        }

        participantMapper.insertParticipant(participant);

        return new Result(true, "参与者创建成功");
    }

    public List<Participant> selectParticipantsWithPage(int page) {
        page = page > 0 ? page : 1; // 最小是 1
        int offset = PAGE_SIZE * (page - 1);
        int count = PAGE_SIZE * page - 1;

        return participantMapper.selectParticipants(offset, count);
    }

    public int selectPagesCountOfAllParticipants() {
        return participantMapper.countOfAllParticipants() / PAGE_SIZE + 1;
    }
}
