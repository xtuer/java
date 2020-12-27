package com.xtuer.service;

import com.xtuer.bean.Result;
import com.xtuer.bean.User;
import com.xtuer.bean.audit.Audit;
import com.xtuer.bean.audit.AuditConfig;
import com.xtuer.bean.audit.AuditItem;
import com.xtuer.bean.audit.AuditType;
import com.xtuer.bean.order.Order;
import com.xtuer.bean.stock.StockRequest;
import com.xtuer.mapper.AuditMapper;
import com.xtuer.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 审核服务
 */
@Service
@Slf4j
public class AuditService extends BaseService {
    @Autowired
    private AuditMapper auditMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StockService stockService;

    /**
     * 查询审批
     *
     * @param auditId 审批 ID
     * @return 返回查询到的审批，查询不到返回 null
     */
    public Audit findAudit(long auditId) {
        return findAuditByAuditIdOrTargetId(auditId, 0);
    }

    /**
     * 查询目标对象的审批 (例如在订单详情页查看订单的审批，不需要直接指定审批 ID)
     *
     * @param targetId 审批对象 ID
     * @return 返回查询到的审批，查询不到返回 null
     */
    public Audit findAuditOfTarget(long targetId) {
        return findAuditByAuditIdOrTargetId(0, targetId);
    }

    /**
     * 查询指定 ID 的审批或者审批目标的审批:
     * * auditId 不为 0 时查询指定 ID 的审批
     * * auditId 等于 0 时查询审批目标的审批
     *
     * @param auditId  审批 ID
     * @param targetId 审批目标的 ID
     * @return 返回查询到的审批，查询不到返回 null
     */
    private Audit findAuditByAuditIdOrTargetId(long auditId, long targetId) {
        // 1. 查询审批
        // 2. 查询审批项
        // 3. 查询审批配置

        // [1] 查询审批
        Audit audit;

        if (auditId > 0) {
            audit = auditMapper.findAuditById(auditId);
        } else {
            audit = auditMapper.findAuditByTargetId(targetId);
        }

        if (audit == null) {
            return null;
        }

        // [2] 查询审批项
        List<AuditItem> items = auditMapper.findAuditItemsByAuditId(audit.getAuditId());
        audit.setItems(items);

        // [3] 查询审批配置
        AuditConfig config = auditMapper.findAuditConfigByType(audit.getType());
        audit.setConfig(config);

        return audit;
    }

    /**
     * 更新或者创建订单的审批
     *
     * @param applicant 申请人
     * @param order     订单
     * @return 返回操作结果
     */
    public Result<String> upsertOrderAudit(User applicant, Order order) {
        Objects.requireNonNull(order, "订单不能为空");
        String desc = String.format("销售订单: %s, 客户: %s", order.getOrderSn(), order.getCustomerCompany());

        return upsertAudit(applicant, AuditType.ORDER, order.getOrderId(), Utils.toJson(order), desc);
    }

    /**
     * 创建物料出库申请
     *
     * @param applicant 申请人
     * @param request   出库申请
     * @return 返回操作结果
     */
    public Result<String> insertStockRequestAudit(User applicant, StockRequest request) {
        Objects.requireNonNull(request, "出库申请不能为空");
        String desc = String.format("出库单号: %s, 物料: %s", request.getStockRequestSn(), request.getDesc());

        return upsertAudit(applicant, AuditType.OUT_OF_STOCK, request.getStockRequestId(), Utils.toJson(request), desc);
    }

