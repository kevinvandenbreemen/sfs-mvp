package com.vandenbreemen.mobilesecurestoragemvp.wrapper

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.sfs_test_utils.SFSTestingUtils
import junit.framework.TestCase.fail
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author kevin
 */
class DefaultStorageRepositoryProviderTest {

    @Test
    fun `should build secure storage repository`() {
        val file = SFSTestingUtils.getTestFile("testFile")
        val sfs = SFSTestingUtils.getNewSecureFileSystem(file)
        sfs.touch("testFile1")
        val credentials = SFSCredentials(file,
            SecureFileSystem.generatePassword(SecureString("password123".toByteArray())))

        val opened = DefaultStorageRepositoryProvider().getRepository(credentials)
        assertEquals(1, opened.lsc())
    }

    @Test
    fun `should re-use existing SFS`() {
        val file = SFSTestingUtils.getTestFile("testFile")
        val sfs = SFSTestingUtils.getNewSecureFileSystem(file)
        sfs.touch("testFile1")
        val credentials = SFSCredentials(file,
            SecureFileSystem.generatePassword(SecureString("password123".toByteArray())))

        val provider = DefaultStorageRepositoryProvider()

        val opened = provider.getRepository(credentials)
        val reOpened = provider.getRepository(credentials)
        opened.store("test2", "This is a test")

        assertEquals(2, reOpened.lsc())
    }

    @Test
    fun `should unmount any mounted SFSs`() {
        val file = SFSTestingUtils.getTestFile("testFile")
        val sfs = SFSTestingUtils.getNewSecureFileSystem(file)
        sfs.touch("testFile1")
        val credentials = SFSCredentials(file,
            SecureFileSystem.generatePassword(SecureString("password123".toByteArray())))

        val provider: StorageRepositoryProvider = DefaultStorageRepositoryProvider()

        val opened = provider.getRepository(credentials)

        provider.destroy()

        try {
            opened.ls()
            fail("Should not be allowed")
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}