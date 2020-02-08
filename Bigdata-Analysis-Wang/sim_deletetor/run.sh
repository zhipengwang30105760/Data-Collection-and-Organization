pdf_path='./pdf'
txt_path='./txt'

echo "======================= Start Converting PDF to TXT ============================="
python pdftotxt.py $pdf_path $txt_path
echo "======================= Done Converting ========================================="

echo "======================= Start Comparing documents similarity ===================="
# python3 findTxtSimilarity.py $txt_path
echo "======================= Done Comparing documents similarity ====================="
