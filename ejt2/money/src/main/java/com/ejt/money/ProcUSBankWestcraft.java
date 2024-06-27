package com.ejt.money;

public class ProcUSBankWestcraft extends ProcUSBankPersonal {

    @Override
    public AccountEnum getAccount() {
        return AccountEnum.USB_WCFT;
    }
}
