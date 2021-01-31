package com.xtuer.service;

import com.xtuer.bean.Const;
import com.xtuer.bean.Result;
import com.xtuer.bean.User;
import com.xtuer.bean.product.ProductItem;
import com.xtuer.bean.stock.StockOutRequestFormBean;
import com.xtuer.bean.stock.StockRecord;
import com.xtuer.bean.stock.StockRequest;
import com.xtuer.exception.ApplicationException;
import com.xtuer.mapper.ProductMapper;
import com.xtuer.mapper.StockMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 库存的服务
 */
@Service
@Slf4j
public class StockService extends BaseService {
    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private AuditServiceHelper auditServiceHelper;

    /**
     * 查询指定 ID 的库存操作申请
     *
     * @param requestId 库存操作申请 ID
     * @return 返回查询到的库存操作申请，查询不到返回 null
     */
    public StockRequest findStockRequest(long requestId) {
        // 1. 查询库存操作申请基本信息
        // 2. 查询库存操作记录
        // 3. 查询操作的产品项
        // 4. 合并产品项到操作记录相关的产品项

        // [1] 查询库存操作申请基本信息
        StockRequest request = stockMapper.findStockRequestById(requestId);

        if (request == null) {
            return null;
        }

        // [2] 查询库存操作记录
        List<StockRecord> records = stockMapper.findStockRecordsByStockRequestId(requestId);
        request.setRecords(records);

        // [3] 查询操作的产品项
        List<ProductItem> productItems = stockMapper.findProductItemsByStockRequestId(requestId);

        // [4] 合并产品项到操作记录相关的产品项
        for (ProductItem item : productItems) {
            for (StockRecord record : records) {
                if (record.getProductItemId() == item.getProductItemId()) {
                    record.setProductItem(item);
                }
            }
        }

        return request;
    }

    /**
     * 入库
     *
     * @param record 库存记录
     * @param user   操作员
     * @return payload 为更新后的库记录
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<StockRecord> stockIn(StockRecord record, User user) {
        // 1. 数据校验
        // 2. 设置入库的相关数据
        // 3. 创建入库记录
        // 4. 入库: 增加产品项的数量
        // 5. 查询新创建的记录

        // [1] 数据校验
        if (record.getCount() <= 0) {
            return Result.fail("入库数量必须大于 0");
        }
        if (StringUtils.isBlank(record.getBatch())) {
            return Result.fail("批次不能为空");
        }

        // [2] 设置入库的相关数据
        record.setStockRecordId(super.nextId());
        record.setType(StockRecord.Type.IN);
        record.setUserId(user.getUserId());
        record.setStockRequestId(0);
        record.setComplete(true);

        // [3] 创建入库记录
        // [4] 入库: 增加产品项的数量

        stockMapper.insertStockRecord(record);
        productMapper.increaseProductItemCount(record.getProductItemId(), record.getCount());

        // [5] 查询新创建的记录
        record = stockMapper.findStockRecordById(record.getStockRecordId());

        return Result.ok(record);
    }

    /**
     * 出库申请
     *
     * @param out  出库信息
     * @param user 操作员
     * @return payload 为新创建的出库请求
     * @exception ApplicationException 审批配置无效时抛异常
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<StockRequest> stockOutRequest(StockOutRequestFormBean out, User user) {
        // 1. 如果没有出库物料，则返回
        // 2. 如果订单已经有出库申请，则不允许继续申请
        // 3. 创建出库请求
        // 4. 创建出库记录，每个物料一个出库记录，但标记为未完成，等待审批通过后才能领取物料，从库存中减去相应的数量
        // 5. 创建出库申请的审批，失败则不继续处理

        // [1] 如果没有出库物料，则返回
        if (CollectionUtils.isEmpty(out.getProductItems())) {
            return Result.fail("没有出库物料");
        }

        // 订单 ID
        long orderId = out.getOrderId();

        // [2] 如果订单已经有出库申请，则不允许继续申请
        if (orderId > 0 && stockMapper.hasOrderStockOutRequest(orderId)) {
            log.warn("订单 [{}] 已经存在出库申请，请不要重复申请", orderId);
            return Result.fail("订单已经存在出库申请，请不要重复申请");
        }

        // Result<Boolean> validateResult = this.checkProductItemsStock(out.getProductItems());
        // if (!validateResult.isSuccess()) {
            // return Result.fail(validateResult.getMessage());
        // }

        // 出库描述: 物料名称拼接在一起
        String desc = out.getProductItems().stream().map(ProductItem::getName).collect(Collectors.joining(", "));

        // [3] 创建出库请求
        StockRequest request = new StockRequest();
        request.setStockRequestId(super.nextId());
        request.setStockRequestSn(this.nextStockOutSn());
        request.setType(StockRecord.Type.OUT);
        request.setOrderId(orderId);
        request.setState(StockRequest.STATE_AUDITING); // 状态为审批中
        request.setApplicantId(user.getUserId());
        request.setApplicantUsername(user.getNickname());
        request.setDesc(desc);
        request.setCreatedAt(new Date());

        stockMapper.insertStockRequest(request);

        // [4] 创建出库记录，每个物料一个出库记录，但标记为未完成，等待审批通过后才能领取物料，从库存中减去相应的数量
        for (ProductItem item : out.getProductItems()) {
            StockRecord record = new StockRecord();
            record.setStockRecordId(super.nextId());
            record.setType(StockRecord.Type.OUT);
            record.setUserId(user.getUserId());
            record.setStockRequestId(request.getStockRequestId());
            record.setStockRequestSn(request.getStockRequestSn());
            record.setProductId(item.getProductId());
            record.setProductItemId(item.getProductItemId());
            record.setCount(item.getCount());
            record.setComplete(false); // 标记为未完成

            stockMapper.insertStockRecord(record);
        }

        // [5] 创建出库申请的审批，失败则不继续处理，抛异常是为了事务回滚
        auditServiceHelper.insertStockRequestAudit(user, request);

        return Result.ok(request);
    }

    /**
     * 校验物料库存，库存不够则返回 false
     *
     * @param items 物料数组
     * @return 库存充足返回 true，否则返回 false
     */
    private Result<Boolean> checkProductItemsStock(Collection<ProductItem> items) {
        // 1. 把相同的物料合并到一起，count 为所有相同物料的 count 之和
        // 2. 当所有物料的库存都满足条件时则返回 true，否则返回 false

        // [1] 把相同的物料合并到一起，count 为所有相同物料的 count 之和
        items = items.stream().collect(Collectors.toMap(ProductItem::getProductItemId, i -> i, (o, n) -> {
                o.setCount(o.getCount() + n.getCount());
                return o;
        })).values();

        // [2] 当所有物料的库存都满足条件时则返回 true，否则返回 false
        for (ProductItem item : items) {
            int count = productMapper.findProductItemCount(item.getProductItemId());

            if (count < item.getCount()) {
                return Result.fail("库存不足: 物料[{}]的库存为 {}，需要 {}",
                        item.getName(),
                        count + " " + item.getUnit(),
                        item.getCount() + " " + item.getUnit());
            }
        }

        return Result.ok();
    }

