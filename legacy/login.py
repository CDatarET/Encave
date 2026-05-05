import pymysql
import sys

database = pymysql.connect(host = 'localhost', user = 'encave', password = 'password')
Cursor = database.cursor()
Cursor.execute("use Encave;")

Cursor.execute("select password, firstName from Customer where prn = %s", sys.argv[1])
user = Cursor.fetchone()

if(user[0] == sys.argv[2]):
    print("Welcome " + user[1] + "!")
else:
    print("Wrong PRN or Password")