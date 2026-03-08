package com.ksptool.bio.commons.config;

import com.ksptool.entities.Entities;
import com.ksptool.entities.mapper.EntityMapper;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Configuration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Configuration
public class EntityMapperConfig implements EntityMapper {

    private static final ModelMapper mMapper = new ModelMapper();

    static {
        //将全局Assign或as的默认转换器实现变更为当前的转换器
        Entities.setObjectMapper(new EntityMapperConfig());
    }

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public EntityMapperConfig() {
        init();
    }

    public void init() {

        Converter<Integer, String> toStringConverter = new Converter<Integer, String>() {
            public String convert(MappingContext<Integer, String> context) {
                return context.getSource() != null ? context.getSource().toString() : null;
            }
        };

        Converter<Long, String> longToStringConverter = new Converter<Long, String>() {
            public String convert(MappingContext<Long, String> context) {
                return context.getSource() != null ? context.getSource().toString() : null;
            }
        };

        Converter<String, Date> strToDateConverter = new Converter<String, Date>() {
            public Date convert(MappingContext<String, Date> context) {
                try {
                    return context.getSource() != null ? sdf.parse(context.getSource()) : null;
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Converter<Date, String> dateToStrConverter = new Converter<Date, String>() {
            public String convert(MappingContext<Date, String> context) {
                return context.getSource() != null ? sdf.format(context.getSource()) : null;
            }
        };

        //用于在Assign或as中自动映射 Ldt->Ld
        Converter<LocalDateTime, LocalDate> ldtToLdConverter = new Converter<>() {
            public LocalDate convert(MappingContext<LocalDateTime, LocalDate> context) {
                LocalDateTime ldt = context.getSource();
                if (ldt == null) {
                    return null;
                }
                return ldt.toLocalDate();
            }
        };

        //用于在Assign或as中自动映射 Ld->Ldt
        Converter<LocalDate, LocalDateTime> ldToLdtConverter = new Converter<>() {
            public LocalDateTime convert(MappingContext<LocalDate, LocalDateTime> context) {
                LocalDate ld = context.getSource();
                if (ld == null) {
                    return null;
                }
                return ld.atStartOfDay();
            }
        };

        //注册转换器
        mMapper.addConverter(ldtToLdConverter);
        mMapper.addConverter(ldToLdtConverter);

        mMapper.addConverter(toStringConverter);
        mMapper.addConverter(longToStringConverter);
        mMapper.addConverter(strToDateConverter);
        mMapper.addConverter(dateToStrConverter);
        mMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        mMapper.getConfiguration().setCollectionsMergeEnabled(false);
    }

    @Override
    public void assign(Object source, Object target) {
        mMapper.map(source, target);
    }

    @Override
    public void assign(Object source, Object target, Map<String, String> map) {

    }
}
