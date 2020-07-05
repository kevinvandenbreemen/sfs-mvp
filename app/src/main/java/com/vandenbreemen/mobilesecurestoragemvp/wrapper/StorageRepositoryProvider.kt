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
}