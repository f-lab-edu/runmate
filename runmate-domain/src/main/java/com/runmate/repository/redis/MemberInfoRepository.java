package com.runmate.repository.redis;

import com.runmate.domain.redis.MemberInfo;
import org.springframework.data.repository.CrudRepository;

public interface MemberInfoRepository extends CrudRepository<MemberInfo, Long> {
}
