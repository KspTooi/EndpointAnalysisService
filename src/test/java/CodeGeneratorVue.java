import assembly.MysqlToTypeScriptPolyConverter;
import com.ksptool.assembly.blueprint.collector.MysqlCollector;
import com.ksptool.assembly.blueprint.collector.VelocityBlueprintCollector;
import com.ksptool.assembly.blueprint.converter.PolyConverter;
import com.ksptool.assembly.blueprint.core.AssemblyFactory;
import com.ksptool.assembly.blueprint.projector.Projector;
import com.ksptool.assembly.blueprint.projector.VelocityProjector;

public class CodeGeneratorVue {

    public static void main(String[] args) {

        // 1. 创建 MySQL 采集器
        MysqlCollector coll = new MysqlCollector();
        coll.setUrl("jdbc:mysql://192.168.10.202:3306/endpoint_analysis_service_test");
        coll.setUsername("root");
        coll.setPassword("root");
        coll.setDatabase("endpoint_analysis_service_test");

        // 2. 创建蓝图采集器 & 投影仪
        VelocityBlueprintCollector blueprintCollector = new VelocityBlueprintCollector();
        Projector projector = new VelocityProjector();

        // 3. ★★★ 使用自定义的 TypeScript 转换器 ★★★
        PolyConverter converter = new MysqlToTypeScriptPolyConverter();

        // 4. 组装工厂
        AssemblyFactory factory = new AssemblyFactory();
        factory.setCollector(coll);
        factory.setCollector(blueprintCollector);
        factory.setConverter(converter);
        factory.setProjector(projector);

        // 5. 设置路径
        // 输入：指向 blueprint_vue 目录
        factory.setInputBasePathFromRelative("./blueprint_vue");
        // 输出：直接生成到项目前端源码目录
        factory.setOutputBasePathFromRelative("./src/main/resources/views/webconsole/");

        // 6. 选择表 & 设置前缀
        factory.selectTables("core_notice", "core_notice_rcd", "core_notice_template");
        factory.removeTablePrefixes("tb_", "core_", "sys_", "pd_");
        factory.setOverwriteEnabled(true);

        // 7. 执行
        factory.execute();
    }
}