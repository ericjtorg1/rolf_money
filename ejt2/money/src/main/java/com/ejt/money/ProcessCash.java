package com.ejt.money;

import com.ejt.util.CalendarUtil;
import com.ejt.util.Util;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ProcessCash extends ProcessTransFile {

    @Override
    protected void parseEntry(TransRecord transRecord, String parseLine) throws BadRecordException {
        List<String> tokens = Util.parseQuotedList(parseLine);
        if (tokens.size() != 3) {
            throw new BadRecordException("each line must have 3 tokens");
        }
        try {
            transRecord.setDate(CalendarUtil.parse(tokens.get(0), "MM/dd/yyyy"));
        } catch (IllegalArgumentException iae) {
            throw new BadRecordException("expected 1st token to be date (MM/dd/yyyy)");
        }
        try {
            transRecord.setAmount(MintUtils.determineCents(tokens.get(1)));
        } catch (Exception e) {
            throw new BadRecordException("expected 2nd token to be amount");
        }
        transRecord.setDescription(tokens.get(2));
        if (StringUtils.isBlank(transRecord.getDescription())) {
            throw new BadRecordException("expected 3rd token to be non-blank description");
        }
        transRecord.setAccount(AccountEnum.CASH);
        transRecord.setDebitNegativeAmt(false);
    }
}
