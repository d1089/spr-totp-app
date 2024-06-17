package com.demo.totpapp.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.demo.totpapp.utils.KeyGeneration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;

@RestController
public class UserController {

    private static final String ENCODED_SPACE = "%20";
    private static final String PLUS_SYMBOL = "+";

    @Autowired
    KeyGeneration keyGeneration;

    @GetMapping("/")
    public String home() {
        return "Welcome to the home page!";
    }
    @GetMapping("/genKey")
    public String genSecretKey(){
        return keyGeneration.generateSecretKey();
    }

    @GetMapping("/genQR")
    public String generateAuthenticatorQR(@RequestParam String email,
                                          @RequestParam String secretKey,
                                          @RequestParam String accountName) {
        try {
            String barCodeUrl = getGoogleAuthenticatorBarCode(secretKey, email, accountName);
            return createQRCode(barCodeUrl);
        } catch (Exception e) {
            System.out.println("Error while generating the Authenticator QR code " + e.getMessage());
        }
        return "Error Occurred";
    }


    private static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        try {
            String utf8 = "UTF-8";
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + account, utf8).replace(PLUS_SYMBOL, ENCODED_SPACE)
                    + "?secret=" + URLEncoder.encode(secretKey, utf8).replace(PLUS_SYMBOL, ENCODED_SPACE)
                    + "&issuer=" + URLEncoder.encode(issuer, utf8).replace(PLUS_SYMBOL, ENCODED_SPACE);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }


    public static String createQRCode(String barCodeData) {
        try{
            String filePath = "QRCode.png";
            int qrCodeImageHeight = 400;
            int qrCodeImageWidth = 400;
            BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE,
                    qrCodeImageWidth, qrCodeImageHeight);
            FileOutputStream out = new FileOutputStream(filePath);
            MatrixToImageWriter.writeToStream(matrix, "png", out);
            File img = new File(filePath);
            byte[] imgBytes = FileUtils.readFileToByteArray(img);

            return "data:image/PNG;base64," + Base64.getEncoder().encodeToString(imgBytes);
        } catch (Exception e) {
            System.out.println("Error while creating the Authenticator QR code "+ e.getMessage());
        }
        return "Failed to generate QR";
    }

    @GetMapping("/verTOTP")
    public String verTOTPCode(@RequestParam String totpCode){
        String actual_totp = getTOTPCode("RKBRJVSMJNQPBDMB635XG6LBZJCL2L2E");
        if(actual_totp.equals(totpCode))
            return "Success";
        return "Failure";
    }


    public String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }
}
