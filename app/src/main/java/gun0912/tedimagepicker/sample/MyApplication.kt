package gun0912.tedimagepicker.sample

import android.app.Application

import com.squareup.leakcanary.LeakCanary

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setLeakCanary()
    }

    private fun setLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
    }
}
