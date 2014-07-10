This is the Xiaocong SDK.

## Feature | 功能特性

* Xiaocong advertisement and pushing service.
* Xiaocong User/Account system service.
* Xiaocong Payment SDK.

> Tip
* Our SDK is designed for TVs, which have higher resolution than mobile phones. We expect the resolution 1280px * 960px at least.
* Almost all android TVs in the market carry Android 4.0+. So we need `minSdkVersion > 14` .
* JDK 1.6+ required.

## Quick Start | 快速上手

[English](doc/english/quick-start.md) | [中文版](doc/zh/quick-start.md)

For more documents, you could inspect the `doc` directory in the SDK.

## Change Logs | 版本

You could find every release version in [release page](https://github.com/XiaoCongGame/sdk-smart-lib/releases).

### Version 0.9.1

2014-05-28
Improve payemnt flow; Support 360 payment.

1. The payment flows have been improved. `PaymentStartActivity` and `PayWayActivity` are merged into a single activity `PaymentActivity_`.
1. The Payment Test DEMO has been simplified. You could start the payment test with a single button.
1. We now take care of checking user account when needed. You don't need to care for user authenciation any more. Refreshing the accessTokens automatically is also supported inside payment SDK now.
1. The payment result set has been polished. Please refer to the doc.

Sorry for some imcompatible changes.

### Version 1.0.0

Improve UI; Fix some bugs; Support small screen.

### Version 1.1.0

Required JDK 1.7 => 1.6.

### Version 1.2.0

Fix a bug under Yeepay way.

### Version 1.3.0

Add a 'Sign up' button in sign-in dialog. | 在登录界面底部增加注册按钮。

### Version 1.3.1

Reduce memory footprint(by 30M);
Improve the response to network failure: If network failure occur which is not recoverable, return a new state `com.xiaocong.sdk.PaymentResults.PAYRESULT_FAIL_NET`.