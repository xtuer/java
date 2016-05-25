package com.xtuer.util;

public interface RegularExpressionPattern {
    String MAIL = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$";

    // 如：0591-6487256，15005059587
    String TELEPHONE = "^(0[0-9]{2,3}\\-)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?$|(^(13|15|18)\\d{9}$)";
}
