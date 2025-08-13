package com.ksptooi.ddl.model;

import com.ksptooi.commons.JavaToMysqlTypeMapper;
import com.ksptooi.commons.TypeMapper;
import com.ksptooi.utils.DatabaseTools;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DDLGenOptions {

    private Connection connection;

    private DatabaseTools databaseTools;

    private final Map<String, Object> opt = new HashMap<>();

    private final TypeMapper typeMapper = new JavaToMysqlTypeMapper();

    //表描述信息
    private List<TableDescription> tableDescriptions = new ArrayList<>();

    //表名
    private String tableName;

    //项目绝对路径
    private String absoluteProjectDir;

    //项目文件夹
    private String projectDir;

    //指定解析的实体
    private List<String> specifyEntities = new ArrayList<>();

    //指定主键的字段
    private List<String> primaryFields = new ArrayList<>();

    //实体类文件夹
    private String entitiesDir;

    //实体类文件夹绝对路径
    private String absoluteEntitiesDir;

    //是否自动创建?
    private Boolean autoExecute;

    //表前缀
    private String prefix;

    //表后缀
    private String suffix;

    //从表名中排除某个字符串
    private List<String> excludeTableName = new ArrayList<>();

    //当表已存在时是否自动替换
    private Boolean autoUpdate;

    //自动替换策略 0:不论如何都会删除重建 1:当表有数据时不进行动作
    private Integer autoUpdatePolicy;

    //表注释解析策略 0:注释位于字段前 1:注释位于字段后
    private Integer entityCommentResolverPolicy = 0;

    private String templatePath; //模板文件路径

    private String templateNameSpace;  //模板文件的命名空间

    private String absoluteTemplateFile; //模板文件绝对路径

    private Boolean silence = false; //为true 不打印输出文件日志

    public List<TableDescription> getTableDescriptions() {
        return tableDescriptions;
    }

    public void setTableDescriptions(List<TableDescription> tableDescriptions) {
        this.tableDescriptions = tableDescriptions;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getProjectDir() {
        return projectDir;
    }

    public void setProjectDir(String projectDir) {
        this.projectDir = projectDir;
    }

    public List<String> getSpecifyEntities() {
        return specifyEntities;
    }

    public void setSpecifyEntities(List<String> specifyEntities) {
        this.specifyEntities = specifyEntities;
    }

    public List<String> getPrimaryFields() {
        return primaryFields;
    }

    public void setPrimaryFields(List<String> primaryFields) {
        this.primaryFields = primaryFields;
    }

    public String getEntitiesDir() {
        return entitiesDir;
    }

    public void setEntitiesDir(String entitiesDir) {
        this.entitiesDir = entitiesDir;
    }

    public Boolean getAutoExecute() {
        return autoExecute;
    }

    public void setAutoExecute(Boolean autoExecute) {
        this.autoExecute = autoExecute;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Boolean getAutoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(Boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public Integer getAutoUpdatePolicy() {
        return autoUpdatePolicy;
    }

    public void setAutoUpdatePolicy(Integer autoUpdatePolicy) {
        this.autoUpdatePolicy = autoUpdatePolicy;
    }

    public Integer getEntityCommentResolverPolicy() {
        return entityCommentResolverPolicy;
    }

    public void setEntityCommentResolverPolicy(Integer entityCommentResolverPolicy) {
        this.entityCommentResolverPolicy = entityCommentResolverPolicy;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getTemplateNameSpace() {
        return templateNameSpace;
    }

    public void setTemplateNameSpace(String templateNameSpace) {
        this.templateNameSpace = templateNameSpace;
    }

    public Boolean getSilence() {
        return silence;
    }

    public void setSilence(Boolean silence) {
        this.silence = silence;
    }

    public String getAbsoluteProjectDir() {
        return absoluteProjectDir;
    }

    public void setAbsoluteProjectDir(String absoluteProjectDir) {
        this.absoluteProjectDir = absoluteProjectDir;
    }

    public String getAbsoluteEntitiesDir() {
        return absoluteEntitiesDir;
    }

    public void setAbsoluteEntitiesDir(String absoluteEntitiesDir) {
        this.absoluteEntitiesDir = absoluteEntitiesDir;
    }

    public String getAbsoluteTemplateFile() {
        return absoluteTemplateFile;
    }

    public void setAbsoluteTemplateFile(String absoluteTemplateFile) {
        this.absoluteTemplateFile = absoluteTemplateFile;
    }

    public TypeMapper getTypeMapper() {
        return typeMapper;
    }

    public Map<String, Object> getOpt() {
        return opt;
    }

    public List<String> getExcludeTableName() {
        return excludeTableName;
    }

    public void setExcludeTableName(List<String> excludeTableName) {
        this.excludeTableName = excludeTableName;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public DatabaseTools getDatabaseTools() {
        return databaseTools;
    }

    public void setDatabaseTools(DatabaseTools databaseTools) {
        this.databaseTools = databaseTools;
    }
}
