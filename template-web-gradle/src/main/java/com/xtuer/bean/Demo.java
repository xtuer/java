package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class Demo {
    @NotNull(message="ID 不能为 null")
    @Min(value=1, message="ID 不能小于 1")
    private Long id;

    @NotBlank(message="Info 不能为空")
    private String info;

    public Demo() {
    }

    public Demo(Long id, String info) {
        this.id = id;
        this.info = info;
    }
}
