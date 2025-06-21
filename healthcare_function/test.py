import easyocr
reader = easyocr.Reader(['vi'])
result = reader.readtext('/Users/happy091/Desktop/template.png')


# Kết quả: [(bbox, text, confidence)]
for box, text, conf in result:
    print(text)


def extract_field(results, keyword, max_words=5):
    for i, (_, text, _) in enumerate(results):
        if keyword.lower() in text.lower():
            # Ghép các từ sau đó lại (có thể lọc bằng bbox nếu muốn chính xác hơn)
            return ' '.join(results[j][1] for j in range(i+1, min(i+1+max_words, len(results))))
    return ""

data = {
    "bac_si": extract_field(result, "Bác sĩ:"),
    "ma_so_bac_si": extract_field(result, "Mã số bác sĩ:"),
    "khoa": extract_field(result, "Khoa:"),
    "thoi_gian": extract_field(result, "Thời gian làm việc:"),
    # ... bạn có thể viết rule riêng cho phần bảng (tìm dòng có "Huyết áp", "Số lượt khám", ...)
}

print(data)

