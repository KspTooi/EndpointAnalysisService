import assembly.MysqlToTypeScriptPolyConverter;
import com.ksptool.assembly.blueprint.collector.MysqlCollector;
import com.ksptool.assembly.blueprint.collector.VelocityBlueprintCollector;
import com.ksptool.assembly.blueprint.converter.PolyConverter;
import com.ksptool.assembly.blueprint.core.AssemblyFactory;
import com.ksptool.assembly.blueprint.projector.Projector;
import com.ksptool.assembly.blueprint.projector.VelocityProjector;

import java.util.List;

public class CodeGeneratorVue {

    public static void main(String[] args) {

        var tables = List.of("qt_task_group");

        //创建 MySQL 采集器
        MysqlCollector coll = new MysqlCollector();
        coll.setUrl("jdbc:mysql://192.168.10.202:3306/endpoint_analysis_service_test");
        coll.setUsername("root");
        coll.setPassword("root");
        coll.setDatabase("endpoint_analysis_service_test");

        //创建蓝图采集器 & 投影仪
        VelocityBlueprintCollector blueprintCollector = new VelocityBlueprintCollector();
        Projector projector = new VelocityProjector();

        //使用自定义的TypeScript聚合转换器转换Mysql类型到TS类型
        PolyConverter converter = new MysqlToTypeScriptPolyConverter();

        //组装Assembly工厂
        AssemblyFactory factory = new AssemblyFactory();
        factory.setCollector(coll);
        factory.setCollector(blueprintCollector);
        factory.setConverter(converter);
        factory.setProjector(projector);

        //置路径

        //输入：指向 blueprint_vue 蓝图目录
        factory.setInputBasePathFromRelative("./blueprint_vue");

        //输出：直接生成到项目前端源码目录
        factory.setOutputBasePathFromRelative("./src/main/resources/views/webconsole/");

        //选择表 & 设置前缀
        factory.selectTables(tables.toArray(new String[0]));
        factory.removeTablePrefixes("tb_", "core_", "sys_", "pd_");
        factory.setOverwriteEnabled(true);

        //工厂执行
        factory.execute();
    }
}