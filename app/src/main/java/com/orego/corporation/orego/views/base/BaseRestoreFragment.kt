package com.orego.corporation.orego.views.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orego.corporation.orego.BuildConfig

abstract class BaseRestoreFragment : Fragment() {
    private lateinit var mRootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) {
            Log.d(javaClass.simpleName, "onCreate:")
        }
        if (savedInstanceState != null && fragmentManager != null) {
            val isShow = savedInstanceState.getBoolean(IS_SHOW, true)
            val ft = fragmentManager!!.beginTransaction()
            if (BuildConfig.DEBUG) {
                Log.d(this.javaClass.simpleName, "restore show:$isShow")
            }
            if (!isShow) {
                ft.hide(this)
            } else {
                ft.show(this)
            }
            ft.commit()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_SHOW, !isHidden)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (BuildConfig.DEBUG) {
            Log.d(javaClass.simpleName, "onCreateView:")
        }
        mRootView = onCreateContentView(inflater, container, savedInstanceState)
        return mRootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (BuildConfig.DEBUG) {
            Log.v(javaClass.simpleName, "onViewCreated: ")
        }
        initView(view, savedInstanceState)
        initData(savedInstanceState)
    }

    override fun onStop() {
        super.onStop()
        if (BuildConfig.DEBUG) {
            Log.d(javaClass.simpleName, "onStop: ")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (BuildConfig.DEBUG) {
            Log.d(javaClass.simpleName, "onDestroyView ")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (BuildConfig.DEBUG) {
            Log.d(javaClass.simpleName, "onDestroy: ")
        }
    }

    protected abstract fun onCreateContentView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View

    protected abstract fun initView(root: View, savedInstanceState: Bundle?)

    protected abstract fun initData(savedInstanceState: Bundle?)

    companion object {
        val IS_SHOW = "is_show"
    }
}
