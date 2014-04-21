package tv.xiaocong.sdk.security;

import java.lang.ref.WeakReference;

import tv.xiaocong.sdk.R;
import tv.xiaocong.sdk.TextWatcherAdapter;
import tv.xiaocong.sdk.XcAndroidUtils;
import tv.xiaocong.sdk.XcServiceClient;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 小葱用户注册界面
 * 
 * @author yaoyuan
 * 
 */
public class RegisterActivity extends Activity {

    /** 注册失败 */
    public static final int RESULT_CODE_FAIL = 400;

    public static final String USERNAME = "username";

    private static final String LOG_TAG = "register";

    private static final int MOBILE_NUM_LENGTH = 11;

    private static final long RESEND_CODE_DELAY = 60 * 1000; // 60秒后允许重发验证码

    private String userMobileNum; // 手机号
    private String secureCode; // 校验码

    private EditText usernameInput;
    private Button sendCodeBtn;
    private EditText codeInput;
    private EditText passwordInput;
    private TextView passwordStatusView;
    private EditText passwordConfirmInput;
    private Button submitBtn;
    private Button cancelBtn;
    private TextView statusView;

    private int validFieldBgColor;
    private int invalidFieldBgColor;

    private SendCodeTask sendCodeTask;

    private AnimatorSet statusAnimatorSet;

