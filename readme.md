Simple in memory storage wich is persistent and transactional.

Prevayler is a way to persist your object model without the need for a database. All objects live in memory, any change is persisted to a transaction log, and after a crash or restart the transaction log is read to restore the model in memory.

This project is a little addition on prevayler to simplify working with it. When using prevayler you have to model every change on your model into a command object which will be persisted to disk. With Prevayler-Reference you can use a Dao model and stop worrying about command objects.

To get started:

[Getting-started](https://github.com/rnentjes/Prevayler-Reference/wiki/Getting-started)

More technical info:

[Technical-overview](https://github.com/rnentjes/Prevayler-Reference/wiki/Technical-overview)

You can find the prevayler project here:

[Prevayler](https://github.com/jsampson/prevayler)
