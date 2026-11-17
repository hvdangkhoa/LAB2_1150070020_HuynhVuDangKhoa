# GHI CHÚ SỬA LỖI VÀ CHUẨN HÓA MÃ NGUỒN (LAB 2)

Tài liệu này ghi lại các điều chỉnh kỹ thuật nhỏ khi chuyển đổi mã nguồn từ tài liệu PDF `LAB2_HuongDan.pdf` sang project Maven thực tế, nhằm đảm bảo mã nguồn tuân thủ cú pháp Java chuẩn và biên dịch thành công 100%.

---

## 1. Tệp `pom.xml`
- **Hiện tượng trong PDF**: Một số thẻ XML trong tài liệu PDF bị ngắt dòng khi sao chép (ví dụ: `xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"`, `xsi:schemaLocation`).
- **Xử lý**: Chuẩn hóa liên kết XML Schema về đúng chuẩn Maven 4.0.0.
- **Thêm dependency kiểm thử**: Thêm thư viện `org.junit.jupiter:junit-jupiter:5.10.2` với phạm vi `<scope>test</scope>` để phục vụ việc tự động kiểm thử phi giao diện theo yêu cầu của Bài thực hành mà không làm ảnh hưởng đến thời gian chạy (`exec.mainClass = week_02.frm_Menu`) hay gói jar ứng dụng chính.

---

## 2. Tệp `CipherUtil.java`
- **Hiện tượng trong PDF (Trang 6)**: Chuỗi thông báo lỗi trong phương thức `fromHex` bị ngắt dòng giữa trang:
  ```java
  throw new IllegalArgumentException("Chuỗi HEX không hợp lệ (chỉ gồm 0-9, A-F và có số ký 
  tự chẵn).");
  ```
- **Xử lý**: Nối liền chuỗi string thành một dòng hoặc dùng nối chuỗi hợp lệ trong Java:
  ```java
  throw new IllegalArgumentException("Chuỗi HEX không hợp lệ (chỉ gồm 0-9, A-F và có số ký tự chẵn).");
  ```

---

## 3. Tệp `DESCipher.java` và `TripleDESCipher.java`
- **Hiện tượng trong PDF (Trang 17, 21)**: Chuỗi thông báo lỗi kiểm tra độ dài khóa bị ngắt dòng khi in tài liệu.
- **Xử lý**: Chuẩn hóa chuỗi thông báo thành chuỗi liên tục, bảo đảm không bị lỗi cú pháp `unclosed string literal`.

---

## 4. Tệp `TwofishCipher.java` và `AESCTRCipher.java`
- **Hiện tượng trong PDF (Trang 35, 39)**: Chuỗi thông báo lỗi định dạng tệp lưu `toFile`/`fromFile` bị ngắt dòng.
- **Xử lý**: Chuẩn hóa chuỗi ký tự trên một dòng thống nhất.

---

## 5. Tệp `sql/02_Tao_Tai_Khoan_SQL.sql`
- **Yêu cầu bảo mật**: Để tránh lộ mật khẩu trong kho lưu trữ Git theo quy định an toàn thông tin, mật khẩu thật đã được thay bằng placeholder `<MAT_KHAU_LAB>` kèm chú thích rõ ràng. Trong quá trình cấu hình nội bộ trên máy cá nhân, sinh viên/giảng viên thay bằng mật khẩu quy định (`LabUser@2026`).

---

## 6. Thiết kế giao diện Swing Form (.java + .form)
- **Chuẩn hóa NetBeans GUI Builder**: Do thiết kế giao diện từ môi trường tự động hóa không thao tác chuột kéo thả, toàn bộ 9 form được tạo kèm cặp tệp `.java` (chứa khối `// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents` và `// Variables declaration`) cùng tệp `.form` (định dạng XML chuẩn của NetBeans IDE). Nhờ vậy, NetBeans có thể mở trực tiếp cả tab **Design** và tab **Source**, giữ nguyên 100% tên biến và thuộc tính điều khiển theo các bảng 2.4 - 2.11 trong tài liệu.
