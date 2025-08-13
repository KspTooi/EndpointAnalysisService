package com.ksptooi.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class DirectoryTools {


    public static String getCurrentPath(){
        return System.getProperty("user.dir");
    }

    public static File getCurrentDirectory(){
        return new File(System.getProperty("user.dir"));
    }

    /**
     * 遍历找到模板文件夹
     */
    public static File findTemplate(String path,String templateDirName){

        List<File> freemarkerDirs = new ArrayList<>();
        Path startPath = Paths.get(path);

        try {
            Files.walkFileTree(startPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (dir.getFileName().toString().equals(templateDirName)) {
                        freemarkerDirs.add(dir.toFile());
                        return FileVisitResult.SKIP_SUBTREE; // Skip processing this subtree
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the IOException
        }

        if(freemarkerDirs.isEmpty()){
            return null;
        }

        return freemarkerDirs.get(0);
    }



    /**
     * 递归向上查找父级路径下是否存在Maven项目
     */
    public static File findMavenProject(File path,String projectName){

        if(path == null){
            return null;
        }

        //当前路径是否有项目
        File f = new File(path,projectName);

        if(f.exists() && isMavenProject(f.getAbsolutePath())){
            return f;
        }

        //递归查找上级
        return findMavenProject(path.getParentFile(),projectName);
    }

    public static boolean isMavenProject(String path){
        boolean hasSrc = Files.exists(Paths.get(path + "\\src"));
        boolean hasMain = Files.exists(Paths.get(path + "\\src\\main"));
        boolean hasJava = Files.exists(Paths.get(path + "\\src\\main\\java"));
        return hasSrc && hasMain && hasJava;
    }

}
