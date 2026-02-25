import com.ksptool.assembly.blueprint.collector.MysqlCollector;
import com.ksptool.assembly.blueprint.collector.VelocityBlueprintCollector;
import com.ksptool.assembly.blueprint.converter.JavaTypeInfo;
import com.ksptool.assembly.blueprint.converter.MysqlToJavaPolyConverter;
import com.ksptool.assembly.blueprint.converter.PolyConverter;
import com.ksptool.assembly.blueprint.core.AssemblyFactory;
import com.ksptool.assembly.blueprint.projector.Projector;
import com.ksptool.assembly.blueprint.projector.VelocityProjector;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CodeGenerator {


    public static void main(String[] args) {

        //数据库名称
        var databaseName = "endpoint_analysis_service";

        //需要生成的表名
        var tableNames = List.of("drive_space", "drive_space_member");


        //配置聚合转换器 映射Mysql的DATE类型为Java的LD和LDT
        MysqlToJavaPolyConverter.TYPE_MAP.put("DATE", JavaTypeInfo.of(LocalDate.class));
        MysqlToJavaPolyConverter.TYPE_MAP.put("DATETIME", JavaTypeInfo.of(LocalDateTime.class));

        //创建Mysql采集器
        MysqlCollector coll = new MysqlCollector();
        coll.setUrl("jdbc:mysql://127.0.0.1:3306/" + databaseName);
        coll.setUsername("root");
        coll.setPassword("root");
        coll.setDatabase(databaseName);

        //创建蓝图采集器、聚合转换器、投影仪
        VelocityBlueprintCollector blueprintCollector = new VelocityBlueprintCollector();
        PolyConverter converter = new MysqlToJavaPolyConverter();
        Projector projector = new VelocityProjector();

        //创建AssemblyFactory
        AssemblyFactory factory = new AssemblyFactory();
        factory.setCollector(coll);
        factory.setCollector(blueprintCollector);
        factory.setConverter(converter);
        factory.setProjector(projector);

        //测试：启用 projector.map.json 生成
        projector.enableProjectorMap(true);

        //设置输入和输出路径(使用相对路径)
        factory.setInputBasePathFromRelative("./src/main/resources/support-static/code-generator/blueprint_jpa");
        factory.setOutputBasePathFromRelative("./");

        //选择要收集的表
        factory.selectTables(tableNames.toArray(new String[0]));

        //需移除的表前缀 可多个
        factory.removeTablePrefixes("tb_", "core_", "sys_", "pd_");
        factory.setOverwriteEnabled(true);

        //执行
        factory.execute();
    }

}
