package com.ejt.money;

import com.ejt.util.CalendarUtil;
import org.apache.commons.lang3.StringUtils;

public class ProcessBarcMC extends ProcessTransFile {

    // 02/23/2024,"PANNEKOEKEN","DEBIT",-33.41
    // 02/18/2024,"Payment Received","CREDIT",22.74

    @Override
    protected void parseEntry(TransRecord transRecord, String parseLine) throws BadRecordException {
        int endIdx = parseLine.indexOf(",\""), startIdx = 0;
        if (endIdx < 0) {
            throw new BadRecordException("cannot find 1st token");
        }
        try {
            transRecord.setDate(CalendarUtil.parse(StringUtils.trimToNull(parseLine.substring(startIdx, endIdx)), "MM/dd/yyyy"));
        } catch (IllegalArgumentException iae) {
            throw new BadRecordException("expected 1st token to be date (MM/dd/yyyy)");
        }
        startIdx = endIdx + 2;

        endIdx = parseLine.indexOf("\"", startIdx);
        if (endIdx < 0) {
            throw new BadRecordException("cannot find 2nd token");
        }
        transRecord.setDescription(parseLine.substring(startIdx, endIdx));
        if (StringUtils.isBlank(transRecord.getDescription())) {
            throw new BadRecordException("expected 2nd token to be non-blank description");
        }

        endIdx = parseLine.lastIndexOf(",");
        if (endIdx < 0) {
            throw new BadRecordException("cannot find 4th token");
        }
        try {
            transRecord.setAmount(MintUtils.determineCents(parseLine.substring(endIdx + 1)));
        } catch (Exception e) {
            throw new BadRecordException("expected 4th token to be amount");
        }
        transRecord.setAccount(AccountEnum.BARC_MC);
        transRecord.setDebitNegativeAmt(true);
    }
}
