package yi.master.coretest.message.process;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Cipher;

import org.apache.axis.encoding.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import net.sf.json.JSONObject;
import yi.master.constant.MessageKeys;
import yi.master.coretest.message.parse.URLMessageParse;
import yi.master.util.PracticalUtils;
import yi.master.util.rsa.RSABase64Utils;

/**
 * @author xuwangcheng
 * @version 2018.5
 *
 */
public class AnHuiAPPEncryptMessageProcess extends MessageProcess {
	
	public static final Logger LOGGER = Logger.getLogger(AnHuiAPPEncryptMessageProcess.class);
	/**
	 * 默认加密类型
	 */
	public static final String DEFAULT_ALGORITHM_TYPE = "RSA";
	/**
	 * 默认公钥
	 */
	/*public static final String DEFAULT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTzkDgp27ZJ66BlR3tC1CarLVXrj14Ca05hzvG3aIxA+em3BCmMaWe7FurDP6nDD5u2EZRT9gmVEe3khXrHODLKVFOGdY9y6DBrO6Eix5aKLh4C3eCwW+5duJdKJT2SZrs3h8xvdQrgyDLM52P73guyWUhoNlSEmDLo6MM6bRHzwIDAQAB\r"
			+ "ip5/HiCVaYmzep/D1FupbGGVhV3ejjseNMC0pNYdVbmNN7OZyeSXQK1jtKl1pkkI0A+GpEMo4wW/\r"
			+ "BTqxI8jUb8VfANQDD42MF4OaT5nDl2/ei3PNEpOi0dMDEMBpVJuAAZX+R5trjD3oeST4+/PIVMLm\r"
			+ "J3vAxpSRkNpeqEN/bO0=";*/
	
	public static final String DEFAULT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCfRTdcPIH10gT9f31rQuIInLwe"  
            + "\r" + "7fl2dtEJ93gTmjE9c2H+kLVENWgECiJVQ5sonQNfwToMKdO0b3Olf4pgBKeLThra" + "\r"  
            + "z/L3nYJYlbqjHC3jTjUnZc0luumpXGsox62+PuSGBlfb8zJO6hix4GV/vhyQVCpG" + "\r"  
            + "9aYqgE7zyTRZYX9byQIDAQAB" + "\r";  

	@Override
	public String processRequestMessage(String requestMessage, String processParameter) {
		
		JSONObject parameter = JSONObject.fromObject(processParameter);
		String key = PracticalUtils.replaceGlobalVariable(parameter.getString(MessageKeys.ANHUI_APP_ENCRYPT_KEY), null);
		List<String> sensitiveInformationParameters = Arrays.asList(PracticalUtils.replaceGlobalVariable(parameter.getString(MessageKeys.ANHUI_APP_ENCRYPT_SENSITIVE_INFORMATION), null).split(","));
		SortedMap<String, String> requestParameters = new TreeMap<String, String>(URLMessageParse.parseUrlToMap(requestMessage, null));		
		//根据配置来加密敏感字段
		String algorithmType = PracticalUtils.replaceGlobalVariable(parameter.getString(MessageKeys.ANHUI_APP_ENCRYPT_ALGORITHM_TYPE), null);
		if (StringUtils.isBlank(algorithmType)) algorithmType = DEFAULT_ALGORITHM_TYPE;
		
		String publicKey = PracticalUtils.replaceGlobalVariable(parameter.getString(MessageKeys.ANHUI_APP_ENCRYPT_PUBLIC_KEY), null);
		if (StringUtils.isBlank(publicKey)) publicKey = DEFAULT_PUBLIC_KEY;
		
		for (String infoKey:requestParameters.keySet()) {
			if (sensitiveInformationParameters.contains(infoKey)) {
				//进行敏感字段加密
				try {
					String value = encrypt(requestParameters.get(infoKey), publicKey, algorithmType);
					requestParameters.put(infoKey, value);
				} catch (Exception e) {
					
					LOGGER.error("敏感字段加密失败,该字段" + infoKey + "未进行敏感字段加密!", e);
				}
				
			}
		}
		
		try {
			//获取加密token
			String params = toParamStr(requestParameters, false).toString();
			params = params.substring(0, params.length() - 1);
			String token  = getMD5String(params + "&key=" + key, "UTF-8");
			/*params = toParamStr(requestParameters, true).toString();
			params = params.substring(0, params.length() - 1);*/
			return params + "&token=" + token;
		} catch (Exception e) {
			
			LOGGER.error("加密token失败:requestMessage=" + requestMessage + ",processParameter=" + processParameter);
			return requestMessage;
		}
	}
	
	@Override
	public String processResponseMessage(String responseMessage,
			String processParameter) {
		
		return responseMessage;
	}
	
	private StringBuilder toParamStr(Map<String, String> paramMap, boolean flag) throws UnsupportedEncodingException{
		StringBuilder sb = new StringBuilder();
		if(paramMap != null){
			for (Entry<String, String> e : paramMap.entrySet()) {
				String k = e.getKey();
				String v = e.getValue();
				if (flag) {
					v = URLEncoder.encode(v, "utf-8");
				}
//				if(v != null && ! "".equals(v)){
					sb.append(k).append("=").append(v).append("&");
//				}
			}
		}
		return sb; 
	}
	
