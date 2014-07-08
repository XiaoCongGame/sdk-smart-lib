[toc]

## 注册

用户可以使用SDK注册一个新的小葱号。流程如下：

1. 用户只能用手机号注册。用手机号做用户名。
	![First step](img/signup-1.png)
1. 需要验证发送到手机上的验证码。
	![step2](img/signup-2.png)
	![step2](img/signup-3.png)
1. 输入两次密码。密码长度为6到16个字母或数字。
	![step2](img/signup-4.png)
1. 提交请求
	![step2](img/signup-5.png)

## 登录

用户使用用户名登录。用户名可以是手机号、邮箱、小葱号（取决于用户注册时使用的设备）。

![](img/signin.png)

在调用登录Activity后，在回调中可以从Intent中取到AccessToken和用户名。

```
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE_LOGIN && resultCode == RESULT_OK && data != null) {
        String accessToken = data.getStringExtra(LoginActivity.RESPONSE_ACCESS_TOKEN);
        String username = data.getStringExtra(LoginActivity.USERNAME);

        String toast = String.format("access_token: %s, username: %s", accessToken, username);
        Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
    }
    ...
```

此外，登录成功后，用户名和AccessToken存储在`SharedPreferences`中。可以通过`tv.xiaocong.sdk.security.LoginActivity.getSecurePreferences(Context)`获取到这个`SharedPreferences`。然后利用`tv.xiaocong.sdk.security.LoginActivity.RESPONSE_ACCESS_TOKEN`、`tv.xiaocong.sdk.security.LoginActivity.RESPONSE_REFRESH_TOKEN`、`tv.xiaocong.sdk.security.LoginActivity.USERNAME`三个键，可以分别取到缓存的AccessToken、RefreshToken和用户名。