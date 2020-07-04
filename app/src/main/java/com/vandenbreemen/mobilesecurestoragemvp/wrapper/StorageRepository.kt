package com.vandenbreemen.mobilesecurestoragemvp.wrapper

import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import java.io.Serializable

/**
 * Abstraction from on-disk storage
 * @author kevin
 */
interface StorageRepository {
    fun store(fileName: String, data: Serializable)
    fun load(fileName: String): Any
}

class DefaultStorageRepository(private val secureFileSystem: SecureFileSystem) : StorageRepository {
    override fun store(fileName: String, data: Serializable) {
        secureFileSystem.storeObject(fileName, data)
    }

    override fun load(fileName: String): Any {
        return secureFileSystem.loadFile(fileName)
    }


}