package com.ksptooi.biz.core.repository;

import com.ksptooi.biz.core.model.config.ConfigPo;
import com.ksptooi.biz.core.model.config.GetConfigListVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends JpaRepository<ConfigPo, Long> {


    @Query("""
        SELECT c FROM ConfigPo c
        WHERE c.configKey = :key
        """)
    ConfigPo getGlobalConfig(@Param("key") String key);


    boolean existsByUserIdAndConfigKey(Long playerId, String configKey);


    @Query("""
            SELECT new com.ksptooi.biz.core.model.config.GetConfigListVo(
                c.id, c.user.username, c.configKey,
                c.configValue, c.description, c.createTime, c.updateTime
            )
            FROM ConfigPo c
            WHERE (:keyword IS NULL
                OR c.configKey LIKE %:keyword%
                OR c.configValue LIKE %:keyword%
                OR c.description LIKE %:keyword%
            )
            AND (:userName IS NULL
                OR (c.user.username LIKE CONCAT('%', :userName, '%') AND :userName != '全局')
                OR (c.user IS NULL AND :userName = '全局')
            )
            AND (:userId IS NULL OR c.user.id = :userId)
            ORDER BY c.configKey ASC,c.updateTime DESC
            """)
    Page<GetConfigListVo> getConfigList(@Param("keyword") String keyword,
                                        @Param("userName") String userName,
                                        @Param("userId") Long userId,
                                        Pageable pageable);

}