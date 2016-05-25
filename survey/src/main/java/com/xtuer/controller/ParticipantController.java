package com.xtuer.controller;

import com.xtuer.bean.Participant;
import com.xtuer.bean.Result;
import com.xtuer.service.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
public class ParticipantController {
    public static final String USER_ID = "userId";

    @Autowired
    private ParticipantService participantService;

    @RequestMapping(value = UriViewConstants.URI_PARTICIPANT, method = RequestMethod.GET)
    public String participantForm() {
        return UriViewConstants.VIEW_PARTICIPANT;
    }

    // 创建参与者并存储他的 id 到 session.
    @RequestMapping(value = UriViewConstants.URI_PARTICIPANT, method = RequestMethod.PUT)
    @ResponseBody
    public Result insertParticipant(@RequestBody @Valid Participant participant, BindingResult result, HttpSession session) {
        if (result.hasErrors()) {
            return new Result(false, result.getFieldErrors().toString());
        }

        Result r = participantService.insertParticipant(participant); // 创建参与者

        if (r.isSuccess()) {
            session.setAttribute(USER_ID, participant.getId()); // 存储他的 id 到 session
        }

        return r;
    }

    @RequestMapping(value = UriViewConstants.URI_PARTICIPANT_TELEPHONE_UNUSED)
    @ResponseBody
    public boolean isTelephoneUnused(String telephone) {
        return !participantService.isTelephoneUsed(telephone);
    }

    @RequestMapping(value = UriViewConstants.URI_INNER_PARTICIPANTS_WITH_PAGE)
    @ResponseBody
    public List<Participant> selectParticipantsWithLimit(@PathVariable int page) {
        return participantService.selectParticipantsWithPage(page);
    }

    @RequestMapping(value = UriViewConstants.URI_INNER_PARTICIPANTS)
    @ResponseBody
    public List<Participant> selectFirst50Participants() {
        return participantService.selectParticipantsWithPage(1); // 选择前 50 个
    }

    @RequestMapping(value = UriViewConstants.URI_INNER_PARTICIPANTS_PAGES_COUNT)
    @ResponseBody
    public int selectPagesCountOfAllParticipants() {
        return participantService.selectPagesCountOfAllParticipants();
    }
}
