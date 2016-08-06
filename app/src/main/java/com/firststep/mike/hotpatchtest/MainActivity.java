package com.firststep.mike.hotpatchtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firststep.mike.hotpatchtest.patchManager.PatchManager;

import junit.framework.Test;

import java.lang.reflect.InvocationTargetException;

public class MainActivity extends Activity implements View.OnClickListener{

    private Button mBtnReplaceMethod;
    private Button mBtnReplaceClass;
    private Button mBtnReplaceString;
    private Button mBtnReplaceLayout;
    private Button mBtnReplaceSo;
    private Button mBtnRun;
    private TextView mTvShowResult;

    private String mString4Show;

    private TestClass mTestClass;
    private JavaMethodHooker mJavaMethodHooker;

    private PatchManager mPatchManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTestClass = new TestClass();
        mString4Show = mTestClass.getTestString();
        mJavaMethodHooker = new JavaMethodHooker();
        intiActivityView();

    }

    private void intiActivityView() {

        mBtnReplaceMethod = (Button) findViewById(R.id.btn_replace_method);
        mBtnReplaceMethod.setOnClickListener(this);
        mBtnReplaceClass = (Button) findViewById(R.id.btn_replace_class);
        mBtnReplaceMethod.setOnClickListener(this);
        mBtnReplaceString = (Button) findViewById(R.id.btn_replace_string);
        mBtnReplaceString.setOnClickListener(this);
        mBtnReplaceLayout = (Button) findViewById(R.id.btn_replace_layout);
        mBtnReplaceLayout.setOnClickListener(this);
        mBtnReplaceSo = (Button) findViewById(R.id.btn_replace_so);
        mBtnReplaceSo.setOnClickListener(this);
        mBtnRun = (Button) findViewById(R.id.btn_run);
        mBtnRun.setOnClickListener(this);
        mTvShowResult = (TextView) findViewById(R.id.tv_show_result);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_replace_method:
                /**
                 * 供测试使用，利用反射对方法进行替换（在同一个类中）
                 */
//                try {
//                    mString4Show = mJavaMethodHooker.simpleHook(mTestClass,"getTestString","getChangedString");
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
                /**
                 * 从补丁文件中执行方法替换
                 */
                mPatchManager = new PatchManager(this);
                mPatchManager.init();
                mPatchManager.loadPatch();

                break;
            case R.id.btn_replace_class:

                break;
            case R.id.btn_replace_string:

                break;
            case R.id.btn_replace_layout:

                break;
            case R.id.btn_run:
                mTvShowResult.setText(mString4Show);
                break;
            default:
                break;
        }
    }
}
