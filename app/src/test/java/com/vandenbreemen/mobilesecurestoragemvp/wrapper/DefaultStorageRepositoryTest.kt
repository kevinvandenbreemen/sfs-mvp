package com.vandenbreemen.mobilesecurestoragemvp.wrapper

import com.vandenbreemen.mobilesecurestoragemvp.wrapper.error.RepositoryRuntime
import com.vandenbreemen.sfs_test_utils.SFSTestingUtils
import junit.framework.TestCase.*
import org.junit.Assert.assertArrayEquals
import org.junit.Before
import org.junit.Test

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
        repository.storeBytes("test1", byteArray)
        val storedData = repository.loadBytes("test1")

        assertArrayEquals(storedData, "Hello world".toByteArray())
    }

    @Test
    fun `should list files`() {
        repository.store("test1", "This is a test")
        val byteArray: ByteArray = "Hello world".toByteArray()
        repository.storeBytes("test2", byteArray)

        val fileNames = repository.ls()

        assertEquals(2, fileNames.size)
        assertTrue(fileNames.contains("test1"))
        assertTrue(fileNames.contains("test2"))
    }

    @Test
    fun `should get number of files`() {
        repository.store("test1", "This is a test")
        val byteArray: ByteArray = "Hello world".toByteArray()
        repository.storeBytes("test2", byteArray)

        assertEquals(2, repository.lsc())
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