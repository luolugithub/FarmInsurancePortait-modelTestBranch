package com.innovation.login;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

import org.tensorflow.demo.env.Logger;

import static com.innovation.login.IDCardValidate.validateIDcardNumber;

/**
 * Created by luolu on 08/01/2018.
 */

public class InputValidation {
    private static Logger logger = new Logger(InputValidation.class.getName());
    private static Context context;

    public InputValidation(Context context) {
        this.context = context;
    }

    public static boolean isInputEditTextFilled(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message) {

        String value = textInputEditText.getText().toString().trim();
        if (value.isEmpty()) {
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isInputChineseEditTextFilled(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message) {

        String value = textInputEditText.getText().toString().trim();
        if (value.isEmpty() || (!checkNameChese(value))) {
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobile(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message) {
    /*
    移动：134、135、136、137、138、139、150、151、152、157(TD)、158、159、178(新)、182、184、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、170、173、177、180、181、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String num = "[1][34578]\\d{9}";//"[1]"代表第1位为数字1，"[34578]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        String value = textInputEditText.getText().toString().trim();
        if (value.isEmpty() || !value.matches(num)) {
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }
// else if (textInputEditText.toString().matches(num)){
//            //matches():字符串是否在给定的正则表达式匹配
////            return textInputEditText.toString().matches(num);
//           return true;
//        }else {
//            textInputLayout.setErrorEnabled(false);
//        }

//        if (!value.matches(num)){
//            textInputLayout.setError(message);
//            hideKeyboardFrom(textInputEditText);
//        }
        return true;
    }
    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
        /*
         * 移动:134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
         * 联通:130、131、132、152、155、156、185、186 电信:133、153、180、189、(1349卫通)
         * 总结起来就是第一位必定为1,第二位必定为3或5或8,其他位置的可以为0-9
         */
        String telRegex = "[1][3456789]//d{9}";// "[1]"代表第1位为数字1,"[358]"代表第二位可以为3、5、8中的一个,"//d{9}"代表后面是可以是0~9的数字,有9位。
        if (TextUtils.isEmpty(mobiles))
            return false;
        else
            return mobiles.matches(telRegex);
    }
    public boolean isInputPhoneNumberFilled(AutoCompleteTextView textInputEditText, TextInputLayout textInputLayout, String message) {

        String value = textInputEditText.getText().toString().trim();
        if (value.isEmpty()) {
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isInputSmscodeValid(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message) {

        String value = textInputEditText.getText().toString().trim();
        if (value.length() < 4) {
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isvalidateIDcardNumber(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message) {

        boolean isreturn_AutoCard = false;
        String value = validateIDcardNumber(textInputEditText.getText().toString().trim(), true);
        logger.i("validateIDcardNumber value: " + value);
        if (value == "身份证长度必须为15或者18位！") {
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isInputEditTextUserName(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message) {

        String value = textInputEditText.getText().toString().trim();
//        if(value.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(value).matches()){
        if (value.isEmpty()) {
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;


    }

    public boolean isInputEditTextMatches(TextInputEditText textInputEditText, TextInputEditText textInputEditText2, TextInputLayout textInputLayout, String message) {
        String value1 = textInputEditText.getText().toString().trim();
        String value2 = textInputEditText2.getText().toString().trim();

        if (!value1.contentEquals(value2)) {
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText2);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private static void hideKeyboardFrom(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * 判定输入汉字
     * @param c
     * @return
     */
    public  boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 检测String是否全是中文
     * @param name
     * @return
     */
    public  boolean checkNameChese(String name)
    {
        boolean res=true;
        char [] cTemp = name.toCharArray();
        for(int i=0;i<name.length();i++)
        {
            if(!isChinese(cTemp[i]))
            {
                res=false;
                break;
            }
        }
        return res;
    }

}