    /**
     * 更新或者创建审批
     *
     * @param applicant 申请人
     * @param type      审批类型
     * @param targetId  审批对象的 ID
     * @param desc      审批的简要描述
     * @return 返回操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<String> upsertAudit(User applicant, AuditType type, long targetId, String targetJson, String desc) {
        // 1. 查询审批配置
        // 2. 校验审批配置，如果审批配置无效则返回
        // 3. 查询审批，不存在则创建
        // 4. 创建审批项
        // 5. 设置 step 为 1 的审批项的状态为 1 (待审批)
        // 6. 删除同一个 targetId + type 的审批项，例如审批被拒绝后重新提交审批
        // 7. 保存到数据库

        // [1] 查询审批配置
        AuditConfig config = auditMapper.findAuditConfigByType(type);

        // [2] 校验审批配置，如果审批配置无效则返回
        Result<String> checkResult = checkAuditConfig(config, type);
        if (!checkResult.isSuccess()) {
            return checkResult;
        }

        // [3] 查询审批，不存在则创建
        Audit audit = auditMapper.findAuditByTargetId(targetId);

        if (audit == null) {
            audit = new Audit()
                    .setType(type)
                    .setAuditId(super.nextId())
                    .setApplicantId(applicant.getUserId())
                    .setTargetId(targetId);
        }

        // 设置审批的目标和描述
        audit.setTargetJson(targetJson);
        audit.setDesc(desc);

        // [4] 创建审批项
        final Audit back = audit;
        config.getSteps().forEach(step -> {
            step.getAuditors().forEach(auditor -> {
               AuditItem item = new AuditItem()
                       .setType(type)
                       .setAuditId(back.getAuditId())
                       .setAuditItemId(super.nextId())
                       .setApplicantId(applicant.getUserId())
                       .setTargetId(targetId)
                       .setAuditorId(auditor.getUserId())
                       .setStep(step.getStep())
                       .setState(AuditItem.STATE_INIT);
                back.getItems().add(item);
            });
        });

        // [5] 设置 step 为 1 的审批项的状态为 1 (待审批)
        audit.getItems()
                .stream()
                .filter(item -> item.getStep() == 1)
                .forEach(item -> item.setState(AuditItem.STATE_WAIT));

        log.info("[开始] 创建审批, 用户: [{}], 类型: [{}], 审批对象 ID: [{}]", applicant.getNickname(), type, targetId);

        // [6] 删除同一个 targetId 的审批项，例如审批被拒绝后重新提交审批
        auditMapper.deleteAuditItemsByTargetId(targetId);

        // [7] 保存到数据库
        // 保存审批
        auditMapper.upsertAudit(audit);

        // 保存审批项
        auditMapper.insertAuditItems(audit.getItems());

        log.info("[成功] 创建审批, 用户: [{}], 类型: [{}], 审批对象 ID: [{}]", applicant.getNickname(), type, targetId);

        return Result.ok();
    }

    /**
     * 校验审批配置，审批配置有效返回 Result.ok()，无效返回 Result.fail()
     *
     * @param config 审批配置
     * @param type   审批类型
     * @return 审批配置有效返回 Result.ok()，无效返回 Result.fail()
     */
    public Result<String> checkAuditConfig(AuditConfig config, AuditType type) {
        // 1. 检查审批配置是否不存在
        // 2. 检查是否配置了审批流程
        // 3. 每个流程中都必须配置审批员

        if (config == null) {
            return Result.fail("审批配置不存在: " + type);
        }

        if (config.getSteps().size() == 0) {
            return Result.fail("没有配置审批流程: " + type);
        }

        for (AuditConfig.AuditConfigStep step : config.getSteps()) {
            if (step.getAuditors().size() == 0) {
                return Result.fail("审批的流程中有的没有配置审批员，请核查对应的审批配置");
            }
        }

        return Result.ok();
    }

    /**
     * 插入或者更新审批配置
     *
     * @param configs 审批配置数组
     */
    @Transactional(rollbackFor = Exception.class)
    public void upsertAuditConfigs(List<AuditConfig> configs) {
        configs.forEach(config -> {
            auditMapper.upsertAuditConfig(config);
        });
    }

