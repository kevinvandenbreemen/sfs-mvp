package com.vandenbreemen.mobilesecurestoragemvp.wrapper.error

/**
 * An error occuring during interaction between a storage repository and underlying medium.
 */
class RepositoryRuntime(message: String): RuntimeException(message)
