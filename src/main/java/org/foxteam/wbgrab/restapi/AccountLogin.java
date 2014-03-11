package org.foxteam.wbgrab.restapi;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.RequestBuilder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

/**
 * Created by YeahO_O on 3/7/14.
 */
public class AccountLogin extends RestRequest {
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String checksum(String value) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(value.getBytes());
            messageDigest.update(RestConfig.getValue("MD5Key").getBytes());
            byte[] result = messageDigest.digest();
            return String.valueOf(HEX_DIGITS[result[0] & 0xf]) +
                    HEX_DIGITS[result[2] & 0xf] +
                    HEX_DIGITS[(result[1] >> 4) & 0xf] +
                    HEX_DIGITS[(result[5] >> 4) & 0xf] +
                    HEX_DIGITS[result[8] & 0xf] +
                    HEX_DIGITS[result[4] & 0xf] +
                    HEX_DIGITS[result[12] & 0xf] +
                    HEX_DIGITS[result[13] & 0xf];
        } catch (NoSuchAlgorithmException impossible) {
            return "";
        }
    }

    private static String encryptPassword(String password) throws RestException {
        try {
            BigInteger m = new BigInteger(RestConfig.getValue("RSAKey"));
            BigInteger e = new BigInteger("65537");
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
            KeyFactory keyFactory = null;
            keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] result = cipher.doFinal(password.getBytes());
            return new String(Base64.encodeBase64(result));
        } catch (NoSuchAlgorithmException e) {
            throw new RestException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RestException(e);
        } catch (InvalidKeyException e) {
            throw new RestException(e);
        } catch (BadPaddingException e) {
            throw new RestException(e);
        } catch (InvalidKeySpecException e) {
            throw new RestException(e);
        } catch (NoSuchPaddingException e) {
            throw new RestException(e);
        }
    }

    public WbLoginSession login(String username, String password) throws RestException {
        RequestBuilder request = RequestBuilder.post()
                .setUri(RestConfig.getValue("ServerUrl") + "/account/login")
                .addParameter("s", checksum(username + password))
                .addParameter("u", username)
                .addParameter("p", encryptPassword(password))
                .addParameter("flag", "1")
                .addParameter("trim_user", "1");
        JSONObject json = restRequest(request, RestRequest.AuthType.AUTH_TYPE_NONE);
        return WbLoginSession.fromJSON(json);
    }
}
