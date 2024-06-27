package com.ejt.money;

public class ProcUSBankVisa extends ProcUSBankPersonal {

    @Override
    public AccountEnum getAccount() {
        return AccountEnum.USB_VISA;
    }
}
