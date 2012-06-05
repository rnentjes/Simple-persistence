Simple in-memory storage wich is persistent and transactional.

This project is a little addition on prevayler to simplify working with it. In prevayler you have to encapsulate every change on your model in a command object and the command object will be persisted to disk. With Simple-persistence you can use a Dao model and stop worrying about command objects.

At the back-end prevayler is still used, prevayler is a way to persist your object model without the need for a database. All objects live in memory, any change is persisted to a transaction log, and after a crash or restart, this transaction log is read to restore the model in memory.

To get started:

[Getting-started](https://github.com/rnentjes/Simple-persistence/wiki/Getting-started)

More technical info:

[Technical-overview](https://github.com/rnentjes/Simple-persistence/wiki/Technical-overview)

You can find the prevayler project here:

[Prevayler](https://github.com/jsampson/prevayler)
