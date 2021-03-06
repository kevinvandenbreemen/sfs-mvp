package com.vandenbreemen.mobilesecurestoragemvp.wrapper

import com.vandenbreemen.mobilesecurestorage.file.api.FileTypes
import com.vandenbreemen.mobilesecurestoragemvp.wrapper.error.RepositoryRuntime
import com.vandenbreemen.sfs_test_utils.SFSTestingUtils
import junit.framework.TestCase.*
import org.junit.Assert.assertArrayEquals
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * @author kevin
 */
class DefaultStorageRepositoryTest {

    lateinit var repository: StorageRepository

    @Before
    fun setup() {
        val testFile = SFSTestingUtils.getTestFile("storageRepoTests_${System.nanoTime()}")
        val sfs = SFSTestingUtils.getNewSecureFileSystem(testFile)
        repository = DefaultStorageRepository(sfs)
    }

    @Test
    fun `should store a data file`() {
        repository.store("test1", "This is a test")
        val storedData = repository.load("test1")
        assertEquals("This is a test", storedData)
    }

    @Test
    fun `should store byte array`() {
        val byteArray: ByteArray = "Hello world".toByteArray()
        repository.storeBytes("test1", byteArray, null)
        val storedData = repository.loadBytes("test1")

        assertArrayEquals(storedData, "Hello world".toByteArray())
    }

    @Test
    fun `should update the contents of a file`() {
        repository.store("test1", "This is a test")
        repository.store("test1", "UPDATE")
        val storedData = repository.load("test1")
        assertEquals("UPDATE", storedData)
    }

    @Test
    fun `should update byte array in same file`() {
        val byteArray: ByteArray = "Hello world".toByteArray()
        repository.storeBytes("test1", byteArray, null)
        repository.storeBytes("test1", "updated".toByteArray(), null)
        val storedData = repository.loadBytes("test1")

        assertArrayEquals(storedData, "updated".toByteArray())
    }

    @Test
    fun `should store byte array with file type`() {
        val byteArray: ByteArray = "Hello world".toByteArray()
        repository.storeBytes("test1", byteArray, FileTypes.SYSTEM)
        repository.store("test2", "This is a test")

        assertEquals(1, repository.lsc(FileTypes.SYSTEM))
        assertEquals(2, repository.lsc())

        val storedData = repository.loadBytes("test1")

        assertArrayEquals(storedData, "Hello world".toByteArray())
    }

    @Test
    fun `should list files`() {
        repository.store("test1", "This is a test")
        val byteArray: ByteArray = "Hello world".toByteArray()
        repository.storeBytes("test2", byteArray, null)

        val fileNames = repository.ls()

        assertEquals(2, fileNames.size)
        assertTrue(fileNames.contains("test1"))
        assertTrue(fileNames.contains("test2"))
    }

    @Test
    fun `should get number of files`() {
        repository.store("test1", "This is a test")
        val byteArray: ByteArray = "Hello world".toByteArray()
        repository.storeBytes("test2", byteArray, null)

        assertEquals(2, repository.lsc())
    }

    @Test
    fun `should rename files`() {
        repository.store("test1", "This is a test")
        repository.mv("test1", "renamed")
        val storedData = repository.load("renamed")
        assertEquals("This is a test", storedData)
    }

    @Test
    fun `should store with file type`() {
        repository.store("test1", "This is a test", FileTypes.DATA)
        repository.store("test2", "This is a test")

        val files = repository.ls(FileTypes.DATA)
        assertEquals(1, files.size)
        assertTrue(files.contains("test1"))

        val storedData = repository.load("test1")
        assertEquals("This is a test", storedData)

        assertEquals(1, repository.lsc(FileTypes.DATA))
    }

    @Test
    fun `should delete files`() {
        repository.store("test2", "This is a test")
        repository.delete("test2")

        assertEquals(0, repository.lsc())
    }

    @Test
    fun `should delete multiple files`() {
        repository.store("test2", "This is a test")
        repository.store("test1", "This is a test")
        repository.delete("test2", "test1")

        assertEquals(0, repository.lsc())
    }

    @Test
    fun `should get file info`() {
        repository.store("test2", "This is a test")
        val info = repository.stat("test2")

        assertEquals("test2", info.fileName)
        assertNotNull(info.createDate)
        assertEquals(1, info.size)
    }

    @Test
    fun `should import data into files`() {
        val path = "src/test/resource/testdata.dat"
        val testFile = File(path)
        assertTrue(testFile.exists())

        repository.import(path, "test1")

        val data = repository.load("test1")
        assertTrue(data is ByteArray)

        val string = String(data as ByteArray)
        assertEquals("THIS IS A TEST", string)
    }

    @Test
    fun `should load byte data from imported files using loadBytes()`() {
        val path = "src/test/resource/testdata.dat"
        val testFile = File(path)
        assertTrue(testFile.exists())

        repository.import(path, "test1")

        val data = repository.loadBytes("test1")

        val string = String(data!!)
        assertEquals("THIS IS A TEST", string)
    }

    @Test
    fun `should let you import files with a file type`() {
        val path = "src/test/resource/testdata.dat"
        val testFile = File(path)
        assertTrue(testFile.exists())

        repository.import(path, "test1", FileTypes.DATA)
        repository.store("test2", "This is a test")
        assertEquals(1, repository.lsc(FileTypes.DATA))

        val data = repository.loadBytes("test1")

        val string = String(data!!)
        assertEquals("THIS IS A TEST", string)

    }

    @Test
    fun `should check if file exists`() {
        assertFalse(repository.f("test1"))
        repository.store("test1", "This is a test", FileTypes.DATA)
        assertTrue(repository.f("test1"))
    }

    @Test
    fun `Should unmount`() {
        repository.store("test1", "This is a test")
        repository.unmount()

        try {
            repository.lsc()
            fail("Repository unmounted")
        } catch(rex: RepositoryRuntime){
            rex.printStackTrace()
        }
    }

}