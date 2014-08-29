### Current stable version: 1.4

**Release Date**: 2014-08-29

1. Support a new payment service [Shengpay](https://www.shengpay.com/).
1. Improve&simplify the codes of demo.
1. Fix some bugs.
1. We've added some activities, so the manifest file must be updated if you want this version.

----

### Version 1.3.1

Reduce memory footprint(by 30M);
Improve the response to network failure: If network failure occur which is not recoverable, return a new state `com.xiaocong.sdk.PaymentResults.PAYRESULT_FAIL_NET`.

----


### Version 1.3.0

Add a 'Sign up' button in sign-in dialog. | 在登录界面底部增加注册按钮。

----

### Version 1.2.0

Fix a bug under Yeepay way.

----

### Version 1.1.0

Required JDK 1.7 => 1.6.

----

### Version 1.0.0

Improve UI; Fix some bugs; Support small screen.

----

### Version 0.9.1

2014-05-28
Improve payemnt flow; Support 360 payment.

1. The payment flows have been improved. `PaymentStartActivity` and `PayWayActivity` are merged into a single activity `PaymentActivity_`.
1. The Payment Test DEMO has been simplified. You could start the payment test with a single button.
1. We now take care of checking user account when needed. You don't need to care for user authenciation any more. Refreshing the accessTokens automatically is also supported inside payment SDK now.
1. The payment result set has been polished. Please refer to the doc.

Sorry for some imcompatible changes.

----









