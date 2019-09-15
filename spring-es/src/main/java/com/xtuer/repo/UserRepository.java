package com.xtuer.repo;

import com.xtuer.bean.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends ElasticsearchRepository<User, Long> {
    List<User> findByNickname(String nickname);
    List<User> findByNickname(String nickname, PageRequest pageRequest);
}
