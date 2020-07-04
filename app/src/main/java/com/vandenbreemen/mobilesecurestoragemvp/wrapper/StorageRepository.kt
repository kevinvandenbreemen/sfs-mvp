package com.vandenbreemen.mobilesecurestoragemvp.wrapper

import com.vandenbreemen.mobilesecurestorage.file.ImportedFileData
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import java.io.Serializable

/**
 * Abstraction from on-disk storage
 * @author kevin
 */
interface StorageRepository {
    fun store(fileName: String, data: Serializable)
    fun load(fileName: String): Any
    fun storeBytes(fileName: String, byteArray: ByteArray)
    fun loadBytes(fileName: String): ByteArray?

}

class DefaultStorageRepository(private val secureFileSystem: SecureFileSystem) : StorageRepository {
    override fun store(fileName: String, data: Serializable) {
        secureFileSystem.storeObject(fileName, data)
    }

    override fun load(fileName: String): Any {
        return secureFileSystem.loadAndCacheFile(fileName)
    }

    override fun storeBytes(fileName: String, byteArray: ByteArray) {
        val importedData = ImportedFileData(byteArray)
        secureFileSystem.storeObject(fileName, importedData)
    }

    override fun loadBytes(fileName: String): ByteArray? {
        return secureFileSystem.loadAndCacheBytesFromFile(fileName)
    }
}