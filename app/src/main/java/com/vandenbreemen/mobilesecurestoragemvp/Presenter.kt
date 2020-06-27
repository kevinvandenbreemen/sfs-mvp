package com.vandenbreemen.mobilesecurestoragemvp

import com.vandenbreemen.mobilesecurestorage.message.ApplicationError

/**
 * Presenter.  This is the fundamental orchestrator in the MVP system
 * @author kevin
 */
abstract class Presenter<MODEL: Model, VIEW: View>(private val model: MODEL, private var view: VIEW? = null) {

    /**
     * Sets the view on this presenter.  Only call this method before calling [start]
     */
    fun setView(view: VIEW) {
        this.view = view
    }

    protected fun getView(): VIEW? {
        return this.view
    }

    /**
     * Starts up the logic in this presenter.  This method will also initialize needed model and by
     * extension any subsystems it needs
     */
    fun start() {
        try {
            model.init()
            setupView()
        } catch (exception: Exception) {
            view?.showError(ApplicationError("Unexpected Error Occurred"))
        }
    }

    /**
     * Once the view is #onReadyToUse, call any API on the view required for it to initialize further details etc.  For
     * example this could be adding a title to a screen based on the title on an accessed record.  Do NOT call view.readyToUse() from
     * this method!
     */
    protected abstract fun setupView()

    fun close() {
        model.close()
    }

}