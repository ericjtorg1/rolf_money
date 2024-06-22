package com.ejt.money;

import com.ejt.util.CalendarUtil;
import org.apache.commons.lang3.StringUtils;

public class ProcessCitiMC extends ProcessTransFile {

    // Cleared,03/18/2024,"2PSC GOODYEAR GOODYEAR AZ",7.71,
    // Cleared,03/17/2024,"ONLINE PAYMENT, THANK YOU",,-5087.93

    @Override
    protected void parseEntry(TransRecord transRecord, String parseLine) throws BadRecordException {
        int endIdx = parseLine.indexOf(","), startIdx = 0;
        if (endIdx < 0) {
            throw new BadRecordException("cannot find 1st token");
        }
        String tok1 = StringUtils.trimToNull(parseLine.substring(startIdx, endIdx));
        if (!"Cleared".equals(tok1)) {
            throw new BadRecordException("invalid 1st token value, must be Cleared");
        }
        startIdx = endIdx + 1;

        endIdx = parseLine.indexOf(",\"", startIdx);
        if (endIdx < 0) {
            throw new BadRecordException("cannot find 2nd token");
        }
        try {
            transRecord.setDate(CalendarUtil.parse(StringUtils.trimToNull(parseLine.substring(startIdx, endIdx)), "MM/dd/yyyy"));
        } catch (IllegalArgumentException iae) {
            throw new BadRecordException("expected 2nd token to be date (MM/dd/yyyy)");
        }
        startIdx = endIdx + 2;

        String tok3 = null, tok4 = null;

        endIdx = parseLine.indexOf("\",,", startIdx);
        if (endIdx > 0) {
            tok3 = StringUtils.trimToNull(parseLine.substring(startIdx, endIdx));
            startIdx = endIdx + 3;
            tok4 = StringUtils.trimToNull(parseLine.substring(startIdx));

        } else {
            endIdx = parseLine.indexOf("\",", startIdx);
            if (endIdx > 0) {
                tok3 = StringUtils.trimToNull(parseLine.substring(startIdx, endIdx));
                startIdx = endIdx + 2;
                endIdx = parseLine.indexOf(",", startIdx);
                if (endIdx > 0) {
                    tok4 = StringUtils.trimToNull(parseLine.substring(startIdx, endIdx));
                }
            }
        }
        if (tok3 == null) {
            throw new BadRecordException("expected 3rd token to be non-blank description");
        }
        transRecord.setDescription(tok3);

        if (tok4 == null) {
            throw new BadRecordException("cannot find 4th token");
        }
        try {
            transRecord.setAmount(MintUtils.determineCents(tok4));
        } catch (Exception e) {
            throw new BadRecordException("expected 4th token to be amount");
        }
        transRecord.setAccount(AccountEnum.CITI_MC);
        transRecord.setDebitNegativeAmt(false);
    }
}
