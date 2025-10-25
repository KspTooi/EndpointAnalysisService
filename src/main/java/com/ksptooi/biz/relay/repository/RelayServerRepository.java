package com.ksptooi.biz.relay.repository;

import com.ksptooi.biz.relay.model.relayserver.GetRelayServerListDto;
import com.ksptooi.biz.relay.model.relayserver.RelayServerPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelayServerRepository extends CrudRepository<RelayServerPo, Long> {

    @Query("""
                    SELECT t FROM RelayServerPo t
                    WHERE
                    (:#{#dto.name} IS NULL OR t.name LIKE CONCAT('%', :#{#dto.name}, '%'))
                    AND (:#{#dto.forwardUrl} IS NULL OR t.forwardUrl LIKE CONCAT('%', :#{#dto.forwardUrl}, '%'))
                    ORDER BY t.createTime DESC
            """)
    Page<RelayServerPo> getRelayServerList(@Param("dto") GetRelayServerListDto dto, Pageable pageable);

    /**
     * 根据名称统计中继服务器数量
     *
     * @param name 中继服务器名称
     * @return 中继服务器数量
     */
    @Query("""
                    SELECT COUNT(t) FROM RelayServerPo t
                    WHERE t.name = :name
            """)
    Long countByName(@Param("name") String name);


    /**
     * 根据主机和端口统计中继服务器数量
     *
     * @param host 主机
     * @param port 端口
     * @return 中继服务器数量
     */
    @Query("""
                    SELECT COUNT(t) FROM RelayServerPo t
                    WHERE t.host = :host AND t.port = :port
            """)
    Long countByHostAndPort(@Param("host") String host, @Param("port") Integer port);

    /**
     * 根据名称统计中继服务器数量 排除指定ID
     *
     * @param name 中继服务器名称
     * @param id   需排除的ID
     * @return 中继服务器数量
     */
    @Query("""
                    SELECT COUNT(t) FROM RelayServerPo t
                    WHERE t.name = :name AND t.id != :id
            """)
    Long countByNameExcludeId(@Param("name") String name, @Param("id") Long id);

    /**
     * 根据主机和端口统计中继服务器数量 排除指定ID
     *
     * @param host 主机
     * @param port 端口
     * @param id   需排除的ID
     * @return 中继服务器数量
     */
    @Query("""
                    SELECT COUNT(t) FROM RelayServerPo t
                    WHERE t.host = :host AND t.port = :port AND t.id != :id
            """)
    Long countByHostAndPortExcludeId(@Param("host") String host, @Param("port") Integer port, @Param("id") Long id);


    /**
     * 将所有中继服务器修改为停止状态
     */
    @Modifying
    @Query("""
                    UPDATE RelayServerPo t SET t.status = 1 WHERE t.status != 0
            """)
    void stopAllRelayServer();

    /**
     * 获取所有自动运行的中继服务器
     *
     * @return 中继服务器列表
     */
    @Query("""
                    SELECT t FROM RelayServerPo t WHERE t.status = 1 AND t.autoStart = 1
            """)
    List<RelayServerPo> getAutoStartRelayServerList();

}
