package tv.xiaocong.sdk;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * {@link TextWatcher}的适配器类。
 */
public abstract class TextWatcherAdapter implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
}