    /**
     * 生成出库请求的单号，格式为 PTD-20200806-0001
     *
     * @return 返回单号
     */
    private String nextStockOutSn() {
        return super.nextSnByYear(Const.SN_PREFIX_STOCK_OUT);
    }

    /**
     * 库存操作审批通过
     *
     * @param requestId 库存操作 ID
     */
    public void acceptStockRequest(long requestId) {
        stockMapper.updateStockRequestState(requestId, StockRequest.STATE_ACCEPTED);
    }

    /**
     * 库存操作审批被拒绝
     *
     * @param requestId 库存操作 ID
     */
    public void rejectStockRequest(long requestId) {
        stockMapper.updateStockRequestState(requestId, StockRequest.STATE_REJECTED);
    }

    /**
     * 物料出库
     *
     * @param requestId 库存操作 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> stockOut(long requestId) {
        // 1. 查询出库申请的物料出库记录
        // 2. 校验物料库存，库存不足则不允许出库
        // 3. 出库，每个物料的库存减去对应的数量
        // 4. 更新物料出库记录的状态为完成
        // 5. 更新出库申请的状态为完成

        // [1] 查询出库申请的物料出库记录
        List<StockRecord> records = stockMapper.findStockRecordsByStockRequestId(requestId);

        // [2] 校验物料库存，库存不足则不允许出库
        Collection<ProductItem> items = records.stream().map(StockRecord::getProductItem).collect(Collectors.toList()); // 获取物料
        Result<Boolean> validateResult = this.checkProductItemsStock(items);
        if (!validateResult.isSuccess()) {
            return Result.fail(validateResult.getMessage());
        }

        // [3] 出库，每个物料的库存减去对应的数量
        for (StockRecord record : records) {
            productMapper.increaseProductItemCount(record.getProductItemId(), -record.getCount()); // 出库，数量为负值
        }

        // [4] 更新物料出库记录的状态为完成
        stockMapper.completeStockRecordByRequestId(requestId);

        // [5] 更新出库申请的状态为完成
        stockMapper.updateStockRequestState(requestId, StockRequest.STATE_COMPLETE);

        return Result.ok();
    }
}
