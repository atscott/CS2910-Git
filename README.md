<<<<<<< HEAD
Header is parsed, all you need to do is call your method at the
indicated TODO comment.  Also I changed it to a BufferedReader instead
of DataInputStream.  Also, with the enum MimeType, when you call
getContentType() on the HTTPHeader, it returns a MimeType instance, if
you want the actual ContentType such as "text/html" you have to call
.contentType on the result you get from getContentType() method.  Do
you also need the resourceId thing?  Change it if you do.

>>>>>>> Read the README
