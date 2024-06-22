package com.ejt.money;

public enum AccountEnum {
    AMEX("Delta Amex", "amex.txt", new ProcessAmex()),
    CASH("Cash", "cash.txt", new ProcessCash()),
    BA_CHK("Bank Amer Checking", "ba-checking.txt", new ProcBankAmerCheck()),
    BA_SAV("Bank Amer Savings", "ba-savings.txt", new ProcBankAmerSavings()),
    BARC_MC("Barclays MC", "barclays-mc.txt", new ProcessBarcMC()),
    CHAS_VISA("Chase VISA", "chase-visa.txt", new ProcChaseVisa()),
    CITI_MC("Citi MC", "citi-mc.txt", new ProcessCitiMC()),
    WF_CHK("Wells Fargo Checking", "wf-checking.txt", new ProcWellsFargoCheck()),
    WF_VISA("Wells Fargo VISA", "wf-visa.txt", new ProcWellsFargoVisa());

    private String tag, file;
    private ProcessTransFile procFile;

    AccountEnum(String tag, String file, ProcessTransFile procFile) {
        this.tag = tag;
        this.file = file;
        this.procFile = procFile;
    }

    public String getTag() {
        return tag;
    }

    public String getFile() {
        return file;
    }

    public ProcessTransFile getProcFile() {
        return procFile;
    }

    public static AccountEnum determineAccount(String name) throws Exception {
        try {
            return AccountEnum.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new Exception("Invalid Account name: " + name);
        }
    }
}
