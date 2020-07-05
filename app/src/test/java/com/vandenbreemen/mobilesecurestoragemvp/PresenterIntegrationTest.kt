package com.vandenbreemen.mobilesecurestoragemvp

import com.vandenbreemen.mobilesecurestorage.android.sfs.SFSCredentials
import com.vandenbreemen.mobilesecurestorage.message.ApplicationError
import com.vandenbreemen.mobilesecurestorage.security.SecureString
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem
import com.vandenbreemen.mobilesecurestoragemvp.mgt.TestPresenterWithNoView
import com.vandenbreemen.mobilesecurestoragemvp.wrapper.DefaultStorageRepositoryProvider
import com.vandenbreemen.mobilesecurestoragemvp.wrapper.StorageRepositoryProvider
import com.vandenbreemen.sfs_test_utils.SFSTestingUtils
import junit.framework.TestCase.*
import org.junit.Test

class TestView: View {

    lateinit var sfsFileList: List<String>
    var error: ApplicationError? = null

    override fun showError(error: ApplicationError) {
        this.error = error
    }

    fun showFiles(files: List<String>) {
        sfsFileList = files
    }

}

class TestModel(credentials: SFSCredentials, storageRepositoryProvider: StorageRepositoryProvider): Model(credentials, storageRepositoryProvider) {
    override fun onClose() {

    }

    override fun setup() {

    }

    fun listFiles(): List<String> {
        return storage.ls()
    }

}

class TestPresenter(private val model: TestModel, private val view: TestView): Presenter<TestModel, TestView>(model, view) {
    override fun setupView() {
        view.showFiles(model.listFiles())
    }
}

/**
 *
 * @author kevin
 */
class PresenterIntegrationTest {

    @Test
    fun `Startup Functions Properly`() {
        val file = SFSTestingUtils.getTestFile("testFile_${System.nanoTime()}")
        val sfs = SFSTestingUtils.getNewSecureFileSystem(file)
        sfs.touch("testFile1")
        val credentials = SFSCredentials(file,
            SecureFileSystem.generatePassword(SecureString("password123".toByteArray())))

        val model = TestModel(credentials, DefaultStorageRepositoryProvider())
        val view = TestView()
        val presenter = TestPresenter(model, view)
        presenter.start()

        assertEquals(listOf("testFile1"), view.sfsFileList)
        assertNull(view.error)
    }

    @Test
    fun `Copy Credentials is Resistant to Model Finalizer`() {
        //  Arrange
        val file = SFSTestingUtils.getTestFile("testFile_${System.nanoTime()}")
        val sfs = SFSTestingUtils.getNewSecureFileSystem(file)
        sfs.touch("testFile1")
        val credentials = SFSCredentials(file,
            SecureFileSystem.generatePassword(SecureString("password123".toByteArray())))

        val model = TestModel(credentials, DefaultStorageRepositoryProvider())
        val view = TestView()
        val presenter = TestPresenter(model, view)
        presenter.start()

        //  Act
        val copyCredentials = model.copyCredentials()
        presenter.close()

        //  Assert
        assertFalse(copyCredentials.finalized())
    }

    @Test
    fun `Can set Views Later`() {
        val file = SFSTestingUtils.getTestFile("testFile_${System.nanoTime()}")
        val sfs = SFSTestingUtils.getNewSecureFileSystem(file)
        sfs.touch("testFile1")
        val credentials = SFSCredentials(file,
            SecureFileSystem.generatePassword(SecureString("password123".toByteArray())))

        val model = TestModel(credentials, DefaultStorageRepositoryProvider())
        val view = TestView()
        val presenter = TestPresenterWithNoView(model)
        presenter.setView(view)
        presenter.start()

        assertEquals(listOf("testFile1"), view.sfsFileList)
        assertNull(view.error)
    }
}