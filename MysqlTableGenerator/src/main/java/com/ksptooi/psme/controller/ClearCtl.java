package com.ksptooi.psme.controller;

import com.ksptooi.psm.processor.RequestHandler;
import com.ksptooi.psm.processor.ServiceUnit;
import com.ksptooi.psm.processor.ShellRequest;

@ServiceUnit("nc::auto::clear")
public class ClearCtl {

    @RequestHandler("clear")
    public void clear(ShellRequest sr){
        clearScreen(sr);
    }

    @RequestHandler("cls")
    public void cls(ShellRequest sr){
        clearScreen(sr);
    }

    private void clearScreen(ShellRequest sr){

        var lines = Integer.parseInt(sr.getShell().getEnv().getEnv().get("LINES"));

        for (int i = 0; i < lines * 2; i++) {
            sr.getCable().nextLine();
        }

    }


}
