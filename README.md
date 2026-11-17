# LAB 2 – MÃ HÓA ĐỐI XỨNG HIỆN ĐẠI
**Học phần:** An toàn và bảo mật thông tin

---

## 1. THÔNG TIN SINH VIÊN
- **Họ và tên:** Huỳnh Vũ Đăng Khoa
- **Mã số sinh viên (MSSV):** 1150070020
- **Tên repository:** `LAB2_1150070020_HuynhVuDangKhoa`

---

## 2. MÔ TẢ BÀI THỰC HÀNH
Bài thực hành nghiên cứu, cài đặt và đánh giá các thuật toán mã hóa đối xứng hiện đại cùng cơ chế quản lý phiên và nhật ký hoạt động trên hệ quản trị cơ sở dữ liệu Microsoft SQL Server:
1. **Bài 2.1 – DES (Data Encryption Standard):** Mã khối 64-bit, khóa hiệu dụng 56-bit, chế độ CBC + IV ngẫu nhiên 8 byte, đệm PKCS5. Bản mã đóng gói Base64 kèm IV ở đầu.
2. **Bài 2.2 – TripleDES (3DES / DESede):** Chạy DES 3 lần (Mã - Giải - Mã), khóa 24 byte (192 bit gồm bit chẵn lẻ), chế độ CBC + IV ngẫu nhiên 8 byte.
3. **Bài 2.3 – AES-256-GCM (Galois/Counter Mode) & Xác thực người dùng:**
   - Mã hóa xác thực (AEAD): vừa mã hóa vừa phát hiện sửa đổi bản mã qua thẻ tag 128-bit.
   - Dẫn xuất khóa từ mật khẩu bằng thuật toán PBKDF2-HMAC-SHA256 với salt ngẫu nhiên 16 byte và 120.000 vòng lặp.
   - Đăng ký và đăng nhập tài khoản lưu trữ an toàn trong bảng `dbo.app_users` (không lưu mật khẩu gốc, chỉ lưu salt và hash PBKDF2). Cấp Ticket phiên 128-bit HEX.
4. **Bài 2.4 – RC4 (Rivest Cipher 4):** Mật mã dòng (stream cipher) với các thuật toán KSA (Key-Scheduling Algorithm) và PRGA (Pseudo-Random Generation Algorithm), xuất bản mã dưới dạng chuỗi HEX.
5. **Bài 2.5 – Twofish:** Ứng viên chung kết AES (sử dụng thư viện Bouncy Castle), chế độ CBC, đệm PKCS7, khóa dẫn xuất bằng SHA-256(Key), hỗ trợ lưu/mở tệp gồm IV và bản mã.
6. **Bài 2.6 (Nâng cao 01) – AES-256-CTR (Counter Mode):** Mã hóa khối biến thành mã dòng, không cần đệm, độ dài byte bản mã bằng độ dài byte bản rõ; hỗ trợ xử lý văn bản lớn (> 1000 ký tự).
7. **Bài nâng cao 02 – AES-256-CBC bằng Python:** Ứng dụng giao diện Tkinter và thư viện `cryptography`, đệm PKCS7, tách riêng phần xử lý để hỗ trợ cờ tự kiểm thử `--self-test`.
8. **Cơ chế lưu nhật ký (History):** Tự động ghi nhận lịch sử các thao tác `REGISTER`, `LOGIN`, `ENCRYPT`, `DECRYPT` vào bảng `dbo.crypto_history` trên SQL Server.

---

## 3. YÊU CẦU PHẦN MỀM & MÔI TRƯỜNG
- **Hệ điều hành:** Windows 10/11 (64-bit).
- **Java Development Kit (JDK):** JDK 17 hoặc 21 (đã kiểm thử thành công trên OpenJDK 17.0.10).
- **Apache Maven:** Phiên bản 3.9+ (đã tích hợp và kiểm thử trên Maven 3.9.9).
- **Apache NetBeans:** Phiên bản 20 trở lên (hoặc các IDE Java hỗ trợ Maven).
- **Hệ quản trị CSDL:** Microsoft SQL Server Express (2019/2022/2025) và SQL Server Management Studio (SSMS).
- **Python:** Python 3.10 trở lên kèm thư viện `cryptography` (đã kiểm thử trên Python 3.14.6).

---

## 4. CẤU HÌNH CƠ SỞ DỮ LIỆU SQL SERVER
Ứng dụng sử dụng Microsoft SQL Server Express kết nối qua giao thức TCP/IP cổng **14333**.

