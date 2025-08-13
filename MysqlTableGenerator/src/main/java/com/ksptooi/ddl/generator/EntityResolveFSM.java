package com.ksptooi.ddl.generator;


import com.ksptooi.ddl.model.TableDescription;
import com.ksptooi.ddl.model.TableDescriptionField;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * 有限状态机 用于将一段字符串转换为表结构描述对象
 */
public class EntityResolveFSM {

    private static final int FSM_OUT_CLASS = 0;   //初始化
    private static final int FSM_CLASS_NAME = 1; //解析类名
    private static final int FSM_IN_CLASS = 2; //进入类内部
    private static final int FSM_IN_CMT_LEN = 18; //进入注释转义-长度
    private static final int FSM_COMMENT = 3;  //注释
    private static final int FSM_TYPE = 4;  //数据类型
    private static final int FSM_MEMBER_NAME = 5;  //成员变量名
    private static final int FSM_IDLE = 6;  //惰性状态

    private final String target;

    //表注释解析策略 0:注释位于字段前 1:注释位于字段后
    private int commentPolicy = 0;

    /**
     * 状态机当前状态
     */
    private int state = FSM_OUT_CLASS;

    public EntityResolveFSM(String target){
        this.target = target;
    }

    public TableDescription parse(){

        TableDescription table = new TableDescription();
        table.setTableName(null);
        List<TableDescriptionField> fields = new ArrayList<>();
        table.setFields(fields);

        String className = "";
        StringBuilder fieldType = new StringBuilder();
        StringBuilder fieldLen = new StringBuilder();
        StringBuilder fieldName = new StringBuilder();
        StringBuilder fieldComment = new StringBuilder();
        StringBuilder prevTxtBlock = new StringBuilder();

        char[] t = target.toCharArray();

        int symComment = 0;

        for (int i = 0; i < t.length; i++) {

            if(t[i] == '$'){
                if(state == FSM_COMMENT){
                    state = EntityResolveFSM.FSM_IN_CMT_LEN;
                    continue;
                }
            }

            if(!isNumber(t[i])){
                if(state ==FSM_IN_CMT_LEN){
                    state = FSM_COMMENT;
                }
            }

            if(t[i] == ';'){

                if(state == FSM_MEMBER_NAME && commentPolicy == 1){
                    state = FSM_IN_CLASS;
                    //字段读取完成
                    TableDescriptionField f = new TableDescriptionField();
                    f.setName(fieldName.toString());
                    f.setType(fieldType.toString());
                    f.setComment(fieldComment.toString().trim());

                    if(!fieldLen.isEmpty()){
                        f.setLength(Integer.parseInt(fieldLen.toString()));
                    }

                    fieldName.setLength(0);
                    fieldType.setLength(0);
                    fieldComment.setLength(0);
                    fieldLen.setLength(0);
                    fields.add(f);
                }

                if(state == FSM_MEMBER_NAME && commentPolicy == 0){
                    state = FSM_IN_CLASS;
                    //字段读取完成
                    TableDescriptionField f = new TableDescriptionField();
                    f.setName(fieldName.toString());
                    f.setType(fieldType.toString());
                    f.setComment(fieldComment.toString().trim());

                    if(!fieldLen.isEmpty()){
                        f.setLength(Integer.parseInt(fieldLen.toString()));
                    }

                    fieldName.setLength(0);
                    fieldType.setLength(0);
                    fieldComment.setLength(0);
                    fieldLen.setLength(0);
                    fields.add(f);
                }
            }

            if(t[i] == '\r' || t[i] == '\n'){

                if(state == FSM_COMMENT && commentPolicy == 1){
                    //注释读取完成 附加注释
                    if(!fields.isEmpty()){
                        var field = fields.get(fields.size() - 1);
                        field.setComment(fieldComment.toString().trim());

                        if(!fieldLen.isEmpty()){
                            field.setLength(Integer.parseInt(fieldLen.toString()));
                        }
                        fieldLen.setLength(0);
                        fieldComment.setLength(0);
                    }
                    state = FSM_IN_CLASS;
                    continue;
                }

                if(state == FSM_COMMENT && commentPolicy == 0){
                    state = FSM_IN_CLASS;
                    prevTxtBlock.setLength(0);
                    continue;
                }
            }

            if(t[i] == ' '){

                //状态机在注释中 应对空格进行转义
                if(state == FSM_COMMENT){
                    fieldComment.append(t[i]);
                    continue;
                }

                if(state == FSM_OUT_CLASS){
                    if(prevTxtBlock.toString().equals("class")){
                        state = FSM_CLASS_NAME;
                        prevTxtBlock.setLength(0);
                        continue;
                    }
                }

                if(state == FSM_IN_CLASS){
                    if(prevTxtBlock.toString().equals("private")){
                        state = FSM_TYPE;
                        prevTxtBlock.setLength(0);
                        continue;
                    }
                }

                if(state == FSM_CLASS_NAME){
                    className = prevTxtBlock.toString();
                    state = FSM_IDLE;
                }

                if(state == FSM_TYPE){

                    //解析到static符号 需要回退状态
                    if(prevTxtBlock.toString().equals("static")){
                        state = FSM_IN_CLASS;
                        prevTxtBlock.setLength(0);
                        fieldName.setLength(0);
                        fieldType.setLength(0);
                        fieldComment.setLength(0);
                        continue;
                    }

                    state = FSM_MEMBER_NAME;
                }

                prevTxtBlock.setLength(0);
                continue;
            }

            if(state == FSM_IN_CMT_LEN){
                fieldLen.append(t[i]);
            }

            if(state == FSM_COMMENT){
                fieldComment.append(t[i]);
            }

            if(state == FSM_TYPE){
                fieldType.append(t[i]);
            }

            if(state == FSM_MEMBER_NAME){
                fieldName.append(t[i]);
            }

            if(t[i] == '/' && state == FSM_IN_CLASS){
                symComment++;
            }
            if(symComment >= 2){
                state = FSM_COMMENT;
                symComment = 0;
            }

            if(t[i] == '{'){

                if(state != FSM_IDLE && state != FSM_CLASS_NAME){
                    throw new RuntimeException("状态机解析出错 位于:" + i + "\r\n" + getStack(t,i) + "\r\n\r\n");
                }

                if(state == FSM_CLASS_NAME){
                    className = prevTxtBlock.toString();
                }

                state = FSM_IN_CLASS;
                prevTxtBlock.setLength(0);
                continue;
            }

            prevTxtBlock.append(t[i]);
        }

        table.setTableName(className);
        return table;
    }



    public void setCommentPolicy(int commentPolicy) {
        this.commentPolicy = commentPolicy;
    }


    public static void main(String[] args) throws IOException {

        File f = new File("C:\\InternalDev\\NanliProject\\industrysecureplatform\\baodian-back\\src\\main\\java\\com\\baodian\\back\\models\\dbtd\\ThreeRealTime.java");
        String read = Files.readString(f.toPath());

        EntityResolveFSM fsm = new EntityResolveFSM(read);
        fsm.setCommentPolicy(1);
        var parse = fsm.parse();
        System.out.println(parse);
    }



    public String getStack(char[] c,int i){

        StringBuilder b = new StringBuilder();

        for (int j = 0; j < i + 1; j++) {
            b.append(c[j]);
        }

        return b.toString();
    }


    //用于判断字符是否为阿拉伯数字
    public static boolean isNumber(char ch) {
        return ch >= '0' && ch <= '9';
    }
}
