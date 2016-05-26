package com.xtuer.controller;

import com.xtuer.bean.Participant;
import com.xtuer.bean.Result;
import com.xtuer.service.ParticipantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
public class ParticipantController {
    private static Logger logger = LoggerFactory.getLogger(ParticipantController.class);
    public static final String USER_ID = "userId";

    @Autowired
    private ParticipantService participantService;

    @RequestMapping(value = UriViewConstants.URI_PARTICIPANT, method = RequestMethod.GET)
    public String participantPage() {
        return UriViewConstants.VIEW_PARTICIPANT;
    }

    // 创建参与者并存储他的 id 到 session.
    @RequestMapping(value = UriViewConstants.REST_PARTICIPANTS, method = RequestMethod.POST)
    @ResponseBody
    public Result insertParticipant(@RequestBody @Valid Participant participant, BindingResult result, HttpSession session) {
        if (result.hasErrors()) {
            logger.info("Participant 验证错误: {}", result.getFieldErrors().toString());
            return new Result(false, result.getFieldErrors().toString());
        }

        Result r = participantService.insertParticipant(participant); // 创建参与者

        if (r.isSuccess()) {
            logger.info("Participant 创建成功, 保存其 ID {} 到 Session", participant.getId());
            session.setAttribute(USER_ID, participant.getId()); // 存储他的 id 到 session
        }

        return r;
    }

    @RequestMapping(value = UriViewConstants.REST_PARTICIPANT_TELEPHONE_NUMBER_UNUSED, method = RequestMethod.GET)
    @ResponseBody
    public boolean isTelephoneUnused(@RequestParam String telephoneNumber) {
        return !participantService.isTelephoneUsed(telephoneNumber);
    }

    @RequestMapping(value = UriViewConstants.REST_PARTICIPANTS, method = RequestMethod.GET)
    @ResponseBody
    public List<Participant> selectParticipantsWithLimit(@RequestParam(defaultValue = "1", required = false) int page) {
        return participantService.selectParticipantsWithPage(page);
    }

    @RequestMapping(value = UriViewConstants.REST_PARTICIPANTS_PAGE_COUNT, method = RequestMethod.GET)
    @ResponseBody
    public int selectPagesCountOfAllParticipants() {
        return participantService.selectPagesCountOfAllParticipants();
    }
}
