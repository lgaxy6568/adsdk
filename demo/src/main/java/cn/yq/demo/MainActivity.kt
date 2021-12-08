package cn.yq.demo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import cn.yq.ad.util.AdLogUtils
import com.bytedance.tools.ui.ToolsActivity

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testCalc()
    }

    private fun testCalc(){
        AdLogUtils.i(TAG,"testCalc()")
//        GlobalScope.launch {
//            withContext(Dispatchers.IO){
//                val item1 = AdRespItem(30,1,AdConstants.SDK_TYPE_BY_SDK,AdConstants.PARTNER_KEY_BY_TT)
//                val item2 = AdRespItem(70,1,AdConstants.SDK_TYPE_BY_SDK,AdConstants.PARTNER_KEY_BY_GDT)
//                val mmp = mutableMapOf<String,Int>()
//                for (index in 1..100) {
//                    try {
//                        val lst = mutableListOf<AdRespItem>()
//                        lst.add(item1)
//                        lst.add(item2)
//                        lst.shuffle()
//                        val rr = AdvProxyAbstract.sortLst(lst)
//
//                        val sb = StringBuilder()
//                        sb.append("[")
//                        rr.forEach { ad->
//                            sb.append(ad.adPartnerKey).append(",")
//                        }
//                        sb.append("]")
//                        val key = sb.toString()
//                        if(mmp.containsKey(key)){
//                            mmp.put(key,mmp[key]!!+1)
//                        }else{
//                            mmp.put(key,1)
//                        }
//
//                    } catch (e: Exception) {
//                        AdLogUtils.e(TAG,e.message)
//                    }
//                }
//                mmp.forEach { ent->
//                    AdLogUtils.i(TAG,"${ent.key}=${ent.value}")
//                }
//
//            }
//        }
    }

    fun handBtnByCheckCSJ(v:View){
        startActivity(Intent(this,ToolsActivity::class.java))
    }
    fun handBtnBySplashAD(v:View){
        Intent(this,SplashAdActivity::class.java).let {
            startActivity(it)
        }
    }

    fun handBtnByRewardAD(v:View){
        Intent(this,RewardAdActivity::class.java).let {
            startActivity(it)
        }
    }

    /**
     * 小满 自渲染广告
     */
    fun handBtnByRenderAD_XM(v:View){
        Intent(this, XmRenderAdActivity::class.java).let {
            startActivity(it)
        }
    }
}