### Bước 1: Khởi tạo CSDL và bảng dữ liệu
1. Mở **SQL Server Management Studio (SSMS)**, kết nối vào server `localhost\SQLEXPRESS` bằng quyền **Windows Authentication**.
2. Mở tệp `sql/01_Tao_CSDL.sql` và bấm **Execute (F5)** để tạo CSDL `LAB2_MaHoaHienDai` cùng 2 bảng `dbo.app_users` và `dbo.crypto_history`.

### Bước 2: Tạo tài khoản SQL Server cho bài Lab
1. Mở tệp `sql/02_Tao_Tai_Khoan_SQL.sql`.
2. Thay thế placeholder `<MAT_KHAU_LAB>` bằng mật khẩu lab do giảng viên quy định (ví dụ: `'LabUser@2026'`).
3. Bấm **Execute (F5)** để tạo Login `lab2_user`, User và gán các quyền đọc/ghi (`db_datareader`, `db_datawriter`).

### Bước 3: Cấu hình cổng TCP/IP 14333
1. Mở **SQL Server Configuration Manager** (chạy bằng quyền Administrator).
2. Vào **SQL Server Network Configuration** → **Protocols for SQLEXPRESS**.
3. Nhấp đúp vào **TCP/IP** → tab **Protocol** → đặt **Enabled = Yes**.
4. Chuyển sang tab **IP Addresses**, kéo xuống mục **IPAll**:
   - Xóa trống ô **TCP Dynamic Ports**.
   - Nhập `14333` vào ô **TCP Port**.
5. Nhấn **OK**, sau đó vào mục **SQL Server Services** → nhấp phải vào **SQL Server (SQLEXPRESS)** → chọn **Restart**.

### Bước 4: Kiểm tra dữ liệu
Chạy tệp `sql/03_Kiem_Tra_Du_Lieu.sql` trong SSMS để xem các dòng người dùng và lịch sử mã hóa/giải mã được ghi nhận.

---

## 5. HƯỚNG DẪN BUILD VÀ CHẠY ỨNG DỤNG JAVA

### Cách 1: Chạy bằng NetBeans IDE
1. Mở Apache NetBeans → chọn **File** → **Open Project...** → điều hướng đến thư mục `LAB2_MaHoaHienDai`.
2. Nhấp phải vào tên project → chọn **Clean and Build** (`Shift + F11`).
3. Bấm **Run Project** (`F6`) để khởi chạy giao diện Menu chính (`week_02.frm_Menu`).
4. Tại Menu chính, chọn từng bài để mở form tương ứng hoặc mở form **Kết nối SQL Server** để thiết lập kết nối CSDL.

### Cách 2: Chạy bằng dòng lệnh Maven
Mở Command Prompt / PowerShell tại thư mục `LAB2_MaHoaHienDai`:
```powershell
# Biên dịch và chạy kiểm thử tự động
mvn clean test

# Đóng gói tệp JAR
mvn clean package

# Chạy ứng dụng giao diện Menu chính
mvn exec:java
```

---

## 6. HƯỚNG DẪN CHẠY BÀI NÂNG CAO PYTHON

Thư mục: `LAB2_Python/`

### Cài đặt thư viện:
```powershell
py -m pip install cryptography
```

### Chạy chế độ tự kiểm thử (Self-Test):
```powershell
cd LAB2_Python
py aes_cbc_gui.py --self-test
```
*Kết quả chuẩn*: Xuất hiện dòng thông báo:
```text
SELF-TEST OK: 4 thông điệp mã hóa/giải mã đúng, sai khóa bị phát hiện.
```

### Chạy ứng dụng giao diện Tkinter:
```powershell
py aes_cbc_gui.py
```
- Bấm **Generate Key** để sinh khóa ngẫu nhiên 256-bit (32 byte Base64).
- Bấm **Generate IV** để sinh IV ngẫu nhiên 16 byte Base64.
- Nhập Plaintext → bấm **Encrypt** để mã hóa AES-CBC.
- Bấm **Save File** / **Open File** để lưu/mở tệp `aes_cbc_ciphertext.txt` (chỉ lưu IV và Ciphertext, không lưu khóa).

---

## 7. TÓM TẮT KẾT QUẢ KIỂM THỬ

Chi tiết kết quả có trong tài liệu: [docs/KET_QUA_KIEM_THU.md](docs/KET_QUA_KIEM_THU.md).

