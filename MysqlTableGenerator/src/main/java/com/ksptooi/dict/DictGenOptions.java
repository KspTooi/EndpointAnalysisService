package com.ksptooi.dict;

import java.util.HashMap;
import java.util.Map;

public class DictGenOptions {

    private String absoluteFilePath;

    private String outputPath;

    private Map<String, Object> opt = new HashMap<>();

    private boolean silence;

    private String dictType;

    public String getAbsoluteFilePath() {
        return absoluteFilePath;
    }

    public void setInputFilePath(String absoluteFilePath) {
        this.absoluteFilePath = absoluteFilePath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public Map<String, Object> getOpt() {
        return opt;
    }

    public void setOpt(Map<String, Object> opt) {
        this.opt = opt;
    }

    public boolean isSilence() {
        return silence;
    }

    public void setSilence(boolean silence) {
        this.silence = silence;
    }

    public void setAbsoluteFilePath(String absoluteFilePath) {
        this.absoluteFilePath = absoluteFilePath;
    }

    public String getDictType() {
        return dictType;
    }

    public void setDictType(String dictType) {
        this.dictType = dictType;
    }
}
