package com.ejt.money;

public class ProcBankAmerSavings extends ProcBankAmerCheck {

    @Override
    public AccountEnum getAccount() {
        return AccountEnum.BA_SAV;
    }
}
