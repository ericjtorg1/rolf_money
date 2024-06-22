package com.ejt.money;

import com.ejt.util.CalendarUtil;
import org.apache.commons.lang3.StringUtils;

public class ProcessAmex extends ProcessTransFile {

    @Override
    protected void parseEntry(TransRecord transRecord, String parseLine) throws BadRecordException {
        String[] tokens = StringUtils.split(parseLine, ',');
        if (tokens.length != 3) {
            throw new BadRecordException("each line must have 3 tokens");
        }
        try {
            transRecord.setDate(CalendarUtil.parse(tokens[0], "MM/dd/yyyy"));
        } catch (IllegalArgumentException iae) {
            throw new BadRecordException("expected 1st token to be date (MM/dd/yyyy)");
        }
        try {
            transRecord.setAmount(MintUtils.determineCents(tokens[2]));
        } catch (Exception e) {
            throw new BadRecordException("expected 3rd token to be amount");
        }
        transRecord.setDescription(StringUtils.trimToNull(tokens[1]));
        if (StringUtils.isBlank(transRecord.getDescription())) {
            throw new BadRecordException("expected 2nd token to be non-blank description");
        }
        transRecord.setAccount(AccountEnum.AMEX);
        transRecord.setDebitNegativeAmt(false);
    }
}
