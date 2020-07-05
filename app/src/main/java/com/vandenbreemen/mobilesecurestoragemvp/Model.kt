package com.vandenbreemen.mobilesecurestoragemvp

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestoragemvp.wrapper.StorageRepository
import com.vandenbreemen.mobilesecurestoragemvp.wrapper.StorageRepositoryProvider
import com.vandenbreemen.standardandroidlogging.log.SystemLog

abstract class Model(private val credentials: SFSCredentials, private val provider: StorageRepositoryProvider) {

    protected lateinit var storage:StorageRepository

    @Throws
    fun init() {
        try {
            this.storage = provider.getRepository(credentials)

            this.setup()
        } catch (exception: Exception) {
            SystemLog.get().error(javaClass.simpleName, "Failed to load SFS", exception)
            throw exception
        }
    }

    fun close() {
        if (isClosed()) {
            return
        }
        credentials.finalize()

        onClose()
    }

    /**
     * Any additional logic you'd like to perform after the model has been closed
     */
    abstract fun onClose()

    /**
     * Create a copy of the credentials used to create this model.
     */
    fun copyCredentials(): SFSCredentials {
        return credentials.copy()
    }

    fun isClosed(): Boolean {
        return credentials.finalized()
    }

    /**
     * Do any setup necessary for the model to work.  This method is called once the SFS has been initialized
     */
    protected abstract fun setup()
}