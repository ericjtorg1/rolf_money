package com.ejt.money;

import com.ejt.util.CalendarUtil;
import org.apache.commons.lang3.StringUtils;

public class ProcChaseVisa extends ProcessTransFile {

    // 02/12/2024,02/14/2024,CARIBOU MOBILE APP #9006,Food & Drink,Sale,-25.00,
    // 02/07/2024,02/07/2024,AUTOMATIC PAYMENT - THANK,,Payment,338.27,

    @Override
    protected void parseEntry(TransRecord transRecord, String parseLine) throws BadRecordException {
        String[] tokens = StringUtils.splitByWholeSeparatorPreserveAllTokens(parseLine, ",");
        if (tokens.length != 7) {
            throw new BadRecordException("each line must have 7 tokens");
        }
        try {
            transRecord.setDate(CalendarUtil.parse(tokens[0], "MM/dd/yyyy"));
        } catch (IllegalArgumentException iae) {
            throw new BadRecordException("expected 1st token to be date (MM/dd/yyyy)");
        }
        transRecord.setDescription(StringUtils.trimToNull(tokens[2]));
        if (StringUtils.isBlank(transRecord.getDescription())) {
            throw new BadRecordException("expected 3rd token to be non-blank description");
        }
        try {
            transRecord.setAmount(MintUtils.determineCents(tokens[5]));
        } catch (Exception e) {
            throw new BadRecordException("expected 6th token to be amount");
        }
        transRecord.setAccount(AccountEnum.CHAS_VISA);
        transRecord.setDebitNegativeAmt(true);
    }
}
