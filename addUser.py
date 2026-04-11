import pymysql
import sys

database = pymysql.connect(host = 'localhost', user = 'encave', password = 'password')
Cursor = database.cursor()

Cursor.execute("use Encave;")
Cursor.execute("""insert into Customer values
(%s, %s, %s, %s, %s, %s, default);
""", (sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4], sys.argv[5], sys.argv[6]))

print("successfully added to table")
database.commit()