package com.ksptooi.psme.controller;

import com.ksptooi.psm.processor.EventHandler;
import com.ksptooi.psm.processor.ServiceUnit;
import com.ksptooi.psm.processor.event.ShellInputEvent;
import com.ksptooi.psm.processor.event.StatementCommitEvent;
import com.ksptooi.psm.vk.VK;

import java.util.LinkedList;
import java.util.List;

@ServiceUnit("nc::auto::history")
public class AutoHistoryCtl {

    private List<String> history = new LinkedList<>();
    private long cur = 0;

    @EventHandler(global = true)
    public void onStatementCommit(StatementCommitEvent event){
        //用户提交命令时记录历史
        history.add(event.getStatement());
        cur = history.size();
    }

    @EventHandler(global = true)
    public void onShellInput(ShellInputEvent e){

        //监听up键
        if(e.match(VK.UP)){

            if(history == null){
                return;
            }

            cur--;

            if(cur < 0){
                cur = 0;
            }
            if(cur >= history.size()){
                cur = history.size() - 1;
            }

            //查找上一条记录
            var previous = history.get((int) cur);

            //渲染回终端
            var vt = e.getUserShell().getVirtualTextArea();
            vt.setContent(previous);
            vt.render();
        }

        //监听down
        if(e.match(VK.DOWN)){

            if(history == null){
                return;
            }

            cur++;

            if(cur < 0){
                cur = 0;
            }
            if(cur >= history.size()){
                cur = history.size() - 1;
                //渲染回终端
                var vt = e.getUserShell().getVirtualTextArea();
                vt.setContent("");
                vt.render();
                return;
            }

            //查找下一条记录
            var previous = history.get((int) cur);

            //渲染回终端
            var vt = e.getUserShell().getVirtualTextArea();
            vt.setContent(previous);
            vt.render();
        }

    }

}
