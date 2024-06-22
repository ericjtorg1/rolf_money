package com.ejt.money;

public class ProcWellsFargoCheck extends ProcWellsFargoVisa {

    @Override
    public AccountEnum getAccount() {
        return AccountEnum.WF_CHK;
    }
}
