package com.ksptooi.commons.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.comments.Comment;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FieldParser {

    public static void main(String[] args) {
        try {


            // 读取Java文件
            FileInputStream in = new FileInputStream("E:\\InternalDev\\NanliProjects\\industrysecureplatform\\MysqlTableGenerator\\src\\main\\java\\com\\ksptooi\\commons\\dbtd\\EmergencyPlan.java");

            // 创建JavaParser实例
            JavaParser parser = new JavaParser();

            // 解析文件
            CompilationUnit cu = parser.parse(in).getResult().orElseThrow(() -> new RuntimeException("Failed to parse"));

            // 获取类声明
            List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);
            for (ClassOrInterfaceDeclaration clazz : classes) {
                System.out.println("Class: " + clazz.getName());

                // 获取字段
                List<FieldDeclaration> fields = clazz.getFields();
                for (FieldDeclaration field : fields) {

                    // 获取注释
                    Comment comment = field.getComment().orElse(null);

                    var cmt = "";

                    if (comment != null) {
                        cmt = comment.getContent();
                        //System.out.println("Comment: " + comment.getContent());
                    } else {
                        System.out.println("No comment");
                    }

                    for(var item : field.getVariables()){
                        System.out.println("Field: " + item.getName() + "Comment: " + cmt);
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
