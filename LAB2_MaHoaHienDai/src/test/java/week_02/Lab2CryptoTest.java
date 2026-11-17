package week_02;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class Lab2CryptoTest {

    private static final String SAMPLE_TEXT = "Xin chào Lab 2 - Mã hóa hiện đại!";

    @Test
    @DisplayName("2.1 DES: Mã hóa, giải mã, đổi IV ngẫu nhiên, kiểm tra khóa sai")
    void testDESCipher() throws Exception {
        String key = "12345678";
        
        // 1. Decrypt(Encrypt(x)) = x
        String ciphertext1 = DESCipher.encrypt(SAMPLE_TEXT, key);
        assertNotNull(ciphertext1);
        String decrypted1 = DESCipher.decrypt(ciphertext1, key);
        assertEquals(SAMPLE_TEXT, decrypted1);

        // 2. Hai lần Encrypt cho bản mã khác nhau (do IV ngẫu nhiên)
        String ciphertext2 = DESCipher.encrypt(SAMPLE_TEXT, key);
        assertNotEquals(ciphertext1, ciphertext2, "Mỗi lần mã hóa phải có IV ngẫu nhiên dẫn đến bản mã khác nhau");
        assertEquals(SAMPLE_TEXT, DESCipher.decrypt(ciphertext2, key));

        // 3. Khóa ngắn (4 ký tự) phải ném ngoại lệ
        assertThrows(IllegalArgumentException.class, () -> DESCipher.encrypt(SAMPLE_TEXT, "1234"));

        // 4. Giải mã với khóa sai phải báo lỗi
        assertThrows(Exception.class, () -> DESCipher.decrypt(ciphertext1, "87654321"));
    }

    @Test
    @DisplayName("2.2 TripleDES: Mã hóa/giải mã đúng với khóa 24 byte, báo lỗi khi khóa 23 ký tự")
    void testTripleDESCipher() throws Exception {
        String key24 = "123456789012345678901234";
        
        // 1. Mã hóa và giải mã thành công
        String ciphertext = TripleDESCipher.encrypt(SAMPLE_TEXT, key24);
        String decrypted = TripleDESCipher.decrypt(ciphertext, key24);
        assertEquals(SAMPLE_TEXT, decrypted);

        // 2. Khóa 23 ký tự phải báo lỗi
        String key23 = "12345678901234567890123";
        assertThrows(IllegalArgumentException.class, () -> TripleDESCipher.encrypt(SAMPLE_TEXT, key23));
        assertThrows(IllegalArgumentException.class, () -> TripleDESCipher.decrypt(ciphertext, key23));
    }

    @Test
    @DisplayName("2.3 AES-GCM: Mã/giải mã đúng, sửa 1 ký tự bản mã thì giải mã thất bại")
    void testAESGCMCipher() throws Exception {
        String key = "MatKhauAES@2026";
        
        // 1. Mã hóa và giải mã đúng
        String ciphertext = AESCipher.encrypt(SAMPLE_TEXT, key);
        assertNotNull(ciphertext);
        String decrypted = AESCipher.decrypt(ciphertext, key);
        assertEquals(SAMPLE_TEXT, decrypted);

        // 2. Sửa 1 ký tự giữa chuỗi bản mã Base64 -> giải mã phải ném ngoại lệ (chống sửa đổi của GCM)
        char[] chars = ciphertext.toCharArray();
        int mid = chars.length / 2;
        chars[mid] = (chars[mid] == 'A') ? 'B' : 'A';
        String tamperedCiphertext = new String(chars);

        assertThrows(Exception.class, () -> AESCipher.decrypt(tamperedCiphertext, key));
    }

    @Test
    @DisplayName("2.4 RC4: Plaintext 'Plaintext', Key 'Key' cho HEX đúng BBF316E8D940AF0AD3")
    void testRC4Cipher() {
        String plaintext = "Plaintext";
        String key = "Key";
        String expectedHex = "BBF316E8D940AF0AD3";

        // 1. Mã hóa
        String actualHex = RC4Cipher.encrypt(plaintext, key);
        assertEquals(expectedHex, actualHex);

        // 2. Giải mã
        String decrypted = RC4Cipher.decrypt(actualHex, key);
        assertEquals(plaintext, decrypted);
    }

    @Test
    @DisplayName("2.5 Twofish: Mã/giải mã đúng, sai IV thì giải mã sai/lỗi, toFile/fromFile đúng")
    void testTwofishCipher() throws Exception {
        String key = "Twofish@Lab2";
        String iv = TwofishCipher.newIv();
        assertEquals(32, iv.length(), "IV phải là 32 ký tự HEX (16 byte)");

        // 1. Mã hóa và giải mã đúng
        String ciphertext = TwofishCipher.encrypt(SAMPLE_TEXT, key, iv);
        String decrypted = TwofishCipher.decrypt(ciphertext, key, iv);
        assertEquals(SAMPLE_TEXT, decrypted);

        // 2. Sai IV thì giải mã sai (khối đầu hỏng) hoặc ném lỗi
        String wrongIv = TwofishCipher.newIv();
        try {
            String badDecrypted = TwofishCipher.decrypt(ciphertext, key, wrongIv);
            assertNotEquals(SAMPLE_TEXT, badDecrypted, "Sai IV thì kết quả giải mã phải khác bản rõ ban đầu");
        } catch (Exception ex) {
            // Ném ngoại lệ cũng được chấp nhận
            assertTrue(true);
        }

        // 3. toFile và fromFile
        String fileContent = TwofishCipher.toFile(iv, ciphertext);
        assertTrue(fileContent.startsWith("IV="));
        String[] parts = TwofishCipher.fromFile(fileContent);
        assertEquals(iv, parts[0]);
        assertEquals(ciphertext, parts[1]);
    }

    @Test
    @DisplayName("2.6 AES-CTR: Mã/giải mã đúng, len(bản mã byte) = len(bản rõ byte), văn bản > 1000 ký tự đúng")
    void testAESCTRCipher() throws Exception {
        String key = AESCTRCipher.newKey();
        String nonce = AESCTRCipher.newNonce();

        // 1. Mã hóa và giải mã
        String ciphertext = AESCTRCipher.encrypt(SAMPLE_TEXT, key, nonce);
        String decrypted = AESCTRCipher.decrypt(ciphertext, key, nonce);
        assertEquals(SAMPLE_TEXT, decrypted);

        // 2. Độ dài bản mã byte = độ dài bản rõ byte (tính chất mã dòng CTR, không đệm)
        byte[] plainBytes = SAMPLE_TEXT.getBytes(StandardCharsets.UTF_8);
        byte[] cipherBytes = Base64.getDecoder().decode(ciphertext);
        assertEquals(plainBytes.length, cipherBytes.length, "Độ dài bản mã byte phải bằng độ dài bản rõ byte");

        // 3. Văn bản dài > 1000 ký tự
        StringBuilder longTextBuilder = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            longTextBuilder.append("Đoạn văn bản kiểm thử AES-CTR có dấu độ dài lớn ").append(i).append(". ");
        }
        String longText = longTextBuilder.toString();
        assertTrue(longText.length() > 1000, "Văn bản phải dài hơn 1000 ký tự");

        String longNonce = AESCTRCipher.newNonce();
        String longCiphertext = AESCTRCipher.encrypt(longText, key, longNonce);
        String longDecrypted = AESCTRCipher.decrypt(longCiphertext, key, longNonce);
        assertEquals(longText, longDecrypted);

        // 4. toFile và fromFile
        String fileContent = AESCTRCipher.toFile(nonce, ciphertext);
        assertTrue(fileContent.startsWith("NONCE="));
        String[] parts = AESCTRCipher.fromFile(fileContent);
        assertEquals(nonce, parts[0]);
        assertEquals(ciphertext, parts[1]);
    }
}
