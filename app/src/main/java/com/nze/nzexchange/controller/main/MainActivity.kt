package com.nze.nzexchange.controller.main

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.leiyun.fingerprint.FingerprintDialog
import com.leiyun.fingerprint.FingerprintHelper
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.ActivityManager
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.Preferences
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.bibi.BibiFragment
import com.nze.nzexchange.controller.home.HomeFragment
import com.nze.nzexchange.controller.login.LoginActivity
import com.nze.nzexchange.controller.market.MarketFragment
import com.nze.nzexchange.controller.my.MyFragment
import com.nze.nzexchange.controller.otc.OtcFragment
import kotlinx.android.synthetic.main.activity_main.*
import net.grandcentrix.tray.AppPreferences

class MainActivity : NBaseActivity(), View.OnClickListener, NBaseFragment.OnFragmentInteractionListener {

    val mFragmentManager: FragmentManager by lazy { supportFragmentManager }
    var mCurrentTab = 0
    var mLastTab = 0
    var mLastFragment: NBaseFragment? = null
    //指纹解锁
    val mDialog: FingerprintDialog by lazy {
        FingerprintDialog().apply {
            isCancelable = false
        }
    }
    //share preferences
    val appPreferences: AppPreferences by lazy { AppPreferences(this) }

    /**
     * 是否退出应用
     */
    private var isExit: Boolean = false
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            isExit = false
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (mCurrentTab != 0) {
            mLastTab = mCurrentTab
            mCurrentTab = 0
            selectTab(mCurrentTab)
            return false
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                showToast("再按一次退出应用")
                isExit = true
                mHandler.sendEmptyMessageDelayed(0, 1000)
                return false
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
    }

    override fun onNetworkDisConnected() {
    }

    override fun getContainerTargetView(): View? = null

    override fun isBindEventBusHere(): Boolean = true

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        if (eventCenter.eventCode == EventCode.CODE_REFRESH_MAIN_ACT) {
            val i = eventCenter.data as Int
            mLastTab = mCurrentTab
            mCurrentTab = i
            selectTab(mCurrentTab)
        }
        if (eventCenter.eventCode == EventCode.CODE_LOGIN_SUCCUSS) {
            selectTab(mCurrentTab)
        }
        if (eventCenter.eventCode == EventCode.CODE_APP_TO_FRONT) {
            val pwdType = appPreferences.getInt(Preferences.COME_BACK_PWD, -1)
            if (pwdType == Preferences.BACK_PWD_FINGERPRINT) {
                if (FingerprintHelper.isHardwareDetected()) {
                    if (FingerprintHelper.hasEnrolledFingerprints()) {
                        mDialog.show(ActivityManager.instance.topActivity()?.supportFragmentManager, "dialog")
                    }
                }
            }
        }
        if (eventCenter.eventCode == EventCode.CODE_LOGIN_FAIL) {
            skipActivity(MainActivity::class.java)
            skipActivity(LoginActivity::class.java)
        }
    }

    override fun getOverridePendingTransitionMode(): TransitionMode = TransitionMode.RIGHT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasPermission()) {
            requestPermission()
        }
    }

    override fun getRootView(): Int = R.layout.activity_main

    override fun initView() {
        setWindowStatusBarColor(R.color.color_title_bg)
        tab_home_main.setOnClickListener(this)
        tab_market_main.setOnClickListener(this)
        tab_bibi_main.setOnClickListener(this)
        tab_otc_main.setOnClickListener(this)
        tab_my_main.setOnClickListener(this)
        selectTab(mCurrentTab)

        FingerprintHelper.init(this)//指纹解锁
    }

    override fun onClick(v: View?) {
        mLastTab = mCurrentTab
        when (v?.id) {
            R.id.tab_home_main -> mCurrentTab = 0
            R.id.tab_market_main -> mCurrentTab = 1
            R.id.tab_bibi_main -> mCurrentTab = 2
            R.id.tab_otc_main -> mCurrentTab = 3
            R.id.tab_my_main -> {
                mCurrentTab = 4
                if (!UserBean.isLogin()) {
                    skipActivity(LoginActivity::class.java)
                    return
                }
            }
            else -> {
            }
        }
        if (mLastTab != mCurrentTab) {
            selectTab(mCurrentTab)
        }
    }

    private fun selectTab(position: Int) {
//        mLastTab = position
//        mCurrentTab = mLastTab
        val childCount = layout_tab_main.getChildCount()
        for (i in 0 until childCount) {
            if (position != i) {
                layout_tab_main.getChildAt(i).setSelected(false)
            } else {
                layout_tab_main.getChildAt(i).setSelected(true)
            }
        }
        switchFragment(position)
    }

    fun switchFragment(id: Int) {
        val tag = id.toString()

        var transaction = mFragmentManager.beginTransaction()
        mLastFragment?.let { transaction.hide(mLastFragment!!) }
        var fragment: NBaseFragment? = mFragmentManager.findFragmentByTag(tag) as NBaseFragment?
        if (fragment == null) {
            when (id) {
                0 -> fragment = HomeFragment.newInstance()
                1 -> fragment = MarketFragment.newInstance()
                2 -> fragment = BibiFragment.newInstance()
                3 -> fragment = OtcFragment.newInstance()
                4 -> fragment = MyFragment.newInstance()
                else -> {
                    fragment = HomeFragment.newInstance()
                }
            }
            transaction.add(R.id.content_main, fragment, tag)
        } else {
            transaction.show(fragment)
        }
        mLastFragment = fragment as NBaseFragment
        transaction.commitAllowingStateLoss()
    }


    override fun onFragmentInteraction(uri: Uri) {
    }

    val PERMISSION_REQUEST_CODE = 0x99
    //进入app需加载的权限
    fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.USE_FINGERPRINT), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "已经获取指纹权限", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "已拒绝取指纹权限", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
