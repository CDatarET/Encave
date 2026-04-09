import pymysql

database = pymysql.connect(host = 'localhost', user = 'encave', password = 'password')
Cursor = database.cursor()

Cursor.execute("use Encave;")
Cursor.execute("""create table customers(
    custID int primary key, 
    firstName varchar(30), 
    lastName varchar(30))""")
database.commit()