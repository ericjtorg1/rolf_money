package com.ejt.money;

public class ProcSchwabWestcraft extends ProcSchwabPersonal {

    @Override
    public AccountEnum getAccount() {
        return AccountEnum.SWB_WCFT;
    }
}
