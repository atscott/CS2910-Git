<<<<<<< HEAD
If you need to, email me at my msoe email aultj@msoe.edu to explain the enum thing.  It took me a little bit to get it.
=======
Header is parsed, all you need to do is call your method at the
indicated TODO comment.  Also I changed it to a BufferedReader instead
of DataInputStream.  Also, with the enum MimeType, when you call
getContentType() on the HTTPHeader, it returns a MimeType instance, if
you want the actual ContentType such as "text/html" you have to call
.contentType on the result you get from getContentType() method.  Do
you also need the resourceId thing?  Change it if you do.

email me at aultj@msoe.edu if you need anything clarified
>>>>>>> Read the README
