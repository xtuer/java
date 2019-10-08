package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@Document(indexName = "user")
public class User {
    @Id
    private long   id;

    private String username; // 英文

    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_smart")
    private String nickname; // 中文

    private Address address = new Address("北京", "朝阳");

    private List<Address> addresses = new LinkedList<>();

    public User() {}

    public User(long id, String username, String nickname) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;

        addresses.add(new Address("河北", "保定"));
        addresses.add(new Address("河北", "涿州"));
    }
}
