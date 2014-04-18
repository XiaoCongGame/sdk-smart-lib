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







