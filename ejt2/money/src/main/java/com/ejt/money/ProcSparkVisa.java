package com.ejt.money;

import com.ejt.util.CalendarUtil;
import org.apache.commons.lang3.StringUtils;

public class ProcSparkVisa extends ProcessTransFile {

    // 2024-06-09,2024-06-10,3715,CAPITAL ONE AUTOPAY PYMT,Payment/Credit,,2214.13
    // 2024-06-07,2024-06-10,3715,MENARDS EDEN PRAIRIE MN,Merchandise,135.60,

    @Override
    protected void parseEntry(TransRecord transRecord, String parseLine) throws BadRecordException {
        String[] tokens = StringUtils.splitByWholeSeparatorPreserveAllTokens(parseLine, ",");
        if (tokens.length != 7) {
            throw new BadRecordException("each line must have 7 tokens");
        }
        try {
            transRecord.setDate(CalendarUtil.parse(tokens[0], "yyyy-MM-dd"));
        } catch (IllegalArgumentException iae) {
            throw new BadRecordException("expected 1st token to be date (yyyy-MM-dd)");
        }
        transRecord.setDescription(StringUtils.trimToNull(tokens[3]));
        if (StringUtils.isBlank(transRecord.getDescription())) {
            throw new BadRecordException("expected 4rd token to be non-blank description");
        }

        String debit = StringUtils.trimToNull(tokens[5]);
        String credit = StringUtils.trimToNull(tokens[6]);

        if (debit != null) {
            try {
                transRecord.setAmount(-MintUtils.determineCents(debit));
            } catch (Exception e) {
                throw new BadRecordException("expected 6th token to be debit amount");
            }
        } else if (credit != null) {
            try {
                transRecord.setAmount(MintUtils.determineCents(credit));
            } catch (Exception e) {
                throw new BadRecordException("expected 7th token to be credit amount");
            }
        } else {
            throw new BadRecordException("expected either 6th or 7th token to be amount");
        }
        transRecord.setAccount(AccountEnum.SPRK_VISA);
        transRecord.setDebitNegativeAmt(true);
    }
}