    private TextWatcher passwordMatchTest = new TextWatcherAdapter() {
        @Override
        public void afterTextChanged(Editable s) {
            String password1 = passwordInput.getText().toString().trim();
            String password2 = passwordConfirmInput.getText().toString().trim();

            if (password1.equals(password2) && checkPassword(password1)) {
                passwordConfirmInput.setTextColor(validFieldBgColor);

                showField(submitBtn);
            } else {
                passwordConfirmInput.setTextColor(invalidFieldBgColor);

                hideField(submitBtn);
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.xcsdk_register);

        // resouce
        validFieldBgColor = getResources().getColor(R.color.xcsdk_valid_field);
        invalidFieldBgColor = getResources().getColor(R.color.xcsdk_invalid_field);

        // view
        usernameInput = (EditText) findViewById(R.id.xcsdk_register_username);
        sendCodeBtn = (Button) findViewById(R.id.xcsdk_register_send_code);
        codeInput = (EditText) findViewById(R.id.xcsdk_register_code);
        passwordInput = (EditText) findViewById(R.id.xcsdk_register_password);
        passwordStatusView = (TextView) findViewById(R.id.xcsdk_register_password_status);
        passwordConfirmInput = (EditText) findViewById(R.id.xcsdk_register_password_confirm);
        submitBtn = (Button) findViewById(R.id.xcsdk_register_submit);
        cancelBtn = (Button) findViewById(R.id.xcsdk_register_cancel);
        statusView = (TextView) findViewById(R.id.xcsdk_register_status);

        // 配置
        // 触摸对话框外不关闭对话框
        setFinishOnTouchOutside(false);

        resetAllStates();

        usernameInput.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                String mobileNum = s.toString();

                if (userMobileNum != null && !mobileNum.equals(userMobileNum)) {
                    resetAllStates();
                }

                if (mobileNum.length() == MOBILE_NUM_LENGTH) {
                    showField(sendCodeBtn);
                    sendCodeBtn.requestFocus();

                    userMobileNum = mobileNum;
                }
            }
        });

        codeInput.addTextChangedListener(new TextWatcherAdapter() {

            @Override
            public void afterTextChanged(Editable s) {
                if (secureCode != null && s.toString().equals(secureCode)) {
                    codeInput.setTextColor(validFieldBgColor);
                    showField(passwordInput, passwordStatusView, passwordConfirmInput);
                    passwordInput.requestFocus();

                    hideField(sendCodeBtn, codeInput);
                } else {
                    codeInput.setTextColor(invalidFieldBgColor);
                    hideField(passwordInput, passwordStatusView, passwordConfirmInput);
                }
            }
        });

        passwordInput.addTextChangedListener(passwordMatchTest);
        passwordConfirmInput.addTextChangedListener(passwordMatchTest);

        passwordInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkPassword(passwordInput.getText().toString());
                }
            }
        });
    }

    private boolean checkPassword(String p) {
        boolean valid = p.matches("\\w{6,16}");
        if (valid) {
            hideField(passwordStatusView);
        } else {
            showField(passwordStatusView);

            passwordStatusView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

            AnimatorSet fieldScaleInAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(this,
                    R.animator.rock_horizontal);
            fieldScaleInAnimator.setTarget(passwordStatusView);
            fieldScaleInAnimator.start();
        }

        return valid;
    }

    private void resetAllStates() {
        // 改变手机号后，重置所有状态
        userMobileNum = null;
        secureCode = null;

        codeInput.setText(null);
        passwordInput.setText(null);
        passwordConfirmInput.setText(null);

        hideField(sendCodeBtn, codeInput, passwordInput, passwordStatusView, passwordConfirmInput,
                submitBtn);
        passwordStatusView.setText(getResources().getString(R.string.xcsdk_register_password_tip));
    }

    /**
     * 发送验证码
     */
    public void sendCode(View view) {
        secureCode = null;

        // 先取消之前的
        if (sendCodeTask != null && sendCodeTask.getStatus() != Status.FINISHED) {
            sendCodeTask.cancel(true);
        }

        // 发送
        // sendCodeTask = new SendCodeTask(this);
        // sendCodeTask.execute(usernameInput.getText().toString());
        secureCode = "123";

        sendCodeBtn.setText(R.string.xcsdk_register_code_sent);
        sendCodeBtn.setEnabled(false);

        // 1分钟后允许重发
        ResetSendCode resetSendCode = new ResetSendCode(this);
        view.postDelayed(resetSendCode, RESEND_CODE_DELAY);

        showField(codeInput);
        codeInput.requestFocus();
    }

    private void resetSendCodeBtn() {
        sendCodeBtn.setEnabled(true);
        sendCodeBtn.setText(getResources().getText(R.string.xcsdk_register_send_code_again));
    }

    private void showField(View... views) {
        for (View view : views) {
            view.setVisibility(View.VISIBLE);

            AnimatorSet fieldScaleInAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(this,
                    R.animator.field_scale_in);
            fieldScaleInAnimator.setTarget(view);
            fieldScaleInAnimator.start();
        }
    }

    private static void hideField(View... views) {
        for (View view : views) {
            view.setVisibility(View.GONE);
        }
    }

    public void cancelRegister(View view) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public void submit(View view) {
        hideField(usernameInput, sendCodeBtn, codeInput, passwordInput, passwordStatusView,
                passwordConfirmInput, cancelBtn, submitBtn);

        statusView.setText(getResources().getString(R.string.xcsdk_registering));
        statusView.setVisibility(View.VISIBLE);

        statusAnimatorSet = XcAndroidUtils.loadAnimatorSet(R.animator.trying, statusView, this);
        statusAnimatorSet.start();

        RegisterTask task = new RegisterTask(this);
        task.execute(userMobileNum, passwordInput.getText().toString());
    }

    /**
     * 发送验证码的任务
     * 
     * @author yaoyuan
     * 
     */
    private static class ResetSendCode implements Runnable {

        private final WeakReference<RegisterActivity> activityWeakRef;

        public ResetSendCode(RegisterActivity activity) {
            activityWeakRef = new WeakReference<RegisterActivity>(activity);
        }

        @Override
        public void run() {
            if (activityWeakRef != null) {
                activityWeakRef.get().resetSendCodeBtn();
            }

        }
    }

    /**
     * 发送验证码的异步任务
     * 
     * @author yaoyuan
     * 
     */
    private static class SendCodeTask extends AsyncTask<String, Void, Object> {

        private final WeakReference<RegisterActivity> activityWeakRef;

        public SendCodeTask(RegisterActivity activity) {
            activityWeakRef = new WeakReference<RegisterActivity>(activity);
        }

        @Override
        protected Object doInBackground(String... params) {
            String mobileNum = params[0];
            try {
                return XcServiceClient.sendMobileCode(mobileNum);
            } catch (RuntimeException e) {
                Log.e(LOG_TAG, "发送验证码失败", e);
                return e;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            RegisterActivity activity = activityWeakRef.get();
            if (activity == null) {
                return;
            }

            if (result instanceof Exception) {
                Toast.makeText(activity, R.string.xcsdk_send_code_error, Toast.LENGTH_LONG).show();
            } else {
                activity.secureCode = (String) result;
            }

        }
    }

    /**
     * 用户注册任务
     * 
     * @author yaoyuan
     * 
     */
    private static class RegisterTask extends AsyncTask<String, Void, Object> {

        private static final int FINISH_DELAY = 3000; // 3秒后关闭Activity

        private final WeakReference<RegisterActivity> activityWeakRef;

        public RegisterTask(RegisterActivity activity) {
            activityWeakRef = new WeakReference<RegisterActivity>(activity);
        }

        @Override
        protected Object doInBackground(String... params) {
            String mobileNum = params[0];
            String password = params[0];
            try {
                XcServiceClient.registerUser(mobileNum, password);
                return null;
            } catch (RuntimeException e) {
                Log.e(LOG_TAG, "注册失败", e);
                return e;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            RegisterActivity activity = activityWeakRef.get();
            if (activity == null) {
                return;
            }

            if (activity.statusAnimatorSet != null) {
                activity.statusAnimatorSet.cancel();
            }

            Intent responseIntent = new Intent();

            if (result instanceof Exception) {
                String msg = ((Exception) result).getMessage();
                Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                activity.statusView.setText(R.string.xcsdk_register_fail);

                activity.setResult(RESULT_CODE_FAIL, responseIntent);
            } else {
                activity.statusView.setText(R.string.xcsdk_register_success);

                responseIntent.putExtra(USERNAME, activity.userMobileNum);
                activity.setResult(RESULT_OK, responseIntent);
            }

            activity.statusView.postDelayed(new FinishActivity(activity), FINISH_DELAY);

        }
    }

    /**
     * 结束活动自己
     * 
     * @author yaoyuan
     * 
     */
    private static class FinishActivity implements Runnable {

        private final WeakReference<RegisterActivity> activityWeakRef;

        public FinishActivity(RegisterActivity activity) {
            activityWeakRef = new WeakReference<RegisterActivity>(activity);
        }

        @Override
        public void run() {
            if (activityWeakRef != null) {
                activityWeakRef.get().finish();
            }

        }
    }
}