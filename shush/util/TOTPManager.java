package shush.util;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * TOTPManager handles Time-Based One-Time Password operations,
 * including generating secrets, displaying QR codes, and verifying codes.
 */
public class TOTPManager {

    private static final String ISSUER = "Shush";

    /**
     * Generates a new TOTP secret key.
     * 
     * @return SecretKey generated for TOTP
     * @throws NoSuchAlgorithmException if the HMAC-SHA1 algorithm is unavailable
     */
    public static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(TimeBasedOneTimePasswordGenerator.TOTP_ALGORITHM_HMAC_SHA1);
        keyGenerator.init(160); // RFC recommends 160 bits for SHA1
        return keyGenerator.generateKey();
    }

    /**
     * Converts the SecretKey into a Base64-encoded string for manual entry or storage.
     * 
     * @param key the SecretKey to encode
     * @return Base64 string representation of the key
     */
    public static String getBase32Secret(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * Builds the otpauth URI for use in QR code or manual authenticator setup.
     * 
     * @param user the username or account label
     * @param base32Secret the secret in Base64 format
     * @return otpauth:// URI string
     */
    public static String buildOtpAuthURI(String user, String base32Secret) {
        String label = URLEncoder.encode(ISSUER + ":" + user, StandardCharsets.UTF_8);
        String issuerEncoded = URLEncoder.encode(ISSUER, StandardCharsets.UTF_8);
        return String.format("otpauth://totp/%s?secret=%s&issuer=%s", label, base32Secret, issuerEncoded);
    }

    /**
     * Generates and prints a QR Code to the terminal using Unicode block characters.
     * 
     * @param otpAuthUrl the otpauth URI to encode in the QR
     * @throws WriterException if QR code generation fails
     */
    public static void printQRCodeToConsole(String otpAuthUrl) throws WriterException {
        QRCodeWriter qrWriter = new QRCodeWriter();
        BitMatrix matrix = qrWriter.encode(otpAuthUrl, BarcodeFormat.QR_CODE, 40, 40);

        for (int y = 0; y < matrix.getHeight(); y++) {
            for (int x = 0; x < matrix.getWidth(); x++) {
                System.out.print(matrix.get(x, y) ? "\u2588\u2588" : "  ");
            }
            System.out.println();
        }
    }

    /**
     * Validates a user-provided TOTP code.
     * 
     * @param code the 6-digit code entered by the user
     * @param key the SecretKey associated with the authenticator
     * @return true if the code is valid for the current time window, false otherwise
     * @throws NoSuchAlgorithmException if TOTP algorithm is not available
     * @throws InvalidKeyException if the key is invalid
     */
    public static boolean validateCode(String code, SecretKey key) throws NoSuchAlgorithmException, InvalidKeyException {
        TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator(30, TimeUnit.SECONDS);
        String generatedCode = String.format("%06d", totp.generateOneTimePassword(key, Instant.now()));
        return generatedCode.equals(code);
    }
}
