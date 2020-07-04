

# sfs-mvp
Model-View-Presenter Pattern implementation for use in NewCryptoFramework-based Android Apps

# Architecture
![Architecture](documentation/res/SFS-sfs-mvp.svg)

# Usage Guide
## StorageRepository

The storage repository provides an API intentionally designed to feel like *nix commands.  For example ls() lists the files in the file system.

### A Note about File Types
File types are intended to filter files for use by a particular application.  So you will notice that commands like store() and ls/lsc give you the option to provide file types but none of the load commands do.  This is by design, as applications should first determine the files they will provide access to by file type and then simply load data from them.