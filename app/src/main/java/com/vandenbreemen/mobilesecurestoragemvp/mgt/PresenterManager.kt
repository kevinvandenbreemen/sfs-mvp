package com.vandenbreemen.mobilesecurestoragemvp.mgt

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestoragemvp.Presenter
import com.vandenbreemen.mobilesecurestoragemvp.wrapper.DefaultStorageRepositoryProvider
import com.vandenbreemen.mobilesecurestoragemvp.wrapper.StorageRepositoryProvider

/**
 * Builds and manages presenters, deleting and re-creating them etc.  Use this type whenever possible
 * to abstract yourself away from directly creating or destroying presenters in your activities, view
 * models, fragments, etc.
 * @author kevin
 */
abstract class PresenterManager {

    @PublishedApi
    internal val presenters: MutableList<Presenter<*, *>> = mutableListOf()

    private var storageRepositoryProvider: StorageRepositoryProvider = DefaultStorageRepositoryProvider()

    fun build(credentials: SFSCredentials) {
        buildPresenters(credentials, storageRepositoryProvider)
    }

    /**
     * Create the presenters this manager manages
     */
    abstract fun buildPresenters(credentials: SFSCredentials, provider: StorageRepositoryProvider)

    /**
     * Adds the given presenter to the list of presenters being managed
     */
    protected fun addPresenter(presenter: Presenter<*, *>) {
        presenters.add(presenter)
    }

    inline fun <reified P: Presenter<*,*>> getPresenter(): P {
        try{
            return this.presenters.first { p->p.javaClass == P::class.java } as P
        } catch (nse: NoSuchElementException) {
            throw RuntimeException("Presenter type ${P::class.simpleName} not supported by this ${this.javaClass.simpleName}")
        }
    }

    fun destroy() {
        presenters.forEach { it.close() }
        presenters.clear()
        storageRepositoryProvider.destroy()
    }

}