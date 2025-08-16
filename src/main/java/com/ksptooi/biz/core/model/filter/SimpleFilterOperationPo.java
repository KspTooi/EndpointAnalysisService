package com.ksptooi.biz.core.model.filter;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "simple_filter_request_operation")
@Getter
@Setter
@Comment("过滤器操作")
public class SimpleFilterOperationPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("操作ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "filter_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("过滤器ID")
    private SimpleFilterPo filter;

    @Comment("操作名称")
    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Comment("类型 0:持久化 1:缓存 2:注入缓存 3.注入持久化 4:覆写URL")
    @Column(name = "kind", nullable = false, columnDefinition = "tinyint")
    private Integer kind;

    @Comment("目标 0:标头 1:JSON载荷 2:URL(仅限kind=4)")
    @Column(name = "target", nullable = false, columnDefinition = "tinyint")
    private Integer target;

    @Comment("原始键")
    @Column(name = "from", length = 128, nullable = false)
    private String from;

    @Comment("目标键")
    @Column(name = "to", length = 128, nullable = false)
    private String to;

    @Comment("排序")
    @Column(name = "seq", nullable = false)
    private Integer seq;
}
