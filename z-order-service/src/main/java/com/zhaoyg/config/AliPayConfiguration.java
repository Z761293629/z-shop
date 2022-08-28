package com.zhaoyg.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;

/**
 * @author zhao
 * @date 2022/8/25
 */
@Data
public class AliPayConfiguration {
    public static final String ALI_PAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhXSLBDwmUjjSCPzUj1i3Ar+Xd8SSVo36zpV7Q2/FPnWJ5nXxfiN8yd0TW89oTB9E3YPmGklWTFMDJLfZQOSmaosbzfo1EoJc2GZ0YCsajj8qk106unTYU7nv17psnDEXRxxGS/ksIQ0sCgBWGQvqJF7dDDOqO2eFriCk6YyTZAiXIzraI6cvzY0jOpaXtfr5n2tKbkHzG7Hp6zRbhe+TpoQ+PR1cuueCBHofgXjynX4OxbHyGnQdePXmhOCqinIZp78MKK3iIkDlBXXM5cjx4ojc6kLKWxgxaQQibMFKT67PMPg/nUbfbGGnYJ49vcXNDUVAQbJLzgDrOWHKi4GyMQIDAQAB";

    private static final String PRIVATE_KEY =
            "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDgHsJ4CKsP7055" +
                    "xxTKjnW4wpqBzVHg69yuuLu9qc+DDn9HGKhR2TUdNPN+xffOvx8elnze/DSVQJhd" +
                    "uNa8J1SEumKLRe++YTdyslkqJ+jWZIm2k8eR5ci/POYVUUD363qdxEVU/EBgrThH" +
                    "OgcOgO4iJBmiWcWVRwpKNdpzHXm9SBueUNjpWrOWxtJ/lsmzUXd0Nkyg1ecGkfq4" +
                    "bqepPYFZTEvJuftP8lw6figofR5YaUq3nBcQyoqiw0NkFRDFV3XYZT9+OYNGUz3Q" +
                    "lahyw7bNpJ9XF9ydeFnnA5MkqBGIUEd3dW6uc2yGu6fGMxqe8CZcDfC7AQM64QP/" +
                    "DbdZp21fAgMBAAECggEAD9cct48lSAMJ8OF5PrfC6kQW14XDe7ov5sl3u3f9Cq5D" +
                    "oKrFesN3VZEghJ1JXntzn53TRY9dyA7Ath8STkoZVyg1PtK6lyIX4Stp0ejwM/b3" +
                    "JMhdGpq7DYkoWelcRimGiRAONmt9wVd82svnI8dJDXCvvyHJLTvQJl6YCfrTV+VW" +
                    "787a7pHA/8PICoWF7o1uIuHIV1uTQHVZvUWpHoN7CD8UAhiwuvoahBc7KHNcoCs9" +
                    "X4LR3ztmkTypgAxJP1tH2hkXPTIygmRsLSMP8WQoZrE7k/ArZ+FZUGVIa9y3jVvY" +
                    "egoTAi5r29g1GJYaFzWCDlusYs9ulS57r0IoHpiOQQKBgQD1b4HYG6yIutKwl8ZR" +
                    "2ig0tPd+LdpngedAFtQ3mr37zxoeFW5WL9DaMoM8Op3S2V1s30aqFtZ0VmI5rbAP" +
                    "+hGeYr8kkc+KeFGgxqNSR2P9KuII6j7lYJeDiIprRa10KXwcefum7Dm2Acouu7p5" +
                    "eO5IbjunwwJlYMcAENu75mcgqwKBgQDpxF/fyP/8yuNXf5qAd0MWhAC6fWCatUTq" +
                    "PNQBSmALTYGHZOR4RDrZO506peKNLiRR+e0A+v0NMv+dkdGyxpnkbTIwruB7bMtP" +
                    "Ir+m1dBfSAfg3ICS3nB3p4ERs113O0UInrdmscmEbpyHlnQPY+3mjuLvTDuOHzzl" +
                    "jCi7TE4uHQKBgHGejEqCDS5C5nRP3sRXrNMxj30/NuNB9M7rgpS3MwoWRu30V97Y" +
                    "ReQtDCnerN9GNwTGnAzzg5XZw6EV8fKDfpKG/hus0Jo2tvsp2cOmPvidWIRuBG3l" +
                    "eF6w3/tnBzRGXClGoyQUKWdDjthxmZLQU+b9+Onh0gMieR1jsIVc1RL/AoGAMrcB" +
                    "vcETmDpD/BWY5Un6agYVSmAs9yNgYhj0PZtvHsSf10nSLy1OPxap07FtZwedpZM9" +
                    "Ebth7t0ji4G5JrnjZSUzgFBQLza5HOADCCTW4fT0kJoF4tcTO0JBxB9PpbBcF7Sn" +
                    "QUZM28hLeTbw0u3xDNXu8c9uFDU+tuYjaA7z3xkCgYAnMbctR7Ixnri69f2TRvYE" +
                    "aO5x/eNm3jWJa+1LMfd2aQemBplQCE4AO+FnogJvEgoroUCoy912PQqvHo6mYCtu" +
                    "GT6I9qCE+iySaE0nQGZAoKBSSHTM9VO/cR+zmy/VAgo4xGlVbJNZIWbbpXoyZ5BZ" +
                    "b6Fj4S5FqHXxvEWqiXGReg==";

    private static volatile AlipayClient instance;

    private static final String SERVER_URL = "https://openapi.alipaydev.com/gateway.do";
    private static final String APP_ID = "2021000121651956";

    public static AlipayClient getInstance() {
        // DCL
        if (instance == null) {
            synchronized (AliPayConfiguration.class) {
                if (instance == null) {
                    instance = new DefaultAlipayClient(SERVER_URL,
                            APP_ID,
                            PRIVATE_KEY,
                            AlipayConstants.FORMAT_JSON,
                            AlipayConstants.CHARSET_UTF8,
                            ALI_PAY_PUBLIC_KEY,
                            AlipayConstants.SIGN_TYPE_RSA2
                    );
                }
            }
        }
        return instance;

    }

}
