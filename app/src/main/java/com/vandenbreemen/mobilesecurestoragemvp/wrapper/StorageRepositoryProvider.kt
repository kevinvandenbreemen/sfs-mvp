package com.vandenbreemen.mobilesecurestoragemvp.wrapper

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem

/**
 *
 * @author kevin
 */
interface StorageRepositoryProvider {

    fun getRepository(credentials: SFSCredentials): StorageRepository

    /**
     * Unmounts any storage repositories this provider may have mounted.  This will render them un-usable.
     */
    fun destroy()

}

class DefaultStorageRepositoryProvider: StorageRepositoryProvider {

    private var repository: StorageRepository? = null

    override fun getRepository(credentials: SFSCredentials): StorageRepository {

        repository?.let {
            return it
        } ?: run {
            val sfs = object: SecureFileSystem(credentials.fileLocation) {
                override fun getPassword(): SecureString {
                    return credentials.password
                }
            }
            repository = DefaultStorageRepository(sfs)
            return repository!!
        }


    }

    override fun destroy() {
        repository?.apply {
            unmount()
            repository = null
        }
    }
}