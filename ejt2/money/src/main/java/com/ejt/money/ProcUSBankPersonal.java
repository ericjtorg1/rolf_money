package com.ejt.money;

import com.ejt.util.CalendarUtil;
import com.ejt.util.Util;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ProcUSBankPersonal extends ProcessTransFile {

    // "2024-06-10","CREDIT","MOBILE CHECK DEPOSIT","Download from usbank.com.","2175.00"
    // "2024-06-11","DEBIT","MOBILE BANKING TRANSFER WITHDRAWAL 3348","Download from usbank.com.","-5000.00"

    @Override
    protected void parseEntry(TransRecord transRecord, String parseLine) throws BadRecordException {
        List<String> tokens = Util.parseQuotedList(parseLine);
        if (tokens.size() != 5) {
            throw new BadRecordException("each line must have 5 tokens");
        }
        try {
            transRecord.setDate(CalendarUtil.parse(tokens.get(0), "yyyy-MM-dd"));
        } catch (IllegalArgumentException iae) {
            throw new BadRecordException("expected 1st token to be date (yyyy-MM-dd)");
        }
        transRecord.setDescription(StringUtils.trimToNull(tokens.get(2)));
        if (StringUtils.isBlank(transRecord.getDescription())) {
            throw new BadRecordException("expected 3rd token to be non-blank description");
        }
        try {
            transRecord.setAmount(MintUtils.determineCents(tokens.get(4)));
        } catch (Exception e) {
            throw new BadRecordException("expected 5th token to be amount");
        }

        transRecord.setAccount(getAccount());
        transRecord.setDebitNegativeAmt(true);
    }

    public AccountEnum getAccount() {
        return AccountEnum.USB_PERS;
    }
}
