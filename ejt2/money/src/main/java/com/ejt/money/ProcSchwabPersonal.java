package com.ejt.money;

import com.ejt.util.CalendarUtil;
import com.ejt.util.Util;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ProcSchwabPersonal extends ProcessTransFile {

    // "04/30/2024","Posted","INTADJUST","","Interest Paid","","$0.35","$118.60"
    // "04/30/2024","Posted","ACH","","Electronic Withdrawal","$800.00","","$118.25"

    @Override
    protected void parseEntry(TransRecord transRecord, String parseLine) throws BadRecordException {
        List<String> tokens = Util.parseQuotedList(parseLine);
        if (tokens.size() != 8) {
            throw new BadRecordException("each line must have 8 tokens");
        }
        try {
            transRecord.setDate(CalendarUtil.parse(tokens.get(0), "MM/dd/yyyy"));
        } catch (IllegalArgumentException iae) {
            throw new BadRecordException("expected 1st token to be date (MM/dd/yyyy)");
        }
        transRecord.setDescription(StringUtils.trimToNull(tokens.get(4)));
        if (StringUtils.isBlank(transRecord.getDescription())) {
            throw new BadRecordException("expected 5th token to be non-blank description");
        }

        String debit = StringUtils.trimToNull(tokens.get(5));
        String credit = StringUtils.trimToNull(tokens.get(6));

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
        transRecord.setAccount(getAccount());
        transRecord.setDebitNegativeAmt(true);
    }

    public AccountEnum getAccount() {
        return AccountEnum.SWB_PERS;
    }
}