| Hạng mục kiểm thử | Đối tượng / Vector kiểm thử | Kết quả mong đợi | Kết quả thực tế |
| :--- | :--- | :--- | :---: |
| **DES** | Plaintext tiếng Việt, Key 8 byte | Decrypt(Encrypt(x)) = x, IV đổi ngẫu nhiên, khóa sai báo lỗi | **Đạt** |
| **TripleDES** | Plaintext tiếng Việt, Key 24 byte | Giải mã đúng, khóa 23 ký tự ném ngoại lệ | **Đạt** |
| **AES-GCM** | Plaintext tiếng Việt, Key PBKDF2 | Giải mã đúng, sửa 1 ký tự bản mã thì ném lỗi BadTag | **Đạt** |
| **RC4** | Plaintext: `"Plaintext"`, Key: `"Key"` | Bản mã HEX: `BBF316E8D940AF0AD3` | **Đạt** |
| **Twofish** | Key SHA-256, IV 16 byte HEX | Giải mã đúng, sai IV giải mã sai, toFile/fromFile đúng | **Đạt** |
| **AES-CTR** | Key 256-bit, Nonce 96-bit | Byte ciphertext = Byte plaintext, giải mã đúng văn bản > 1000 ký tự | **Đạt** |
| **AES-CBC Python** | Chạy `py aes_cbc_gui.py --self-test` | Vượt qua 4 test case, đệm PKCS7 và phát hiện khóa sai | **Đạt** |
| **SQL Server** | Cấu trúc CSDL và bảng dữ liệu | Tạo thành công CSDL và 2 bảng `app_users`, `crypto_history` | **Đạt** |

---

## 8. DANH SÁCH ẢNH MINH CHỨNG
Các ảnh chụp màn hình minh chứng kết quả cần lưu trong thư mục `images/` (xem chi tiết tại [images/README.md](images/README.md)):
1. `01_frm_Menu.png`: Giao diện Menu chính mở tất cả các bài.
2. `02_frm_DES_Encrypt.png`: Giao diện DES sau khi mã hóa.
3. `03_frm_DES_WrongKey.png`: Thông báo lỗi khóa sai độ dài hoặc sai khóa giải mã DES.
4. `04_frm_TripleDES.png`: Giao diện TripleDES sau khi mã hóa.
5. `05_frm_AES_Login_Encrypt.png`: Giao diện AES sau khi đăng nhập thành công (hiện Ticket HEX) và mã hóa.
6. `06_frm_RC4_BBF3.png`: Giao diện RC4 với chuỗi HEX kiểm thử chuẩn `BBF316E8D940AF0AD3`.
7. `07_frm_Twofish.png`: Giao diện Twofish sau khi sinh IV và mã hóa.
8. `08_frm_AES_CTR.png`: Giao diện AES-CTR sau khi sinh Key, Nonce và mã hóa.
9. `09_frm_LichSu.png`: Giao diện bảng lịch sử thao tác từ SQL Server.
10. `10_ssms_result.png`: Kết quả truy vấn bảng lịch sử và người dùng trên SSMS.
11. `11_python_gui.png`: Giao diện ứng dụng Tkinter Python AES-CBC.
12. `12_python_selftest.png`: Kết quả chạy self-test thành công trên Command Prompt.

---

## 9. LƯU Ý BẢO MẬT
- **Không đưa mật khẩu lên Git:** Mật khẩu trong mã nguồn CSDL đã được thay bằng placeholder `<MAT_KHAU_LAB>`. Mật khẩu kết nối CSDL và khóa mã hóa chỉ lưu tạm trên bộ nhớ RAM (dưới dạng `char[]`) khi ứng dụng đang chạy.
- **Không lưu khóa kèm tệp bản mã:** Các định dạng tệp lưu trữ (`twofish_ciphertext.txt`, `aes_ctr_ciphertext.txt`, `aes_cbc_ciphertext.txt`) chỉ lưu IV hoặc Nonce cùng chuỗi Ciphertext, tuyệt đối không lưu khóa bí mật vào cùng tệp bản mã.
- **Thuật toán lỗi thời:** Các thuật toán DES, 3DES, RC4 chỉ được triển khai cho mục đích học tập; các ứng dụng hiện đại thực tế bắt buộc sử dụng AES (AES-GCM / AES-CTR) hoặc ChaCha20-Poly1305.

---

## 10. HƯỚNG DẪN ĐẨY LÊN GITHUB
Sau khi kiểm tra toàn bộ project và bổ sung các ảnh chụp màn hình, sinh viên thực hiện các lệnh sau để đẩy mã nguồn lên GitHub:
```powershell
# 1. Khởi tạo Git repository (nếu chưa khởi tạo)
git init

# 2. Thêm toàn bộ tệp vào staging
git add .

# 3. Tạo commit đầu tiên
git commit -m "feat: hoan thanh toan bo bai thuc hanh LAB 2 - Ma hoa doi xung hien dai"

# 4. Đặt nhánh chính là main
git branch -M main

# 5. Liên kết repository từ xa (thay thế URL bằng URL GitHub của bạn)
git remote add origin https://github.com/<USERNAME>/LAB2_1150070020_HuynhVuDangKhoa.git

# 6. Đẩy mã nguồn lên GitHub
git push -u origin main
```
