package uz.smd.retsept.app

import android.app.Application
import uz.smd.retsept.data.NetworkModule
import uz.smd.retsept.data.Prefs


class App : Application() {


    override fun onCreate() {
        super.onCreate()
        NetworkModule.init()
        Prefs.init(applicationContext)
    }

}