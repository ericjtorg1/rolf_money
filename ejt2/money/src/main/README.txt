# Download amex.txt wf-visa.txt etc.

# Parse account files and create 2024_trans.list
mint_proctransfiles.sh 2024 E

# run and fix any issues
mint_report.sh 2024

mint_find.sh 2024 E Medical Q1
mint_find.sh 2024 I Investments_Payout Q4



