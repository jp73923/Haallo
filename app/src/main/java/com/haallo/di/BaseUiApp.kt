package com.haallo.di

import android.app.Application
import com.haallo.ui.home.HomeActivity
import com.haallo.ui.newchat.NewChatActivity

/**
 *
 * This base app class will be extended by either Main or Demo project.
 *
 * It then will provide library project app component accordingly.
 *
 */
abstract class BaseUiApp : Application() {
    abstract fun getAppComponent(): BaseAppComponent
    abstract fun setAppComponent(baseAppComponent: BaseAppComponent)
}

/**
 * Base app component
 *
 * This class should have all the inject targets classes
 *
 */
interface BaseAppComponent {
    fun inject(app: Application)
    fun inject(homeActivity: HomeActivity)
    fun inject(newChatActivity: NewChatActivity)
}

/**
 * Extension for getting component more easily
 */
fun BaseUiApp.getComponent(): BaseAppComponent {
    return this.getAppComponent()
}
