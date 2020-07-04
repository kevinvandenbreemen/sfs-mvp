package com.vandenbreemen.mobilesecurestoragemvp.wrapper

import com.vandenbreemen.mobilesecurestorage.file.ImportedFileData
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.mobilesecurestoragemvp.wrapper.error.RepositoryRuntime
import java.io.Serializable
import java.util.*

/**
 * Abstraction from on-disk storage
 * @author kevin
 */
interface StorageRepository {
    fun store(fileName: String, data: Serializable)
    fun load(fileName: String): Any
    fun storeBytes(fileName: String, byteArray: ByteArray)
    fun loadBytes(fileName: String): ByteArray?
    fun ls(): List<String>
    fun lsc(): Int

    /**
     * Unmount the file system.  The StorageRepository will no longer be usable!
     */
    fun unmount()

}

class DefaultStorageRepository(private var secureFileSystem: SecureFileSystem?) : StorageRepository {

    @Throws(RepositoryRuntime::class)
    private fun checkMounted() {
        if(secureFileSystem == null) {
            throw RepositoryRuntime("File system no longer mounted")
        }
    }

    override fun store(fileName: String, data: Serializable) {
        checkMounted()
        secureFileSystem!!.storeObject(fileName, data)
    }

    override fun load(fileName: String): Any {
        checkMounted()
        return secureFileSystem!!.loadAndCacheFile(fileName)
    }

    override fun storeBytes(fileName: String, byteArray: ByteArray) {
        checkMounted()
        val importedData = ImportedFileData(byteArray)
        secureFileSystem!!.storeObject(fileName, importedData)
    }

    override fun loadBytes(fileName: String): ByteArray? {
        checkMounted()
        return secureFileSystem!!.loadAndCacheBytesFromFile(fileName)
    }

    override fun ls(): List<String> {
        checkMounted()
        return Collections.unmodifiableList(secureFileSystem!!.listFiles())
    }

    override fun lsc(): Int {
        checkMounted()
        return secureFileSystem!!.listFiles().count()
    }

    override fun unmount() {
        secureFileSystem!!.close()
        secureFileSystem = null
    }
}