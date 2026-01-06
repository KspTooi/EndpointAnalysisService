package com.ksptooi.commons.httprelay.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.MultiValueMap;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelayParam {

    @Schema(description = "是否启用")
    private boolean e;

    @Schema(description = "k")
    private String k;

    @Schema(description = "v")
    private String v;

    @Schema(description = "排序")
    private Integer s;

    @Override
    public String toString() {
        return "RelayParam[e=" + e + ", k=" + k + ", v=" + v + ", s=" + s + "]";
    }

    /**
     * 将MultiValueMap转换为RelayParam列表
     * @param map MultiValueMap
     * @return RelayParam列表
     */
    public static List<RelayParam> of(MultiValueMap<String, String> map) {

        if (map == null || map.isEmpty()) {
            return new ArrayList<>();
        }

        List<RelayParam> params = new ArrayList<>();
        final AtomicInteger index = new AtomicInteger(0);
        map.forEach((k, v) -> {
            RelayParam param = new RelayParam();
            param.setE(true);
            param.setK(k);
            param.setV(v.get(0));
            param.setS(index.getAndIncrement());
            params.add(param);
        });
        return params;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null || getClass() != obj.getClass()){
            return false;
        }
        RelayParam that = (RelayParam) obj;
        //只要k、v相等就认为相等
        return k.equals(that.k) && v.equals(that.v);
    }

    @Override
    public int hashCode() {
        return Objects.hash(k, v);
    }

}
