from jinja2 import Environment, FileSystemLoader
from weasyprint import HTML
import os
from datetime import date

logo_file = os.path.abspath("assets/fv_logo.png")  # ví dụ logo nằm trong assets/

# Dữ liệu báo cáo
data = {
  "hospital_group": "Sở Y tế TP. Hồ Chí Minh",
  "hospital_name": "Bệnh viện Đại học Y Dược",
  "patient_name": "NGUYỄN VĂN A",
  "dob": "12/03/1975",
  "age": 49,
  "gender": "Nam",
  "nation": "Kinh",
  "address_number": "123",
  "address_ward": "Phường 5",
  "address_district": "Quận 10",
  "address_city": "TP. Hồ Chí Minh",
  "insurance_id": "GD4010120123456",
  "cccd_id": "079123456789",
  "in_date": "10/06/2025",
  "out_date": "15/06/2025",
  "diagnoses": "Tăng huyết áp (I10)",
  "diagnoses_out": "Tăng huyết áp ổn định (I10)",
  "dianogses": "Chóng mặt, mệt mỏi kéo dài",
  "medical_hisotry": "Tiểu đường type 2, điều trị hơn 5 năm",
  "diagnoses_result": "Xét nghiệm đường huyết cao, huyết áp 160/100 mmHg",
  "surgegy_methods": "Không phẫu thuật",
  "doctor_signature": "TS. BS. LÊ MINH TÂM",
  "doctor_fullName": "LÊ MINH TÂM"
}

# Render HTML
env = Environment(loader=FileSystemLoader('.'))
template = env.get_template("healthcare_function/app/generate_protocol/template.html")
html_out = template.render(**data)

# Xuất PDF
output_dir = "output"
output_path = os.path.join(output_dir, "hospital_report.pdf")
os.makedirs(output_dir, exist_ok=True)
HTML(string=html_out).write_pdf(output_path)

print(f"PDF đã tạo tại: {output_path}")