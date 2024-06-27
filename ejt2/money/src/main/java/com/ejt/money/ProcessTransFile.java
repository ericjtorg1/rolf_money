package com.ejt.money;

import com.ejt.util.Util;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class ProcessTransFile {

    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ProcessTransFile.class);

    // Meta Transaction Info
    // trans-data CAT=xyz  CMT=xyz  TAG=XYZ  USE-FROM  NOT-DEBIT

    protected static String[] META_TAGS = new String[]{"CAT=", "CMT=", "TAG=", "USE-FROM", "NOT-DEBIT"};

    public List<Transaction> process(String transFile, List<TransRule> transRules) {
        List<Transaction> transactions = new ArrayList<>();

        String currentLine = null;
        List<String> lines = Util.readFile(transFile);
        int lineNum = 0;
        try {
            for (String line : lines) {
                lineNum++;

                if (StringUtils.isBlank(line)) {
                    continue;
                }
                currentLine = line;
                TransRecord transRecord = parseLine(line);

                Transaction trans = processRecord(transRecord, transRules);
                transactions.add(trans);
            }
        } catch (BadRecordException bre) {
            System.out.println("Line " + lineNum + ": " + bre.getMessage());
            System.out.println(currentLine);
            System.exit(1);
        }
        return transactions;
    }

    protected Transaction processRecord(TransRecord transRecord, List<TransRule> transRules) throws BadRecordException {
        TransRule match = null;
        Transaction trans = new Transaction();

        if (transRecord.isUseFrom()) {
            trans.setDescription(transRecord.getDescription());

        } else {
            rule_loop:
            for (TransRule rule : transRules) {
                logger.debug("Have trans.rule." + rule.getId());
                if (rule.getAccount() != null && rule.getAccount() != transRecord.getAccount()) {
                    continue;
                }
                if (rule.isDebit() && transRecord.isDebitNegativeAmt() && !transRecord.isAmtNegative()) {
                    continue;
                }
                if (rule.isDebit() && !transRecord.isDebitNegativeAmt() && transRecord.isAmtNegative()) {
                    continue;
                }
                if (rule.isCredit() && transRecord.isDebitNegativeAmt() && transRecord.isAmtNegative()) {
                    continue;
                }
                if (rule.isCredit() && !transRecord.isDebitNegativeAmt() && !transRecord.isAmtNegative()) {
                    continue;
                }
                logger.debug("Trying trans.rule." + rule.getId());
                for (String token : rule.getMatches()) {
                    int ruleIdx = StringUtils.indexOf(transRecord.getDescription(), token);
                    if (ruleIdx >= 0) {
                        match = rule;
                        break rule_loop;
                    }
                }
            }
            if (match == null) {
                throw new BadRecordException("no rule match found");
            }
            logger.debug("MATCHED trans.rule." + match.getId());
        }

        trans.setDate(transRecord.getDate());
        trans.setAmount(transRecord.getAmount());

        if (transRecord.getAccount() == null) {
            throw new BadRecordException("no account found");
        }
        trans.setAccount(transRecord.getAccount().getTag());

        if (match != null && match.getLabel() != null) {
            trans.setDescription(match.getLabel());
        }
        if (trans.getDescription() == null) {
            throw new BadRecordException("no description determined");
        }

        trans.setNotes(transRecord.getComment());

        if (transRecord.getCategory() != null) {
            trans.setCategory(transRecord.getCategory());

        } else if (match != null && match.getCategory() != null) {
            trans.setCategory(match.getCategory());
        }
        if (trans.getCategory() == null) {
            throw new BadRecordException("no category determined");
        }

        if ("Transfer".equals(trans.getCategory()) || "Cash".equals(trans.getCategory())) {
            trans.setType(Transaction.Type.TRANSFER);
        }
        if (trans.getType() == null && transRecord.getNotDebit() != null) {
            trans.setType(transRecord.getNotDebit().booleanValue() ? Transaction.Type.INCOME : Transaction.Type.EXPENSE);
        }
        if (trans.getType() == null && match != null) {
            trans.setType(match.isDebit() ? Transaction.Type.EXPENSE : Transaction.Type.INCOME);
        }
        if (trans.getType() == null) {
            if (transRecord.isAmtNegative() && transRecord.isDebitNegativeAmt()) {
                trans.setType(Transaction.Type.EXPENSE);

            } else if (!transRecord.isAmtNegative() && transRecord.isDebitNegativeAmt()) {
                trans.setType(Transaction.Type.INCOME);

            } else if (transRecord.isAmtNegative() && !transRecord.isDebitNegativeAmt()) {
                trans.setType(Transaction.Type.INCOME);

            } else if (!transRecord.isAmtNegative() && !transRecord.isDebitNegativeAmt()) {
                trans.setType(Transaction.Type.EXPENSE);
            }
        }
        if (trans.getType() == null) {
            throw new BadRecordException("no trans-type determined");
        }

        // Amount<0 means debit Amount>0 means credit
        if (transRecord.isDebitNegativeAmt()) {
            if (trans.getType() == Transaction.Type.EXPENSE && !transRecord.isAmtNegative()) {
                throw new BadRecordException("mismatch on amount signage (credit) and trans-type " + trans.getType());
            }
            if (trans.getType() == Transaction.Type.INCOME && transRecord.isAmtNegative()) {
                throw new BadRecordException("mismatch on amount signage (debit) and trans-type " + trans.getType());
            }

            // Amount<0 means credit Amount>0 means debit
        } else {
            if (trans.getType() == Transaction.Type.EXPENSE && transRecord.isAmtNegative()) {
                throw new BadRecordException("mismatch on amount signage (credit) and trans-type " + trans.getType());
            }
            if (trans.getType() == Transaction.Type.INCOME && !transRecord.isAmtNegative()) {
                throw new BadRecordException("mismatch on amount signage (debit) and trans-type " + trans.getType());
            }
        }

        if (match != null && match.isNeedsComment() && trans.getNotes() == null) {
            throw new BadRecordException("some comment required");
        }

        if (transRecord.getTags() != null) {
            trans.addTags(transRecord.getTags());

        } else if (match != null && match.getTags() != null) {
            trans.addTags(match.getTags());
        }

        if (match != null && match.isNeedsTag() && trans.getTags().

                isEmpty()) {
            throw new BadRecordException("some tag required");
        }
        if (match != null && match.isNeedsFrom() && !transRecord.isUseFrom()) {
            throw new BadRecordException("must enter From field and add USE-FROM");
        }

        return trans;
    }


    protected TransRecord parseLine(String line) throws BadRecordException {
        TransRecord transRecord = new TransRecord();
        try {
            Integer metaIdx = null;
            List<String> metaTokens = new ArrayList<>();

            int newIdx, oldIdx = -1;

            for (int m = 0; m < META_TAGS.length; m++) {
                newIdx = StringUtils.indexOf(line, META_TAGS[m]);
                if (newIdx > 0) {
                    if (metaIdx == null) {
                        metaIdx = newIdx;
                    }
                    if (oldIdx > 0) {
                        metaTokens.add(line.substring(oldIdx, newIdx).trim());
                    }
                    oldIdx = newIdx;
                }
            }
            if (oldIdx > 0) {
                metaTokens.add(line.substring(oldIdx).trim());
            }

            String parseLine = line;
            if (metaIdx != null) {
                parseLine = line.substring(0, metaIdx);
            }

            for (String metaToken : metaTokens) {
                // CAT
                if (metaToken.startsWith(META_TAGS[0])) {
                    transRecord.setCategory(metaToken.substring(META_TAGS[0].length()));
                    if (StringUtils.isBlank(transRecord.getCategory())) {
                        throw new BadRecordException("invalid meta-data " + metaToken);
                    }
                    // CMT
                } else if (metaToken.startsWith(META_TAGS[1])) {
                    transRecord.setComment(metaToken.substring(META_TAGS[1].length()));
                    if (StringUtils.isBlank(transRecord.getComment())) {
                        throw new BadRecordException("invalid meta-data " + metaToken);
                    }
                    // TAG
                } else if (metaToken.startsWith(META_TAGS[2])) {
                    transRecord.setTags(metaToken.substring(META_TAGS[2].length()));
                    if (StringUtils.isBlank(transRecord.getTags())) {
                        throw new BadRecordException("invalid meta-data " + metaToken);
                    }
                    // USE-FROM
                } else if (metaToken.equals(META_TAGS[3])) {
                    transRecord.setUseFrom(true);

                    // NOT-DEBIT
                } else if (metaToken.equals(META_TAGS[4])) {
                    transRecord.setNotDebit(Boolean.TRUE);

                } else {
                    throw new BadRecordException("invalid meta-data " + metaToken);
                }
            }
            parseEntry(transRecord, StringUtils.trimToNull(parseLine));

        } catch (IllegalArgumentException iae) {
            throw new BadRecordException("failed to parse line into tokens");
        }
        return transRecord;
    }

    protected abstract void parseEntry(TransRecord transRecord, String parseLine) throws BadRecordException;


}
