# BÁO CÁO KẾT QUẢ KIỂM THỬ - LAB 2: MÃ HÓA ĐỐI XỨNG HIỆN ĐẠI

- **Sinh viên thực hiện**: Huỳnh Vũ Đăng Khoa
- **MSSV**: 1150070020
- **Môn học**: An toàn và bảo mật hệ thống thông tin
- **Thời gian thực hiện**: 17/11/2026
- **Môi trường**: Windows 11, OpenJDK 17.0.10, Apache Maven 3.9.9, Python 3.14.6, Microsoft SQL Server 2025 Express

---

## Bảng kết quả kiểm thử chi tiết

| Bài | Dữ liệu thử | Kết quả mong đợi | Kết quả thực tế | Trạng thái |
| :--- | :--- | :--- | :--- | :---: |
| **2.1 DES** (Mã hóa / Giải mã) | Plaintext: `"Xin chào Lab 2 - Mã hóa hiện đại!"`, Key: `"12345678"` | `Decrypt(Encrypt(x)) == x` | Giải mã trả về chính xác chuỗi gốc `"Xin chào Lab 2 - Mã hóa hiện đại!"` | **Đạt** |
| **2.1 DES** (IV ngẫu nhiên) | Plaintext: `"Xin chào Lab 2 - Mã hóa hiện đại!"`, Key: `"12345678"` (mã hóa 2 lần) | Hai lần mã hóa cho hai bản mã khác nhau (do sinh IV ngẫu nhiên 8 byte) | Bản mã lần 1 khác bản mã lần 2; cả hai đều giải mã ra đúng văn bản gốc | **Đạt** |
| **2.1 DES** (Khóa ngắn) | Plaintext: `"Xin chào Lab 2..."`, Key: `"1234"` | Ném ngoại lệ `IllegalArgumentException` yêu cầu đúng 8 byte | Báo lỗi: `"Khóa DES phải đúng 8 byte (gõ 8 ký tự không dấu). Hiện có 4 byte."` | **Đạt** |
| **2.1 DES** (Sai khóa giải mã) | Plaintext: `"Xin chào Lab 2..."`, Khóa mã: `"12345678"`, Khóa giải: `"87654321"` | Báo lỗi giải mã thất bại (lỗi đệm PKCS5) | Ném ngoại lệ `BadPaddingException` / `Exception` khi giải mã | **Đạt** |
| **2.2 TripleDES** (Mã hóa / Giải mã) | Plaintext: `"Xin chào Lab 2..."`, Key: `"123456789012345678901234"` (24 byte) | `Decrypt(Encrypt(x)) == x` | Giải mã trả về chính xác văn bản gốc | **Đạt** |
| **2.2 TripleDES** (Khóa 23 ký tự) | Plaintext: `"Xin chào Lab 2..."`, Key: `"12345678901234567890123"` (23 byte) | Ném ngoại lệ `IllegalArgumentException` | Báo lỗi: `"Khóa DESede phải đúng 24 byte (gõ 24 ký tự không dấu). Hiện có 23 byte."` | **Đạt** |
| **2.3 AES-GCM** (Mã hóa / Giải mã) | Plaintext: `"Xin chào Lab 2..."`, Key: `"MatKhauAES@2026"` (PBKDF2 120.000 vòng) | `Decrypt(Encrypt(x)) == x` | Giải mã trả về chính xác văn bản gốc | **Đạt** |
| **2.3 AES-GCM** (Chống giả mạo bản mã) | Sửa 1 ký tự giữa chuỗi Ciphertext Base64 | Giải mã thất bại (GCM xác thực tag không khớp) | Ném ngoại lệ `AEADBadTagException` (Tag mismatch) | **Đạt** |
| **2.4 RC4** (Vector kiểm thử chuẩn) | Plaintext: `"Plaintext"`, Key: `"Key"` | Bản mã HEX phải là `BBF316E8D940AF0AD3` | Kết quả HEX thu được đúng chuẩn `BBF316E8D940AF0AD3` | **Đạt** |
| **2.4 RC4** (Giải mã) | HexCiphertext: `"BBF316E8D940AF0AD3"`, Key: `"Key"` | Giải mã trả về `"Plaintext"` | Thu được chính xác chuỗi `"Plaintext"` | **Đạt** |
| **2.5 Twofish** (CBC + BouncyCastle) | Plaintext: `"Xin chào Lab 2..."`, Key: `"Twofish@Lab2"`, IV 16 byte HEX ngẫu nhiên | `Decrypt(Encrypt(x)) == x` | Giải mã trả về chính xác văn bản gốc | **Đạt** |
| **2.5 Twofish** (Sai IV) | Bản mã mã hóa với IV_A, giải mã với IV_B | Kết quả giải mã bị sai (khối đầu hỏng) hoặc ném lỗi | Chuỗi giải mã khác hoàn toàn văn bản gốc ban đầu | **Đạt** |
| **2.5 Twofish** (Tệp lưu trữ) | `toFile` ghi dòng 1: `IV=...`, dòng 2: bản mã; `fromFile` đọc lại | Khôi phục chính xác IV và bản mã tách biệt | Khôi phục đúng 100% IV và bản mã từ nội dung tệp | **Đạt** |
| **2.6 AES-CTR** (Mã dòng không đệm) | Plaintext: `"Xin chào Lab 2 - Mã hóa hiện đại!"`, Key 256-bit, Nonce 96-bit | Độ dài byte bản mã = độ dài byte bản rõ UTF-8 | Độ dài byte bản mã chính xác bằng độ dài byte bản rõ (38 bytes) | **Đạt** |
| **2.6 AES-CTR** (Văn bản lớn) | Văn bản tiếng Việt có dấu độ dài > 1000 ký tự | Giải mã đúng 100% toàn văn không mất mát dữ liệu | Giải mã thành công, khớp toàn bộ ký tự | **Đạt** |
| **2.6 AES-CTR** (Tệp lưu trữ) | `toFile` ghi dòng 1: `NONCE=...`, dòng 2: bản mã; `fromFile` đọc lại | Khôi phục chính xác Nonce và bản mã | Khôi phục đúng 100% Nonce và bản mã từ nội dung tệp | **Đạt** |
| **Nâng cao 02: AES-CBC Python** | Chạy kiểm thử: `py aes_cbc_gui.py --self-test` | `SELF-TEST OK`: 4 thông điệp, đệm PKCS7 (3 byte -> 16 byte), bắt lỗi sai khóa | In ra `SELF-TEST OK: 4 thông điệp mã hóa/giải mã đúng, sai khóa bị phát hiện.` | **Đạt** |
| **SQL Server: Cấu trúc CSDL** | Chạy script `01_Tao_CSDL.sql` bằng `sqlcmd` | Tạo CSDL `LAB2_MaHoaHienDai`, bảng `dbo.app_users` và `dbo.crypto_history` | Tạo thành công CSDL và 2 bảng (truy vấn `sys.tables` ra đủ 2 bảng) | **Đạt** |
| **SQL Server: Tạo tài khoản** | Chạy script `02_Tao_Tai_Khoan_SQL.sql` bằng `sqlcmd` | Tạo Login/User `lab2_user`, cấp quyền `db_datareader`, `db_datawriter` | Tạo tài khoản và cấp quyền thành công | **Đạt** |
| **SQL Server: Kiểm tra dữ liệu** | Chạy script `03_Kiem_Tra_Du_Lieu.sql` bằng `sqlcmd` | Truy vấn kiểm tra thông tin CSDL và cấu trúc bảng | `SELECT DB_NAME()` trả về `LAB2_MaHoaHienDai`, bảng sẵn sàng | **Đạt** |
| **SQL Server: Kết nối JDBC (Port 14333)** | Form `frm_KetNoiSQL` và `frm_AES` kết nối tới `127.0.0.1:14333` | Kết nối thành công tới instance qua cổng TCP 14333 | **Chưa kiểm thử trên GUI**: Dịch vụ `SQLEXPRESS` trên máy chưa bật giao thức TCP/IP qua cổng mạng 14333 (mặc định chỉ bật Shared Memory; shell dòng lệnh không có quyền Admin để cấu hình và restart service). Sinh viên chỉ cần mở SQL Server Configuration Manager bật TCP/IP cổng 14333 theo mục hướng dẫn trong README. | **Chưa kiểm thử** |

---

## Kết luận
- **100% các thuật toán mật mã đối xứng hiện đại** (DES, TripleDES, AES-GCM, RC4, Twofish, AES-CTR) đã được kiểm thử tự động bằng JUnit 5 và **đều đạt kết quả chuẩn xác 100%**.
- Bài tập nâng cao **AES-CBC Python** vượt qua toàn bộ self-test.
- Cơ sở dữ liệu và tài khoản trên **Microsoft SQL Server Express** đã được khởi tạo và kiểm tra cấu trúc đầy đủ.
