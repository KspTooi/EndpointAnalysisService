package com.ksptooi.commons.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * 有限状态机 用于将一段字符串转换为表结构描述对象
 */
public class DtdParser {

    private CharReader rd = null;


    private static final int OUT_CLASS = 0;
    private static final int CLASS_NAME = 1;
    private static final int CLASS_META = 16;
    private static final int IN_CLASS = 2;
    private static final int FIELD_TYPE = 3;
    private static final int FIELD_NAME = 4;
    private static final int FIELD_VALUE = 10;
    private static final int FIELD_BLOCK_COMMENT = 100;
    private static final int FIELD_LINE_COMMENT = 101;
    private static final int CLASS_BLOCK_COMMENT = 102;
    private static final int CLASS_LINE_COMMENT = 103;

    private static final int CLASS_ANNOTATION = 104;
    private static final int FIELD_ANNOTATION = 105;
    private static final int ANNOTATION_VALUE = 106;

    private static final int IN_KIND = 104;

    private int state = OUT_CLASS;
    private int prevState = -1;

    private int commentPolicy = -1; //注释解析策略 0:注释位于字段前 1:注释位于字段后 -1:自动侦测

    private String className = "";
    private final StringBuilder fieldName = new StringBuilder();
    private final StringBuilder fieldValue = new StringBuilder();
    private final StringBuilder fieldType = new StringBuilder();
    private final StringBuilder fieldComment = new StringBuilder();
    private final StringBuilder fieldKind = new StringBuilder();
    private final List<String> fieldKinds = new ArrayList<>();

    private final StringBuilder annoName = new StringBuilder();
    private final StringBuilder annoValue = new StringBuilder();
    private final List<FieldAnnotation> fieldAnno = new ArrayList<>();

    private final ClassDescribe clazz = new ClassDescribe();
    private final List<ClassField> fields = new ArrayList<>();

