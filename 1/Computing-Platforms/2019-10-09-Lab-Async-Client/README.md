# 9 October 2019

Using Eclipse write a java program as follows:

The program includes

N. 3 servers running in three separate threads. Each server is listening to a different TCP port (e.g., 8841, 8842, 8843) and activates a new thread at each connection request.

Once activated, the server wait some time, prints some information, wait some time prints some information and so forth (like in the programs seen in the past lectures).

After a couple of sends the server sends a string with a * characters to signal end of transmission.

It also includes an Asynchronous IO client that simultaneously connects to the 3 servers and receives the messages that they send.

Everything must be traced on the console output