    /**
     * 审批: 通过或者拒绝审批项
     *
     * @param auditItemId 审批项 ID
     * @param accepted    true 为通过，false 为拒绝
     * @param comment     审批意见
     */
    @Transactional(rollbackFor = Exception.class)
    public void acceptAuditItem(long auditItemId, boolean accepted, String comment) {
        // 审批项状态: 0 (初始化), 1 (待审批), 2 (拒绝), 3 (通过)
        // 1. 查询审批项，得到审批的 ID，然后获取此审批的所有审批项，计算出上一阶段、当前阶段、下一阶段的审批项
        // 2. 如果审批通过:
        //    2.1 更新当前审批项的状态为通过
        //    2.2 统计最大的阶段数和当前阶段的审批项数量、通过的审批项数量、审批员数量
        //    2.3 当前阶段的审批项都为通过时:
        //        2.3.1 如果是最后一阶段，则审批通过
        //        2.3.2 如果不是最后一阶段，则修改下一阶段的所有审批项的状态为待审批
        // 3. 如果审批被拒绝:
        //    3.1 更新当前审批项的状态为拒绝
        //    3.2 如果是第一阶段，则审批不通过
        //    3.3 如果不是第一阶段，更新当前阶段待审批状态的审批项状态为初始化, 上一阶段审批项的状态为待审批

        // [1] 查询审批项，得到审批的 ID，然后获取此审批的所有审批项，计算出上一阶段、当前阶段、下一阶段的审批项
        AuditItem item = auditMapper.findAuditItemByAuditItemId(auditItemId); // 当前审批项
        final int step = item.getStep(); // 当前审批项的阶段
        final long auditId = item.getAuditId();

        List<AuditItem> allItems          = auditMapper.findAuditItemsByAuditId(item.getAuditId()); // 所属审批的所有审批项
        List<AuditItem> currentStepItems  = allItems.stream().filter(t -> t.getStep() == step).collect(Collectors.toList());   // 当前阶段的审批项
        List<AuditItem> previousStepItems = allItems.stream().filter(t -> t.getStep() == step-1).collect(Collectors.toList()); // 上一阶段的审批项
        List<AuditItem> nextStepItems     = allItems.stream().filter(t -> t.getStep() == step+1).collect(Collectors.toList()); // 下一阶段的审批项

        log.info("[开始] 审批审批项: 审批 [{}], 审批项 [{}]", auditId, auditItemId);

        // 同步从数据库中查询得到的当前审批项的状态
        for (AuditItem t : currentStepItems) {
            if (t.getAuditItemId() == item.getAuditItemId()) {
                t.setState(accepted ? AuditItem.STATE_ACCEPTED : AuditItem.STATE_REJECTED);
                break;
            }
        }

        if (accepted) {
            // [2] 如果审批通过
            // [2.1] 更新当前审批项的状态为通过
            auditMapper.acceptOrRejectAuditItem(auditItemId, AuditItem.STATE_ACCEPTED, comment);
            log.info("[通过] 审批审批项，通过审批项: 审批 [{}], 审批项 [{}]", auditId, auditItemId);

            // [2.2] 统计最大的阶段数和当前阶段的审批项数量、通过的审批项数量、审批员数量
            final int maxStep = allItems.stream().mapToInt(AuditItem::getStep).max().orElse(Integer.MAX_VALUE); // 最大的阶段数
            final int currentStepItemCount = currentStepItems.size();                // 当前阶段的审批项数量
            final int currentStepAcceptedItemCount = (int) currentStepItems.stream() // 当前阶段通过的审批项数量
                    .filter(t -> t.getState() == AuditItem.STATE_ACCEPTED)
                    .count();

            // [2.3] 当前阶段的审批项都为通过时
            if (currentStepItemCount == currentStepAcceptedItemCount) {
                if (step == maxStep) {
                    // [2.3.1] 如果是最后一阶段，则审批通过
                    auditMapper.updateAuditState(item.getAuditId(), Audit.STATUS_ACCEPTED);
                    acceptTarget(item.getAuditId(), item.getTargetId(), item.getType());
                    log.info("[通过] 审批审批项，通过审批: 审批 [{}], 审批项 [{}]", auditId, auditItemId);
                } else {
                    // [2.3.2] 如果不是最后一阶段，则修改下一阶段的所有审批项的状态为待审批
                    nextStepItems.forEach(t -> auditMapper.updateAuditItemState(t.getAuditItemId(), AuditItem.STATE_WAIT));
                    log.info("[通过] 审批审批项，下一阶段审批项状态修改为待审批: 审批 [{}], 审批项 [{}]", auditId, auditItemId);
                }
            }
        } else {
            // [3] 如果审批被拒绝
            // [3.1] 更新当前审批项的状态为拒绝
            auditMapper.acceptOrRejectAuditItem(auditItemId, AuditItem.STATE_REJECTED, comment);
            log.info("[拒绝] 审批审批项，拒绝审批项: 审批 [{}], 审批项 [{}]", auditId, auditItemId);

            if (step == 1) {
                // [3.2] 如果是第一阶段，则审批不通过
                auditMapper.updateAuditState(item.getAuditId(), Audit.STATUS_REJECTED);
                rejectTarget(item.getAuditId(), item.getTargetId(), item.getType());
                log.info("[拒绝] 审批审批项，拒绝审批: 审批 [{}], 审批项 [{}]", auditId, auditItemId);
            } else {
                // [3.3] 如果不是第一阶段，更新当前阶段待审批状态的审批项状态为初始化, 上一阶段审批项的状态为待审批
                currentStepItems.stream()
                        .filter(t -> t.getState() == AuditItem.STATE_WAIT)
                        .forEach(t -> auditMapper.updateAuditItemState(t.getAuditItemId(), AuditItem.STATE_INIT));
                previousStepItems.forEach(t -> auditMapper.updateAuditItemState(t.getAuditItemId(), AuditItem.STATE_WAIT));
                log.info("[拒绝] 审批审批项，前一阶段审批项状态修改为待审批: 审批 [{}], 审批项 [{}]", auditId, auditItemId);
            }
        }

        log.info("[结束] 审批审批项: 审批 [{}], 审批项 [{}]", auditId, auditItemId);
    }

    /**
     * target 的审批通过
     *
     * @param auditId  审批的 ID
     * @param targetId 审批目标的 ID
     * @param type     审批的类型
     */
    public void acceptTarget(long auditId, long targetId, AuditType type) {
        if (type == AuditType.ORDER) {
            orderService.acceptOrder(targetId);
        } else if (type == AuditType.OUT_OF_STOCK) {
            stockService.acceptStockRequest(targetId);
        }
    }

    /**
     * target 的审批被拒绝
     *
     * @param auditId  审批的 ID
     * @param targetId 审批目标的 ID
     * @param type     审批的类型
     */
    public void rejectTarget(long auditId, long targetId, AuditType type) {
        if (type == AuditType.ORDER) {
            orderService.rejectOrder(targetId);
        } else if (type == AuditType.OUT_OF_STOCK) {
            stockService.rejectStockRequest(targetId);
        }
    }
}