    public DtdParser(File f){
        try {
            this.rd = new CharReader(Files.readString(f.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public DtdParser(String rawText){
        this.rd = new CharReader(rawText);
    }

    public ClassDescribe parse(){

        rd.addTextBlockSeparator(' ');
        rd.addTextBlockSeparator(';');
        rd.addTextBlockSeparator('\r');
        rd.addTextBlockSeparator('\n');

        while (rd.hasNext()){

            rd.next();

            stateForAnnotation();
            stateForComment();

            //有static时回退状态
            if(state == FIELD_TYPE && rd.eq("static")){
                state = IN_CLASS;
                clearBuf();
            }

            //处理状态更改
            if(state == OUT_CLASS){
                if(rd.eq("class")){
                    className = rd.nxt().nextBlock();
                    state = CLASS_META;
                }
            }
            if(state == CLASS_META){
                if(rd.eq('{')){
                    state = IN_CLASS;
                }
            }
            if(state == IN_CLASS){
                if(rd.eq("private")){
                    rd.skipNextBlock(); //跳转到下一个文本块
                    state = FIELD_TYPE;
                    continue;
                }
            }
            if(state == FIELD_TYPE){
                if(rd.eq(' ')){
                    state = FIELD_NAME;
                }
            }
            if(state == FIELD_NAME){
                if(rd.eq('=')){
                    state = FIELD_VALUE;
                    continue;
                }
                if(rd.eq(';')){  //对象没有声明值
                    state = IN_CLASS;
                    newField();
                    clearBuf();
                }
            }
            if(state == FIELD_VALUE){
                if(rd.eq(';')){  //对象有声明值
                    state = IN_CLASS;
                    newField();
                    clearBuf();
                }
            }
            readProperty();
        }

        clazz.setClassName(className.trim());
        clazz.setFields(fields);

        for(var f : fields){

            f.setName(f.getName().trim());

            if(f.getValue() != null){
                f.setValue(f.getValue().trim());
            }

            if(f.getType() != null){
                f.setType(f.getType().trim());
            }

            if(f.getComment() != null){
                f.setComment(f.getComment().trim());
            }

            for(var a : f.getAnnotations()){
                if(a.getName() != null){
                    a.setName(a.getName().trim());
                }
            }

        }

        return clazz;
    }


    private void readProperty(){

        if(state == CLASS_NAME && !rd.isSpace()){
            className = className + rd.getCur();
        }
        if(state == FIELD_TYPE && !rd.isSpace()){
            fieldType.append(rd.getCur());
        }
        if(state == FIELD_NAME && !rd.isSpace()){
            fieldName.append(rd.getCur());
        }
        if(state == FIELD_VALUE && !rd.isSpace()){
            fieldValue.append(rd.getCur());
        }
        if(state == FIELD_LINE_COMMENT){
            fieldComment.append(rd.getCur());
        }
        if(state == IN_KIND){
            fieldKind.append(rd.getCur());
        }
        if(state == FIELD_ANNOTATION && !rd.isSpace()){
            annoName.append(rd.getCur());
        }
        if(state == ANNOTATION_VALUE){
            annoValue.append(rd.getCur());
        }
    }

    private void stateForAnnotation(){

        //在注解中进入值
        if(state == FIELD_ANNOTATION && rd.eq('(')){
            rd.next();
            state = ANNOTATION_VALUE;
        }

        //在注解中退出值
        if(state == ANNOTATION_VALUE && rd.eq(')')){
            rd.next();
            state = FIELD_ANNOTATION;
        }

        //在类中进入注解
        if(rd.eq('@') && state == IN_CLASS){
            rd.next();
            state = FIELD_ANNOTATION;
        }

        //在类中退出注解
        if(state == FIELD_ANNOTATION && (rd.eq('@') || rd.isSpace() || rd.isLineBreak() || rd.isSeparator())){

            var anno = new FieldAnnotation(annoName.toString());

            if(!annoValue.isEmpty()){
                anno.setDefaultVal(annoValue.toString().replace("\"",""));
            }

            fieldAnno.add(anno);
            annoName.setLength(0);
            annoValue.setLength(0);

            state = IN_CLASS;

            if(rd.eq('@')){
                rd.next();
                state = FIELD_ANNOTATION;
            }

        }

    }

    private void stateForComment(){

        //在注释中进入Kind
        if(state == CLASS_LINE_COMMENT || state == CLASS_BLOCK_COMMENT || state == FIELD_LINE_COMMENT || state == FIELD_BLOCK_COMMENT){
            if(rd.eq('$')){
                rd.next();
                prevState = state;
                state = IN_KIND;
            }
        }
        //在注释中退出Kind
        if(state == IN_KIND && (rd.eq('$') || rd.isSpace() || rd.isLineBreak() || rd.isSeparator())){
            state = prevState;
            if(!fieldKind.isEmpty()){
                fieldKinds.add(fieldKind.toString());
                fieldKind.setLength(0);
            }
            if(rd.eq('$')){
                rd.next();
                state = IN_KIND;
            }
        }

        //进入行注释
        if(rd.eq("//")){
            rd.next();
            if(state == OUT_CLASS){
                state = CLASS_LINE_COMMENT;
            }
            if(state == IN_CLASS){
                state = FIELD_LINE_COMMENT;
            }
        }

        //进入块注释
        if(rd.eq("/*") || rd.eq("/**")){
            rd.next();
            if(state == OUT_CLASS){
                state = CLASS_BLOCK_COMMENT;
            }
            if(state == IN_CLASS){
                state = FIELD_BLOCK_COMMENT;
            }
        }

        //退出行注释
        if(rd.isLineBreak() && (state == CLASS_LINE_COMMENT || state == FIELD_LINE_COMMENT)){
            if(state == CLASS_LINE_COMMENT){
                state = OUT_CLASS;
            }
            if(state == FIELD_LINE_COMMENT){
                state = IN_CLASS;
                appendCommentToPrevField();
            }
        }

        //退出块注释
        if(rd.eq("*/") && (state == CLASS_BLOCK_COMMENT || state == FIELD_BLOCK_COMMENT)){
            if(state == CLASS_BLOCK_COMMENT){
                state = OUT_CLASS;
            }
            if(state == FIELD_BLOCK_COMMENT){
                state = IN_CLASS;
            }
        }
    }

    public void newField(){
        var field = new ClassField();
        field.setName(fieldName.toString());
        field.setValue(fieldValue.toString());
        field.setType(fieldType.toString());

        //注释前置时直接加入注释
        if(commentPolicy == 0){
            field.setComment(fieldComment.toString());

            //如果有kind需要附加kind
            if(!fieldKinds.isEmpty()){
                field.setKinds(new ArrayList<>(fieldKinds));
                field.setKind(fieldKinds.get(0));
                fieldKind.setLength(0);
                fieldKinds.clear();
            }
        }

        //有注解时加入注解
        if(!fieldAnno.isEmpty()){
            field.getAnnotations().addAll(fieldAnno);
            field.setAnnotation(fieldAnno.get(0));
            fieldAnno.clear();
        }

        //自动侦测字段注释位置
        if(commentPolicy == -1) {
            if(fieldComment.isEmpty()){
                //注释后置
                commentPolicy = 1;
            }else {
                //注释前置
                commentPolicy = 0;
            }
        }

        fields.add(field);
        clearBuf();
    }

    //注释后置时注入注释到上一个字段
    public void appendCommentToPrevField(){
        if(commentPolicy == 1 && !fieldComment.isEmpty()){
            var field = fields.get(fields.size() - 1);
            field.setComment(fieldComment.toString());

            //如果有kind需要附加kind
            if(!fieldKinds.isEmpty()){
                field.setKinds(new ArrayList<>(fieldKinds));
                field.setKind(fieldKinds.get(0));
                fieldKind.setLength(0);
                fieldKinds.clear();
            }
        }
    }

    private void clearBuf(){
        //className = "";
        fieldName.setLength(0);
        fieldType.setLength(0);
        fieldComment.setLength(0);
    }


    public void setCommentPolicy(int commentPolicy) {
        this.commentPolicy = commentPolicy;
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
