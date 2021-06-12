package com.runmate.repository.redis;

import com.runmate.domain.redis.TeamInfo;
import org.springframework.data.repository.CrudRepository;

public interface TeamInfoRepository extends CrudRepository<TeamInfo, Long> {
}
