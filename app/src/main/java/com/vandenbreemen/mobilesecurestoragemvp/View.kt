package com.vandenbreemen.mobilesecurestoragemvp

import com.vandenbreemen.mobilesecurestorage.message.ApplicationError

/**
 * Display/vieww
 * @author kevin
 */
interface View {

    /**
     * Display the given error
     */
    fun showError(error: ApplicationError)

}