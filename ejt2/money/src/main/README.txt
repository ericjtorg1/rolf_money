
Download transactions.csv

# list out all duplicates
mint_convtrans.sh 20XX dups

# process duplicates

# create 20XX_trans.list
mint_convtrans.sh 20XX

# run and fix any issues
mint_report.sh 20XX

#
# NEW WAY 2024 and beyond
#
Download amex.txt wf-visa.txt etc.

# create 20XX_trans.list
mint_proctransfiles.sh 20XX

# run and fix any issues
mint_report.sh 20XX

mint_find.sh 20XX E Medical Q1
mint_find.sh 20XX I "Investments Payout" Q4



