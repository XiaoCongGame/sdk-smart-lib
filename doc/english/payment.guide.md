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

The following is the entrance of different payment providers.

![](payways.png)

## `tv.xiaocong.sdk.demo.XcPayUtils`

The enter point of payment service is `tv.xiaocong.sdk.demo.XcPayUtils.pay`:

```java
    /**
     * Execute payment.
     * 
     * @param caller
     *            caller that starts {@link PaymentStartActivity}.
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
    public static void pay(Activity caller, int partnerId, int amount, String signType,
            String orderNo, String pkgname, String goodsDes, String signature, String notifyUrl,
            String remark, String accessToken) {
        PaymentStartActivity.startMe(caller, partnerId, amount, signType, orderNo, pkgname,
                goodsDes, signature, notifyUrl, remark, accessToken);
    }
```


The `caller` is an Activity in your app which starts payment. After calling `pay` above, our activity `PaymentStartActivity` will show up, in which users do payments. When payments flows finish in our sdk, then screen return to your activity `caller`. And you could be notified the result by using `onActivityResult`.

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (requestCode == PaymentStartActivity.REQUEST_CODE_START_PAY) {
		Toast.makeText(this, "Result: " + resultCode, Toast.LENGTH_LONG).show();
	}
}
```

All possible `resultCode`s are defined in `com.xiaocong.sdk.PaymentResults`.

- `ILLEGAL_PARAMETER`: Your request(ie. calling `pay`) is invalid.
- `NO_PAY_WAY`: No payment providers available. If you come with this error, there may be a business/contract problem, and you may send an email to ask if pay ways are open to you.
- `PAYRESULT_OK`: Payment successful.
- `PAYRESULT_FAIL`: Payment failed definitely.
- `CANCEL_BUY`: Users cancel the payemnt explicitly.
- `CREATE_ORDER_FAIL`: Your order number has existed in our system. This may be a bug from you.
- `AsyncTask_result` and *all other codes*: The result is pending or not resolved yet, you should query the order from our server to get the final result.


The parameter `notifyUrl` is the callback url on your server, wich we will call it after we finish the process. See beblow.

The `remark` could be arbitrary message. We'll sent it back to you with `notifyUrl`.



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

## Query orders

You could query the status of an order from our server:
```
http://data.xiaocong.tv/queryOrderInfo.action?orderNo=2013041510251288&version=2&sign=b4600ae75b27f5fe1fb213f6e6d9620a
```

Http query parameters:
- `orderNo` [String]：The order number in your system, which is identical with the parameter `orderNo` of `XcPayUtils.pay`.
- `version` [int]：Always be `2`。
- `sign` [String]：The signature, which is identical with the parameter `signature` of `XcPayUtils.pay`.

The reponse is plain text, like `Code~Message`. For example, for successful payment, the response is `200~Success`.

The full list of response text:
- `200~Success`: success
- `512~Order payment failure`: payment failure
- `210~Order processing`: processing
- `509~Order not exists`: the order isn't existed
- `400~Version number error`: invalid `version`
- `400~Order number is request`: `orderNo` is missing
- `400~Sign is request`: `sign` is missing
- `401~Sign verification failed`: invalid `sign`

