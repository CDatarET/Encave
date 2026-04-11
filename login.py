import pymysql
import sys

database = pymysql.connect(host = 'localhost', user = 'encave', password = 'password')
Cursor = database.cursor()
Cursor.execute("use Encave;")

Cursor.execute("select password from Customer where prn = %s", sys.argv[1])

if(Cursor.fetchone()[0] == sys.argv[2]):
    print("SUCCESS")
else:
    print("Wrong PRN or Password")