package com.ejt.money;

import com.ejt.util.Util;

import java.util.Set;

public enum AccountEnum {
    AMEX("Delta Amex", "amex.txt", new ProcessAmex(), "E"),
    CASH("Cash", "cash.txt", new ProcessCash(), "E,R"),
    BA_CHK("Bank Amer Checking", "ba-checking.txt", new ProcBankAmerCheck(), "E"),
    BA_SAV("Bank Amer Savings", "ba-savings.txt", new ProcBankAmerSavings(), "E"),
    BARC_MC("Barclays MC", "barclays-mc.txt", new ProcessBarcMC(), "E"),
    CHAS_VISA("Chase VISA", "chase-visa.txt", new ProcChaseVisa(), "E"),
    CITI_MC("Citi MC", "citi-mc.txt", new ProcessCitiMC(), "E"),
    WF_CHK("Wells Fargo Checking", "wf-checking.txt", new ProcWellsFargoCheck(), "E"),
    WF_VISA("Wells Fargo VISA", "wf-visa.txt", new ProcWellsFargoVisa(), "E"),
    // Rolf Accounts
    SWB_PERS("Schwab Personal", "swb-pers-chk.txt", new ProcSchwabPersonal(), "R"),
    SWB_WCFT("Schwab Westcraft", "swb-wcft-chk.txt", new ProcSchwabWestcraft(), "R"),
    SWB_GV("Schwab Grandview", "swb-gv-chk.txt", new ProcSchwabGrandview(), "R"),
    USB_PERS("USBank Personal", "usb-pers-chk.txt", new ProcUSBankPersonal(), "R"),
    USB_WCFT("USBank Westcraft", "usb-wcft-chk.txt", new ProcUSBankWestcraft(), "R"),
    USB_VISA("USBank VISA", "usb-visa.txt", new ProcUSBankVisa(), "R"),
    SPRK_VISA("Spark VISA", "spark-visa.txt", new ProcSparkVisa(), "R");

    private String tag, file;
    private ProcessTransFile procFile;
    private Set<String> codes;

    AccountEnum(String tag, String file, ProcessTransFile procFile, String codes) {
        this.tag = tag;
        this.file = file;
        this.procFile = procFile;
        this.codes = Util.convertToStringSet(codes);
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

    public Set<String> getCodes() {
        return codes;
    }

    public static AccountEnum determineAccount(String name) throws Exception {
        try {
            return AccountEnum.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new Exception("Invalid Account name: " + name);
        }
    }
}
