package com.ejt.money;

public class ProcSchwabGrandview extends ProcSchwabPersonal {

    @Override
    public AccountEnum getAccount() {
        return AccountEnum.SWB_GV;
    }
}
