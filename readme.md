Simple in memory storage wich is persistent and transactional.

This project is a little addition on prevayler to simplify working with it. When using prevayler you have to model every change on your model into a command object and the command object will be persisted to disk. With Simple-persistence you can use a Dao model and stop worrying about command objects.

At the back-end we still use prevayler, prevayler is a way to persist your object model without the need for a database. All objects live in memory, any change is persisted to a transaction log, and after a crash or restart, this transaction log is read to restore the model in memory.

When you store object this way you will lose the references between the objects. In this project we added some functionality to keep the references intact and you can work with your object with a Dao object (which is probably familiar).

To get started:

[Getting-started]

More technical info:

[Technical-overview]

You can find the prevayler project here:

[Prevayler](https://github.com/jsampson/prevayler)
