# Payment Guide

[toc]

<a name="appply_for_account" ></a>
## id/key pairs

For some payment services, users must sign in first. The SDK has an *OAuth 2* client for our user system. You may apply for `client_id`/`client_secret` of *OAuth 2*。

For other 3rd-party payment services, users pay using their accounts in 3rd-party provider. But we need track the order in our system, which require *md5*/*RSA* to encrypt the request.

You may ask for two pairs of id/key.
- The `client_id` and`client_secret` of *OAuth 2*.
- The `partnerId` and *md5*/*RSA* key. If you want to use RSA, you need provide with your public key, and we'll provide you with our pubic key.

## How to run the demo

Find the class `tv.xiaocong.sdk.demo.Keys` and replace the key.

```java
static final String CLIENT_ID = "100018";
static final String CLIENT_SECRET = "ghjgrtyafcdbn345bvbndlk";
```

Run the demo.

You may need sign up/in first.

> Mobile number is required for signing up. If you couldn't finish the signing up, you may ask for a test user from user.

Click the *pay* button. You will see a test activity.

Before you code your payment service, refer to `tv.xiaocong.sdk.demo.PayActivity` and `tv.xiaocong.sdk.demo.XcPayUtils`.

## Payment providers

Users could choose other payment providers: [Alipay](https://www.alipay.com/) or [Yeepay](https://www.yeepay.com/). They could purchase items by RMB directly. For these payment, user don't need to sign in using their Xiaocong Account first - All they need is their accounts in Alipay or YeePay.

## `tv.xiaocong.sdk.demo.XcPayUtils`

The enter point of payment service is `tv.xiaocong.sdk.demo.XcPayUtils.pay`:

```java
/**
 * Execute payment.
 *
 * @param context
 *            an {@link Context} instance
 * @param partnerId
 * @param amount
 *            the paying amount
 * @param signType
 *            md5 or RSA
 * @param orderNo
 *            the order number in your system
 * @param pkgname
 *            the package name of you Application
 * @param goodsDes
 *            some descriptions about your goods
 * @param signature
 * @param notifyUrl
 *            the callback URL
 * @param remark
 */
public static void pay(Context context, String partnerId,
	String amount, String signType,
	String orderNo, String pkgname, String goodsDes,
	String signature, String notifyUrl,
    String remark, String accessToken)
```

The parameter `notifyUrl` is the callback url on your server, wich we will call it after we finish the process. See beblow.

The `remark` could be arbitrary message. We'll sent it back to you with `notifyUrl`

## Notify your server

Finally, your server will receive a callback after we finish the process. The callback urls seem like:

```
http://notify.java.jpxx.org/notify.jsp?orderNo=2013041510251288&amount=10&account=13218181&notifyTime=12365212352&goodsDes=sword&status=1&sign=ZPZULntRpJwFmGNIVKwjLEF2Tze7bqs60rxQ22CqT5J1UlvGo575QK9z/+p+7E9cOoRoWzqR6xHZ6WVv3dloyGKDR0btvrdq PgUAoeaX/YOWzTh00vwcQ+HBtXE+vPTfAqjCTxiiSJEOY7ATCF1q7iP3sfQxhS0nDUug1LP3OLk&mark=testcontent
```

`http://notify.java.jpxx.org/notify.jsp`

Query paramters:
- `orderNo`: the order number
- `amount`: Unit: cent
- `account`: The user's Xiaocong Account
- `sign`: the request signature
- `notifyTime` a long integer
- `goodsDes`
- `status`: 1 for successul payment; 2 for failure
- `mark`: your remark

The HTTP response for this request url should be a simple text:
- `success`: you got the result
- `fail`: any exceptions
- `sign_fail`: for invalid signature



