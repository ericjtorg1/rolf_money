package com.ejt.money;

import com.ejt.util.CalendarUtil;
import org.apache.commons.lang3.StringUtils;

public class ProcBankAmerCheck extends ProcessTransFile {

    @Override
    protected void parseEntry(TransRecord transRecord, String parseLine) throws BadRecordException {

        //           T                                                           T3       T2
        // 03/04/2024  WF HOME MTG DES:AUTO PAY ID:XXXXX84045 ID:XXXXX87461 PPD   -2,533.05        640.07
        // 03/08/2024  EDWARD JONES DES:SEC PPD ID:18726TXXXXX7461 ID:XXXXX45811 PPD    1,500.00      2,140.07

        int target = 1, idx2 = -1, idx3 = -1;
        int startIdx = parseLine.length() - 1;

        for (int i = startIdx; i >= 0; i--) {
            String ch = parseLine.substring(i, i + 1);

            if (target == 1) {
                if (StringUtils.isBlank(ch)) {
                    target = 2;
                }
            } else if (target == 2) {
                if (StringUtils.isNotBlank(ch)) {
                    idx2 = i + 1;
                    target = 3;
                }
            } else if (target == 3) {
                if (StringUtils.isBlank(ch)) {
                    idx3 = i + 1;
                    break;
                }
            }
        }

        if (idx2 < 0 || idx3 < 0) {
            throw new BadRecordException("failed to find 2 tokens at end of line");
        }

        target = -1;
        for (int i = 0; i < startIdx; i++) {
            String ch = parseLine.substring(i, i + 1);

            if (StringUtils.isBlank(ch)) {
                target = i;
                break;
            }
        }
        if (target < 0) {
            throw new BadRecordException("failed to find token at start of line");
        }
        try {
            transRecord.setDate(CalendarUtil.parse(parseLine.substring(0, target), "MM/dd/yyyy"));
        } catch (IllegalArgumentException iae) {
            throw new BadRecordException("expected 1st token to be date (MM/dd/yyyy)");
        }

        try {
            transRecord.setAmount(MintUtils.determineCents(parseLine.substring(idx3, idx2)));
        } catch (Exception e) {
            throw new BadRecordException("expected 3rd token to be amount");
        }

        transRecord.setDescription(StringUtils.trimToNull(parseLine.substring(target, idx3 - 1)));
        if (StringUtils.isBlank(transRecord.getDescription())) {
            throw new BadRecordException("expected 2nd token to be non-blank description");
        }
        transRecord.setAccount(getAccount());
        transRecord.setDebitNegativeAmt(true);
    }

    public AccountEnum getAccount() {
        return AccountEnum.BA_CHK;
    }
}
