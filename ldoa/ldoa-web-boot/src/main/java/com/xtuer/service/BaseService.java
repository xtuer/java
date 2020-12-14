package com.xtuer.service;

import com.github.wujun234.uid.impl.CachedUidGenerator;
import com.xtuer.config.AppConfig;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class BaseService {
    @Autowired
    private CachedUidGenerator uidGenerator;

    @Autowired
    protected UserService userService;

    @Autowired
    protected TempFileService tempFileService;

    @Autowired
    protected RepoFileService repoFileService;

    @Autowired
    protected AppConfig config;

    @Autowired
    private CommonService commonService;

    /**
     * 生成唯一的 64 位 long 的 ID
     *
     * @return 返回唯一 ID
     */
    final public long nextId() {
        return uidGenerator.getUID();
    }

    /**
     * 按年生成下一个序列号，每年从一开始。
     *
     * 示例:
     * 生成订单号，传入 XSDD => XSDD-20201213-0001
     * 生成出库单，传入 PTD  => PTD-20201213-0001
     *
     * @param sequenceName 序列号的名字
     * @return 序列号
     */
    final public String nextSnByYear(String sequenceName) {
        // XSDD-20201213-0001: XSDD 不动, 20200806 为年月日，根据日期自动生成, 0001 为流水号 (0001,0002,003……)，每年再从 0001 开始
        String year = DateTimeFormatter.ofPattern("yyyy").format(LocalDate.now());
        String date = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        String name = sequenceName + "-" + year;
        String n    = commonService.nextSequence(name) + "";
        String sn   = sequenceName + "-" + date + "-" + StringUtils.leftPad(n, 4, "0");

        return sn;
    }
}