	/**
	 * token MD5加密
	 * @param plainText
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String getMD5String(String plainText, String charset)
			throws UnsupportedEncodingException {
		StringBuffer buf = new StringBuffer("");
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes(charset));
			byte b[] = md.digest();
			int i;
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("NoSuchAlgorithmException", e);
		}
		return buf.toString();
	}

	/**
	 * 从字符串中加载公钥
	 * 
	 * @param publicKeyStr
	 *            公钥数据字符串
	 * @throws Exception
	 *             加载公钥时产生的异常
	 */
	public static PublicKey loadPublicKeyByStr(String publicKeyStr, String type)
			throws Exception {
		try {
			byte[] buffer = RSABase64Utils.decode(publicKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance(type);
//			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("公钥非法");
		} catch (NullPointerException e) {
			throw new Exception("公钥数据为空");
		}
	}

	/**
	 * 从字符串中加载私钥<br>
	 * RSA用 加载时使用的是PKCS8EncodedKeySpec（PKCS#8编码的Key指令）。
	 * 
	 * @param privateKeyStr
	 * @return
	 * @throws Exception
	 */
	private static PrivateKey loadPrivateKey(String privateKeyStr, String type)
			throws Exception {
		try {
			byte[] buffer = RSABase64Utils.decode(privateKeyStr);
			// X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			KeyFactory keyFactory = KeyFactory.getInstance(type);
			return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("私钥非法");
		} catch (NullPointerException e) {
			throw new Exception("私钥数据为空");
		}
	}

	/**
	 * 加密方法 source： 源数据
	 */
	public static String encrypt(String source, String publicKey, String algorithmType) throws Exception {
		Key key = loadPublicKeyByStr(publicKey, algorithmType);
		/** 得到Cipher对象来实现对源数据的RSA加密 */
		Cipher cipher = Cipher.getInstance(algorithmType,bouncyCastleProvider);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] b = source.getBytes();
		/** 执行加密操作 */
		byte[] b1 = cipher.doFinal(b);
		return Base64.encode(b1).replaceAll("\\r|\\n", "");
	}

	/**
	 * 解密算法 cryptograph:密文
	 */
	
	private static org.bouncycastle.jce.provider.BouncyCastleProvider bouncyCastleProvider = new org.bouncycastle.jce.provider.BouncyCastleProvider();

	public static String decrypt(String cryptograph, String privateKey, String algorithmType) throws Exception {
		Key key = loadPrivateKey(privateKey, algorithmType);
		/** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
		Cipher cipher = Cipher.getInstance(algorithmType, bouncyCastleProvider);
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] b1 = Base64.decode(cryptograph);
		/** 执行解密操作 */
		byte[] b = cipher.doFinal(b1);
		return new String(b);
	}
	
	
	private static char[] base64EncodeChars = new char[]
			{ 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
					'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
					'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
					'6', '7', '8', '9', '+', '/' };
			private static byte[] base64DecodeChars = new byte[]
			{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53,
					54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
					12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29,
					30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1,
					-1, -1, -1 };

			/**
			 * 加密
			 * 
			 * @param data
			 * @return
			 */
			public static String encode(byte[] data)
			{
				StringBuffer sb = new StringBuffer();
				int len = data.length;
				int i = 0;
				int b1, b2, b3;
				while (i < len)
				{
					b1 = data[i++] & 0xff;
					if (i == len)
					{
						sb.append(base64EncodeChars[b1 >>> 2]);
						sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
						sb.append("==");
						break;
					}
					b2 = data[i++] & 0xff;
					if (i == len)
					{
						sb.append(base64EncodeChars[b1 >>> 2]);
						sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
						sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
						sb.append("=");
						break;
					}
					b3 = data[i++] & 0xff;
					sb.append(base64EncodeChars[b1 >>> 2]);
					sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
					sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);
					sb.append(base64EncodeChars[b3 & 0x3f]);
				}
				return sb.toString();
			}

			/**
			 * 解密
			 * 
			 * @param str
			 * @return
			 */
			public static byte[] decode(String str)
			{
				try
				{
					return decodePrivate(str);
				} catch (UnsupportedEncodingException e)
				{
					LOGGER.warn("UnsupportedEncodingException", e);
				}
				return new byte[]
				{};
			}

			private static byte[] decodePrivate(String str) throws UnsupportedEncodingException
			{
				StringBuffer sb = new StringBuffer();
				byte[] data = null;
				data = str.getBytes("US-ASCII");
				int len = data.length;
				int i = 0;
				int b1, b2, b3, b4;
				while (i < len)
				{

					do
					{
						b1 = base64DecodeChars[data[i++]];
					} while (i < len && b1 == -1);
					if (b1 == -1)
						break;

					do
					{
						b2 = base64DecodeChars[data[i++]];
					} while (i < len && b2 == -1);
					if (b2 == -1)
						break;
					sb.append((char) ((b1 << 2) | ((b2 & 0x30) >>> 4)));

					do
					{
						b3 = data[i++];
						if (b3 == 61)
							return sb.toString().getBytes("iso8859-1");
						b3 = base64DecodeChars[b3];
					} while (i < len && b3 == -1);
					if (b3 == -1)
						break;
					sb.append((char) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));

					do
					{
						b4 = data[i++];
						if (b4 == 61)
							return sb.toString().getBytes("iso8859-1");
						b4 = base64DecodeChars[b4];
					} while (i < len && b4 == -1);
					if (b4 == -1)
						break;
					sb.append((char) (((b3 & 0x03) << 6) | b4));
				}
				return sb.toString().getBytes("iso8859-1");
			}

}
