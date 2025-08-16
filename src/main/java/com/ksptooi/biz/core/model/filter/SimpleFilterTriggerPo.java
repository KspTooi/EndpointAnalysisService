package com.ksptooi.biz.core.model.filter;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "simple_filter_trigger")
@Getter
@Setter
@Comment("过滤器触发器")
public class SimpleFilterTriggerPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("触发器ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "filter_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("过滤器ID")
    private SimpleFilterPo filter;

    @Comment("触发器名称")
    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Comment("目标 0:标头 1:JSON载荷 2:URL 3:HTTP方法")
    @Column(name = "target", nullable = false, columnDefinition = "tinyint")
    private Integer target;

    @Comment("条件 0:包含 1:不包含 2:等于 3:不等于")
    @Column(name = "kind", nullable = false, columnDefinition = "tinyint")
    private Integer kind;

    @Comment("目标键")
    @Column(name = "tk", length = 128, nullable = false)
    private String tk;

    @Comment("比较值")
    @Column(name = "tv", length = 128, nullable = false)
    private String tv;

    @Comment("排序")
    @Column(name = "seq", nullable = false, columnDefinition = "int")
    private Integer seq;

}
