package com.vandenbreemen.mobilesecurestoragemvp.wrapper

import com.vandenbreemen.mobilesecurestorage.file.ImportedFileData
import com.vandenbreemen.mobilesecurestorage.file.api.FileType
import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import com.vandenbreemen.mobilesecurestorage.file.api.SecureFileSystemInteractor
import com.vandenbreemen.mobilesecurestorage.file.api.SecureFileSystemInteractorFactory
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.mobilesecurestoragemvp.wrapper.error.RepositoryRuntime
import java.io.Serializable
import java.util.*

/**
 * Abstraction from on-disk storage
 * @author kevin
 */
interface StorageRepository {
    fun store(fileName: String, data: Serializable, fileType: FileType? = null)
    fun load(fileName: String): Any
    fun storeBytes(fileName: String, byteArray: ByteArray, fileType: FileTypes? = null)
    fun loadBytes(fileName: String): ByteArray?
    fun ls(fileType: FileType? = null): List<String>
    fun lsc(fileType: FileTypes? = null): Int

    /**
     * Unmount the file system.  The StorageRepository will no longer be usable!
     */
    fun unmount()
    fun mv(currentName: String, newName: String)
    fun delete(vararg fileNames: String)

}

class DefaultStorageRepository(private var secureFileSystem: SecureFileSystem?) : StorageRepository {

    private var interactor: SecureFileSystemInteractor = SecureFileSystemInteractorFactory.get(secureFileSystem!!)

    @Throws(RepositoryRuntime::class)
    private fun checkMounted() {
        if(secureFileSystem == null) {
            throw RepositoryRuntime("File system no longer mounted")
        }
    }

    override fun store(fileName: String, data: Serializable, fileType: FileType?) {
        checkMounted()

        fileType?.let { ft->
            interactor.save(data, fileName, ft)
            return
        }

        secureFileSystem!!.storeObject(fileName, data)
    }

    override fun load(fileName: String): Any {
        checkMounted()
        return secureFileSystem!!.loadAndCacheFile(fileName)
    }

    override fun storeBytes(
        fileName: String,
        byteArray: ByteArray,
        fileType: FileTypes?
    ) {
        checkMounted()
        val importedData = ImportedFileData(byteArray)

        fileType?.let { ft->
            interactor.save(importedData, fileName, ft)
            return
        }

        secureFileSystem!!.storeObject(fileName, importedData)
    }

    override fun loadBytes(fileName: String): ByteArray? {
        checkMounted()
        return secureFileSystem!!.loadAndCacheBytesFromFile(fileName)
    }

    override fun ls(fileType: FileType?): List<String> {
        checkMounted()

        fileType?.let { ft->
            return secureFileSystem!!.listFiles().filter { fileName->
                val details = secureFileSystem!!.getDetails(fileName)
                details.fileMeta?.let { meta->
                    ft == meta.getFileType()
                } ?: false

            }
        }

        return Collections.unmodifiableList(secureFileSystem!!.listFiles())
    }

    override fun lsc(fileType: FileTypes?): Int {
        checkMounted()
        return ls(fileType).count()
    }

    override fun unmount() {
        secureFileSystem!!.close()
        secureFileSystem = null
    }

    override fun mv(currentName: String, newName: String) {
        checkMounted()
        secureFileSystem!!.rename(currentName, newName)
    }

    override fun delete(vararg fileNames: String) {
        checkMounted()
        secureFileSystem!!.deleteFiles(*fileNames)
    }
}