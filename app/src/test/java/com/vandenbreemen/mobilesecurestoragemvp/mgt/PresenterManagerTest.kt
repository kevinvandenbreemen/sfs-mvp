package com.vandenbreemen.mobilesecurestoragemvp.mgt

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.mobilesecurestoragemvp.Presenter
import com.vandenbreemen.mobilesecurestoragemvp.TestModel
import com.vandenbreemen.mobilesecurestoragemvp.TestPresenter
import com.vandenbreemen.mobilesecurestoragemvp.TestView
import com.vandenbreemen.sfs_test_utils.SFSTestingUtils
import org.junit.Test

import org.junit.Assert.*
import java.lang.RuntimeException

class TestPresenterManager: PresenterManager() {

    override fun buildPresenters(credentials: SFSCredentials) {
        val testPresenter = TestPresenterWithNoView(TestModel(credentials))
        addPresenter(testPresenter)
    }
}

class TestPresenterWithNoView(private val model: TestModel): Presenter<TestModel, TestView>(model) {

    override fun setupView() {
        getView()?.showFiles(model.listFiles())
    }
}

/**
 * @author kevin
 */
class PresenterManagerTest {

    @Test
    fun `Sanity test Building Presenters`() {

        //  Arrange
        val file = SFSTestingUtils.getTestFile("testFile")
        val sfs = SFSTestingUtils.getNewSecureFileSystem(file)
        sfs.touch("testFile1")
        val credentials = SFSCredentials(file,
            SecureFileSystem.generatePassword(SecureString("password123".toByteArray())))
        val manager = TestPresenterManager()

        //  Act
        manager.buildPresenters(credentials)
    }

    @Test
    fun `Get Presenter of a Specific Type`() {
        //  Arrange
        val file = SFSTestingUtils.getTestFile("testFile")
        val sfs = SFSTestingUtils.getNewSecureFileSystem(file)
        sfs.touch("testFile1")
        val credentials = SFSCredentials(file,
            SecureFileSystem.generatePassword(SecureString("password123".toByteArray())))
        val manager = TestPresenterManager()
        manager.buildPresenters(credentials)

        //  Act
        val presenter: TestPresenterWithNoView = manager.getPresenter()

        //  Assert
        assertNotNull(presenter)

    }

    @Test(expected = RuntimeException::class)
    fun `Throws an Error when Requesting a Type that is Not Supported`() {
        //  Arrange
        val file = SFSTestingUtils.getTestFile("testFile")
        val sfs = SFSTestingUtils.getNewSecureFileSystem(file)
        sfs.touch("testFile1")
        val credentials = SFSCredentials(file,
            SecureFileSystem.generatePassword(SecureString("password123".toByteArray())))
        val manager = TestPresenterManager()
        manager.buildPresenters(credentials)

        //  Act
        val presenter: TestPresenter = manager.getPresenter()

        //  Assert
        fail("Should not have gotten here")
    }
}