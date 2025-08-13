package com.ksptooi.dict;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextResolverFSM {

    private static final int FSM_KEY = 1; //解析key
    private static final int FSM_VALUE = 2; //解析value

    private int state = 1;

    private final String target;

    public TextResolverFSM(String target){
        this.target = target;
    }

    public List<DictElement> parse(){

        List<DictElement> list = new ArrayList<>();

        char[] c = target.toCharArray();

        StringBuilder key = new StringBuilder();
        StringBuilder val = new StringBuilder();

        for (int i = 0; i < c.length; i++) {

            char cur = c[i];

            //状态机状态转换
            if(cur == ' '){
                state = FSM_VALUE;
            }
            if(cur == '\r' || cur == '\n'){
                state = FSM_KEY;

                //排除掉无关字符对状态机的影响
                if(StringUtils.isBlank(val.toString())){
                    key.setLength(0);
                    val.setLength(0);
                    continue;
                }

                var el = new DictElement();
                el.setKey(key.toString().trim());
                el.setValue(val.toString().trim());
                list.add(el);
                key.setLength(0);
                val.setLength(0);
                continue;
            }

            if(state == FSM_KEY){
                key.append(cur);
            }

            if(state == FSM_VALUE){
                val.append(cur);
            }

        }

        return list;
    }

    public static void main(String[] args) throws IOException {

        TextResolverFSM fsm = new TextResolverFSM(Files.readString(Paths.get("C:\\input.txt")));

        List<?> parse = fsm.parse();

        System.out.println(parse);
    }

}
