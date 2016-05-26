package com.xtuer.controller;

import com.xtuer.bean.ParticipantGift;
import com.xtuer.bean.Result;
import com.xtuer.service.GiftService;
import com.xtuer.util.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class GiftController {
    private static Logger logger = LoggerFactory.getLogger(GiftController.class.getName());

    public static final String GIFT_CODE = "giftCode";

    @Autowired
    private GiftService giftService;

    @RequestMapping(value = UriViewConstants.URI_ADMIN_PARTICIPANT_GIFTS, method = RequestMethod.GET)
    public String adminParticipantGiftsPage() {
        return UriViewConstants.VIEW_ADMIN_PARTICIPANT_GIFTS;
    }

    @RequestMapping(value = UriViewConstants.REST_GIFTS, method = RequestMethod.GET)
    @ResponseBody
    public Result selectGift(HttpSession session) {
        int participantId = SessionUtils.getIntegerFromSession(session, ParticipantController.USER_ID, 0);

        // 如果没有 participantId, 则说明还没有填写参与者的信息
        if (participantId == 0) {
            return new Result(false, "请先填写用户信息, 再参与抽奖");
        }

        // 生成 gift code
        int giftCode = giftService.generateGiftCode();
        session.setAttribute(GIFT_CODE, giftCode);
        logger.info("为用户 {} 生成奖品 ID {}", participantId, giftCode);

        return new Result(true, giftCode, "成功");
    }

    @RequestMapping(value = UriViewConstants.REST_GIFTS, method = RequestMethod.POST)
    @ResponseBody
    public Result insertGift(HttpSession session) {
        int participantId = SessionUtils.getIntegerFromSession(session, ParticipantController.USER_ID, 0);
        int giftCode = SessionUtils.getIntegerFromSession(session, GIFT_CODE, -1);
        Result result = giftService.insertParticipantGift(participantId, giftCode);

        // 删除 session 里的 participantId 和 giftCode
        if (result.isSuccess()) {
            session.removeAttribute(ParticipantController.USER_ID);
            session.removeAttribute(GIFT_CODE);
        }

        return result;
    }

    @RequestMapping(value = UriViewConstants.REST_PARTICIPANT_GIFTS, method = RequestMethod.GET)
    @ResponseBody
    public List<ParticipantGift> selectParticipantGifts(@RequestParam(required = false, defaultValue = "1") int page) {
        return giftService.selectParticipantGifts(page);
    }


    @RequestMapping(value = UriViewConstants.REST_PARTICIPANT_GIFTS_PAGES_COUNT, method = RequestMethod.GET)
    @ResponseBody
    public int selectPagesCountOfAllParticipantGifts() {
        return giftService.selectPagesCountOfAllParticipantGifts();
    }

    @RequestMapping(value = UriViewConstants.REST_PARTICIPANT_GIFTS_DESCRIPTION, method = RequestMethod.PUT)
    @ResponseBody
    public Result updateParticipantGiftDescription(@PathVariable int participantGiftId, @RequestBody Map map) {
        String description = (String) map.get("description");
        return giftService.updateParticipantGiftDescription(participantGiftId, description);
    }
}
